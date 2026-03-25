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
    let currentUser ='guest'
    //keep track of wwho is logged and what are their permisions
    let currentUserRole = 'GUEST'; // default role

    // API calls
    let currentMessageCount = 0; 
    let pollingInterval = null;
    
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

            //JAVASCRIPT POLLING - start polling for new messages after successful login
            startChatPolling();
            
            

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
                    // Send message Via API api.js
                    const savedMessage = await api.sendMessage(currentUser, text);
                    
                    // show the new message
                    renderSingleMessage(savedMessage);
                    currentMessageCount++;
                    
                    // Clear the input field
                    messageInput.value = '';
                    
                    // autoscrolling to the bottom
                    ChatMessages.scrollTop = ChatMessages.scrollHeight;
                } catch (error) {
                    console.error("Failed to send message:", error);
                }
            }
        });
    }
    if (addUserForm) {
        addUserForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const newUsernameInput = document.getElementById('new-username').value;
            const newPasswordInput = document.getElementById('new-password').value;
            const newRoleInput = document.getElementById('new-role').value;

            try{
                //pass data to API
                await api.addUser(newUsernameInput,newPasswordInput, newRoleInput);

                //clearing fields
                addUserForm.reset();

                await renderAdminTable();

                console.log(`Added user: ${newUsernameInput} with role ${newRoleInput}`);
            }catch(error){
                console.error("ERROR when adding user:", error);
            }
        });
    }

    // Handle navigation to Admin Panel + data loading
    goToAdminBtn.addEventListener('click', async () => {
        
        //security check - only admins can access
        if(currentUserRole !== 'ADMIN'){
            alert("Access denied! no snooping! Admins only.");
            console.warn("No access! bad try dirty hacker");
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
        if(currentUser !== 'guest'){
            await api.logout(currentUser); // Inform server about logout
        }

        currentUser = 'guest'; // Reset to guest
        currentUserRole = 'GUEST'; // Reset role
        currentMessageCount = 0; // RESET MESSAGE COUNTER
        stopChatPolling();       // stop polling for new messages when logged out

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

            // Wygeneruj wiersze
            users.forEach(user => {
                const dotColor = user.status === 'ONLINE' ? 'green' : 'gray';
                
                tableBody.innerHTML += `
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 10px;">${user.username}</td>
                        <td style="padding: 10px;"><strong>${user.role}</strong></td>
                        <td style="padding: 10px;">
                            <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background-color:${dotColor}; margin-right:5px;"></span>
                            ${user.status}
                        </td>
                        <td style="padding: 10px;">${formatTimeAgo(user.lastSeen)}</td>
                        <td style="padding: 10px;"><button class="btn-small">Edytuj</button></td>
                        <td style="padding: 10px;"><button class="btn-small">Usuń</button></td>
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
            currentMessageCount = messages.length; // set counter to current messages            
            // Auto-scroll to the newest message
            messages.scrollTop = messages.scrollHeight;
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
    // silent polling function to check for new messages every 1 second
    async function startChatPolling() {
        // If there's already a polling interval running, clear it before starting a new one
        if (pollingInterval) clearInterval(pollingInterval);

        pollingInterval = setInterval(async () => {
            try {
                const messages = await api.getMessages();
                
                // check if there are new messages by comparing the count of messages we have with the count from the server
                if (messages.length > currentMessageCount) {
                    
                    // take only new messages that we haven't rendered yet
                    const newMessages = messages.slice(currentMessageCount);
                    
                    // Render only new messages
                    newMessages.forEach(msg => renderSingleMessage(msg));
                    
                    // update counter
                    currentMessageCount = messages.length;
                    
                    // scroll to bottom
                    ChatMessages.scrollTop = ChatMessages.scrollHeight;
                }
            } catch (error) {
                console.error("Polling error (cichy błąd):", error);
            }
        }, 1000); // poll every 1 second
    }

    function stopChatPolling() {
        if (pollingInterval) {
            clearInterval(pollingInterval);
            pollingInterval = null;
        }
    }
});