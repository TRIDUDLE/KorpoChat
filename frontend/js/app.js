// frontend/js/app.js
import { api } from './api.js';

// Helper function to format "last seen" time in a human-readable way
const formatTimeAgo = (dateString) => {
        if(!dateString || dateString === 'null') return 'N/A';

        const date = new Date(dateString);
        const now = new Date();

        const diffInSeconds = Math.floor((now - date) / 1000);
        if(diffInSeconds < 60) return `${diffInSeconds} sekund temu`;

        const diffInMinutes = Math.floor(diffInSeconds / 60);
        if(diffInMinutes < 60) return `${diffInMinutes} minut temu`;

        const diffInHours = Math.floor(diffInMinutes / 60);
        if(diffInHours < 24) return `${diffInHours} godzin temu`;

        const diffInDays = Math.floor(diffInHours / 24);
        if(diffInDays == 1) return `wczoraj`;

        return `${diffInDays} dni temu`;
    }

document.addEventListener('DOMContentLoaded', () => {

    // DOM elements views
    const authView = document.getElementById('auth-view');
    const appView = document.getElementById('app-view');
    const adminView = document.getElementById('admin-view');
    const addUserForm = document.getElementById('add-user-form');
    //DOM chat elements
    const ChatForm = document.getElementById('chat-form');
    const messageInput = document.getElementById('message-input');
    const ChatMessages = document.getElementById('chat-messages');

    //MOCK current user is jsut a guest for TEST!
    let currentUser = null;
    //keep track of wwho is logged and what are their permisions
    let currentUserRole = null;

    // WebSocket Configuration
    let stompClient = null;

    const connectWebSocket = () => {
        // Guard clause: Prevent multiple active WebSocket connections
        if (stompClient !== null && stompClient.connected) {
            console.warn('Websocket jest już połączony. Ignorowanie kolejnej próby połączenia.');
            return; 
        }
        // Connect via relative path so Nginx handles the routing correctly
        const socket = new SockJS('/api/ws'); 
        stompClient = Stomp.over(socket);

        // Disable console debug spam
        stompClient.debug = null; 

        stompClient.connect({}, (frame) => {
            console.log('Połączono z kanałem WebSocket: ' + frame);

            // Subscribe to the public topic
            stompClient.subscribe('/topic/public', (messageOutput) => {
                const newMessage = JSON.parse(messageOutput.body);
                
                // Render the incoming message
                renderSingleMessage(newMessage);
                
                // Auto-scroll to the bottom
                if (ChatMessages) {
                    ChatMessages.scrollTop = ChatMessages.scrollHeight;
                }
            });
        }, (error) => {
            console.error('Błąd WebSocket:', error);
            stompClient = null; // Reset client state on error    
        });
    };

    const disconnectWebSocket = () => {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Disconnected from WebSocket");
    };    
    // DOM elements buttons & forms
    const loginForm = document.getElementById('login-form');
    const logoutBtn = document.getElementById('logout-btn');
    const goToAdminBtn = document.getElementById('go-to-admin-btn');
    const backToChatBtn = document.getElementById('back-to-chat-btn');
    const adminControls = document.getElementById('admin-controls');
    // DOM elements for sidebar toggle
    const sidebarToggleBtn = document.getElementById('sidebar-toggle-btn');
    const sidebarElement = document.querySelector('.sidebar');


    // Handle Login 
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault(); 
        
        const usernameInput = document.getElementById('login-username').value;
        const passwordInput = document.getElementById('login-password').value;

        try {
            // Call API login and get auth data (token + role)
            const authData = await api.login(usernameInput, passwordInput);

            currentUser = usernameInput; // Set current user (for chat purposes)
            currentUserRole = authData.role; // Set current user role
            
            // role-based UI control
            if (authData.role === 'ADMIN') {
                adminControls.classList.remove('hidden');
            } else {
                adminControls.classList.add('hidden');
            }

            // main app view
            authView.classList.add('hidden');
            appView.classList.remove('hidden');

            document.title = "KorpoChat";
            await loadChatHistory(); 
            
            // Connect to WebSocket for real-time updates after successful login
            connectWebSocket();         
        
        } catch (error) {
            alert("Error logging in!");
            console.error("Login error:", error);
        }
    });
    //chat form logic
    if (ChatForm) {
        ChatForm.addEventListener('submit', async (e) => {
            e.preventDefault(); // Prevent page reload
            
            // trimming white spaces from message
            const text = messageInput.value.trim(); 

            // Prevent sending empty messages
            if (text.length > 0) {
                try {
                    // Send message via HTTP API
                    await api.sendMessage(currentUser, text);
                    
                    // Clear the input field instantly
                    messageInput.value = '';
                } catch (error) {
                    console.error("Failed to send message:", error);
                }
            }
        });
    }
    // Event delegation for dynamically generated table buttons
    const userTableBody = document.getElementById('user-table-body');
    if (userTableBody) {
        userTableBody.addEventListener('click', async (e) => {
            const deleteBtn = e.target.closest('.btn-delete');
            const editTagsBtn = e.target.closest('.btn-edit-tags');
            const editPassBtn = e.target.closest('.btn-edit:not(.btn-edit-tags)');

            // Handle DELETE action
            if (deleteBtn) {
                const targetUsername = deleteBtn.getAttribute('data-username');

                if (confirm(`Czy na pewno chcesz usunąć użytkownika ${targetUsername}?`)) {
                    try {
                        await api.deleteUser(targetUsername);
                        alert(`Użytkownik ${targetUsername} został usunięty.`);
                        await renderAdminTable(); // Refresh table after deletion
                    } catch (error) {
                        alert("Nie udało się usunąć użytkownika. Sprawdź logi serwera.");
                    }
                }
            }
            else if (editTagsBtn) {
                const targetUsername = editTagsBtn.getAttribute('data-username');
                const currentTags = editTagsBtn.getAttribute('data-current-tags') || '';
                
                // Prompt user for new tags, pre-filled with current tags
                const newTags = prompt(`Tagi dla ${targetUsername} (oddzielone przecinkiem):`, currentTags);
                
                // Proceed only if user clicked "OK" (newTags is not null)
                if (newTags !== null) {
                    try {
                        // Send update request to backend
                        await api.updateUserTags(targetUsername, newTags.trim());
                        alert(`Tagi dla ${targetUsername} zostały zaktualizowane.`);
                        await renderAdminTable(); // Refresh the table
                    } catch (error) {
                        alert("Nie udało się zaktualizować tagów. Sprawdź logi serwera.");
                    }
                }
            }
            //PASSWORD EDIT -
            else if (editPassBtn) {
                const targetUsername = editPassBtn.getAttribute('data-username');
                
                // Prompt to change password
                const newPassword = prompt(`Wprowadź nowe hasło dla ${targetUsername} (pozostaw puste, aby anulować):`);
                
                if (newPassword && newPassword.trim() !== "") {
                    try {
                        await api.updateUser(targetUsername, newPassword);
                        alert(`Hasło dla ${targetUsername} zostało zaktualizowane.`);
                    } catch (error) {
                        alert("Nie udało się zaktualizować hasła. Sprawdź logi serwera.");
                    }
                }
            };
        });
    }
    if (addUserForm) {
        addUserForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const newUsernameInput = document.getElementById('new-username').value;
            const newPasswordInput = document.getElementById('new-password').value;
            const newRoleInput = document.getElementById('new-role').value;
            const newTagsInput = document.getElementById('new-tags').value; // NEW TAGS INPUT

            try{
                //pass data to API
                await api.addUser(newUsernameInput,newPasswordInput, newRoleInput, newTagsInput);

                //clearing fields
                addUserForm.reset();
            
                await renderAdminTable();
                
                alert(`Dodano użytkownika: ${newUsernameInput} \nz rolą ${newRoleInput} \ni tagami ${newTagsInput}`);
                console.log(`Dodano użytkownika: ${newUsernameInput} z rolą ${newRoleInput} i tagami ${newTagsInput}`);
            }catch(error){
                alert(`Błąd: ${error.message}`);
            }
        });
    }

    // Handle navigation to Admin Panel + data loading
    goToAdminBtn.addEventListener('click', async () => {
        
        //security check - only admins can access
        if(currentUserRole !== 'ADMIN'){
            alert("ZABRONIONY DOSTĘP! Nie masz uprawnień do tej sekcji.");
            console.warn("Nie ma dostępu dla hackeruw");
            return; //halt
        }
        
        appView.classList.add('hidden');
        adminView.classList.remove('hidden');

        document.title = "KorpoChat - Admin Panel";
        // user data loading
        await renderAdminTable();
    });

    backToChatBtn.addEventListener('click', () => {
        adminView.classList.add('hidden');
        appView.classList.remove('hidden');
        document.title = "KorpoChat";
    });


    // Handle Logout
    logoutBtn.addEventListener('click', async () => {
        if(currentUser){
            await api.logout(currentUser); // Inform server about logout
        }

        //reset all user-related data and states
        currentUser = null; 
        currentUserRole = null; 

        // Disconnect from WebSocket to stop receiving messages
        disconnectWebSocket(); 

        // Show login view, hide others
        appView.classList.add('hidden');
        authView.classList.remove('hidden');
        adminView.classList.add('hidden');
        loginForm.reset();
        document.title = "KorpoChat - Logowanie";
    });

    // Handle Sidebar Toggle (Hamburger Menu)
    if (sidebarToggleBtn && sidebarElement) {
        sidebarToggleBtn.addEventListener('click', () => {
            // Toggle the 'closed' class to hide/show the sidebar globally
            sidebarElement.classList.toggle('closed');
        });
    }
    
    // --- render functions ---
    async function renderAdminTable() {
        const tableBody = document.getElementById('user-table-body');
        if (!tableBody) return;

        // data loading state
        tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center;">Ładowanie danych...</td></tr>`;

        try {
            // take data from API 
            const users = await api.getUsers();
            
            // clear old data
            tableBody.innerHTML = '';

            // render new rows with user data
            users.forEach(user => {
                const dotColor = user.status === 'ONLINE' ? 'green' : 'gray';
                
                tableBody.innerHTML += `
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 10px;">${user.username}</td>
                        <td style="padding: 10px;"><strong>${user.role}</strong></td>
                        <td style="padding: 10px;">
                            <span class="badge" style="background: #e2e3e5; color: #41464b;">${user.tags || 'Brak'}</span>
                        </td>
                        <td style="padding: 10px;">
                            <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background-color:${dotColor}; margin-right:5px;"></span>
                            ${user.status}
                        </td>
                        <td style="padding: 10px;">${formatTimeAgo(user.lastSeen)}</td>
                        <td style="padding: 10px;"><button class="btn-small btn-edit" data-username="${user.username}">Zmień Hasło</button></td>
                        <td style="padding: 10px;"><button class="btn-small btn-edit btn-edit-tags" data-username="${user.username}" data-current-tags="${user.tags || ''}" style="margin-left: 5px;">Zmień Tagi</button></td>
                        <td style="padding: 10px;"><button class="btn-small btn-delete btn-danger" data-username="${user.username}">Usuń</button></td>
                    </tr>
                `;
            });
        } catch (error) {
            tableBody.innerHTML = `<tr><td colspan="5" style="color: red; text-align: center;">Error loading data</td></tr>`;
        }
    }
    async function loadChatHistory() {
        if (!ChatMessages) return;
        
        // Clear current HTML
        ChatMessages.innerHTML = ''; 
        
        try {
            const messages = await api.getMessages();

            // Render each message from storage
            messages.forEach(msg => renderSingleMessage(msg));

            // Auto-scroll to the newest message
            ChatMessages.scrollTop = ChatMessages.scrollHeight;
        } catch (error) {
            console.error("Error loading chat history:", error);
            ChatMessages.innerHTML = '<p style="color:red;">Error loading messages.</p>';
        }
    }
    function renderSingleMessage(msg) {
        if (!ChatMessages) return;

        // Extract hour and minute from the ISO string
        const dateObj = new Date(msg.timestamp);
        const timeString = dateObj.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        // Build HTML structure for the message
        const messageHtml = `
            <div class="message" style="margin-bottom: 10px; padding: 10px; background: #f1f1f1; border-radius: 5px;">
                <strong style="color: #3b82f6;">${msg.sender}</strong> 
                <span style="font-size: 0.8rem; color: #888; margin-left: 10px;">${timeString}</span>
                <p style="margin-top: 5px; word-wrap: break-word;">${msg.text}</p>
            </div>
        `;

        // Inject into the DOM
        ChatMessages.innerHTML += messageHtml;
    }
});