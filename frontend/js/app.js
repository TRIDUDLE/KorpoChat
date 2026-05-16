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

document.addEventListener('DOMContentLoaded', async () => {

    // DOM elements views
    const authView = document.getElementById('auth-view');
    const appView = document.getElementById('app-view');
    const adminView = document.getElementById('admin-view');
    const addUserForm = document.getElementById('add-user-form');
    //DOM chat elements
    const ChatForm = document.getElementById('chat-form');
    const messageInput = document.getElementById('message-input');
    const ChatMessages = document.getElementById('chat-messages');
    //DOM elements buttons and forms
    const loginForm = document.getElementById('login-form');
    const logoutBtn = document.getElementById('logout-btn');
    const goToAdminBtn = document.getElementById('go-to-admin-btn');
    const backToChatBtn = document.getElementById('back-to-chat-btn');
    const adminControls = document.getElementById('admin-controls');
    const sidebarToggleBtn = document.getElementById('sidebar-toggle-btn');
    const sidebarElement = document.querySelector('.sidebar');
    

    //state variables
    
    //MOCK current user is jsut a guest for TEST!
    let currentUser = null;
    //keep track of wwho is logged and what are their permisions
    let currentUserRole = null;
    // WebSocket Configuration
    let stompClient = null;
    // multi-channel support, we track the current channel ID
    let currentChannelId = null;
    let currentChannels = [];
    //track the current subscription for proper cleanup
    let currentSubscription = null;

    // cache for processed message IDs to prevent duplicates in UI
    const processedMessageIds = new Set();

    const connectWebSocket = () => {
        disconnectWebSocket();

        const socket = new SockJS('/api/ws');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        // 1. HEARTBEAT every 20 seconds to keep connection alive and detect drops
        stompClient.heartbeat.outgoing = 20000; // Send a ping every 20 seconds
        stompClient.heartbeat.incoming = 20000; // Expect a pong every 20 seconds

        stompClient.connect({}, (frame) => {
            console.log('✅ Połączono z WebSocket:', frame);

                currentSubscription = stompClient.subscribe(`/topic/channel/${currentChannelId}`,
                (messageOutput) => {
                    const newMessage = JSON.parse(messageOutput.body);
                    
                    if (newMessage.id && processedMessageIds.has(newMessage.id)) {
                        return; // duplicate message, ignore
                    }
                    
                    if (newMessage.id) {
                        processedMessageIds.add(newMessage.id);
                    }
                    renderSingleMessage(newMessage);
                    
                    if (ChatMessages) {
                        ChatMessages.scrollTop = ChatMessages.scrollHeight;
                    }
                },
                { id: 'public-chat-subscription' }
            );

        }, (error) => {
            console.error('❌ Błąd WebSocket lub rozłączono (Silent Drop).', error);
            stompClient = null;
            currentSubscription = null;

            // 2. AUTO-RECONNECT: If connection drops, attempt to reconnect after a delay (only if user is still logged in)
            if (currentUser) {
                console.log('🔄 Próba ponownego połączenia za 5 sekund...');
                setTimeout(() => {
                    connectWebSocket(); // delayed reconnect attempt
                }, 5000);
            }
        });
};


const disconnectWebSocket = () => {
    console.log("🔴 disconnectWebSocket wywołany – stompClient:", !!stompClient, "subscription:", !!currentSubscription);

    if (currentSubscription) {
        try {
            currentSubscription.unsubscribe();
            console.log("✅ Unsubscribed from /topic/public");
        } catch (e) {
            console.warn("Nie udało się unsubscribe", e);
        }
        currentSubscription = null;
    }

    if (stompClient !== null) {
        try {
            //added callback to confirm disconnection, but also catch any potential errors during disconnect
            stompClient.disconnect(() => {
                console.log("✅ WebSocket rozłączony pomyślnie");
            }, () => {
                console.warn("⚠️ Disconnect error");
            });
        } catch (e) {
            console.warn("Błąd podczas disconnect", e);
        }
        stompClient = null;
    }
};

async function loadChannelList() {
    const channelList = document.getElementById('channel-list');
    const currentChannelNameHeader = document.getElementById('current-channel-name');

    if (!channelList || !currentUser) {
        return;
    }

    try {
        const channels = await api.getUserChannels(currentUser);
        currentChannels = channels || [];
        channelList.innerHTML = '';

        if (currentChannels.length === 0) {
            channelList.innerHTML = '<li class="channel disabled">Brak kanałów</li>';
            currentChannelId = null;
            if (currentChannelNameHeader) currentChannelNameHeader.textContent = 'Brak kanałów';
            return;
        }

        const defaultChannel = currentChannels.find(channel => channel.name === '#główny') || currentChannels[0];
        currentChannelId = defaultChannel.id;
        if (currentChannelNameHeader) currentChannelNameHeader.textContent = defaultChannel.name;

        currentChannels.forEach((channel) => {
            const activeClass = channel.id === currentChannelId ? 'active' : '';
            channelList.innerHTML += `
                <li class="channel ${activeClass}" data-channel-id="${channel.id}">${channel.name}</li>
            `;
        });
    } catch (error) {
        console.error('Nie udało się załadować listy kanałów:', error);
        if (channelList) {
            channelList.innerHTML = '<li class="channel disabled" style="color:#b02a37;">Błąd ładowania kanałów</li>';
        }
    }
}

async function loadTagOptions() {
    const select = document.getElementById('new-tags');
    const tagList = document.getElementById('existing-tag-list');

    if (!select && !tagList) {
        return;
    }

    try {
        const tags = await api.getTags();

        if (select) {
            select.innerHTML = '';
            tags.forEach(tag => {
                const option = document.createElement('option');
                option.value = tag.name;
                option.textContent = tag.displayName || tag.name;
                select.appendChild(option);
            });
        }

        if (tagList) {
            tagList.innerHTML = tags.map(tag => `
                <span class="badge" style="background:#e2e3e5; color:#292b2c; padding: 6px 10px; border-radius: 999px; font-size: 0.9rem;">${tag.displayName || tag.name}</span>
            `).join('');
        }
    } catch (error) {
        console.error('Nie udało się pobrać listy tagów:', error);
        if (tagList) {
            tagList.innerHTML = '<span style="color:#b02a37;">Błąd pobierania tagów</span>';
        }
    }
}

async function ensureAdminTagData() {
    if (currentUserRole === 'ADMIN') {
        await loadTagOptions();
    }
}

async function loadChatHistory() {
        if (!ChatMessages) return;
        if (!currentChannelId) {
            ChatMessages.innerHTML = '<p style="color:#555;">Wybierz kanał, aby wyświetlić historię.</p>';
            return;
        }
        
        ChatMessages.innerHTML = '';
        
        try {
            const messages = await api.getMessages(currentChannelId);
            processedMessageIds.clear(); 
            
            messages.forEach(msg => {
                if (msg.id) processedMessageIds.add(msg.id);
                renderSingleMessage(msg);
            });

            ChatMessages.scrollTop = ChatMessages.scrollHeight;
        } catch (error) {
            console.error("Error loading chat history:", error);
            ChatMessages.innerHTML = '<p style="color:red;">Error loading messages.</p>';
        }
    }

    function renderSingleMessage(msg) {
        if (!ChatMessages) return;

        const dateObj = new Date(msg.timestamp);
        const timeString = dateObj.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        let messageHtml = '';

        // Handle System Notifications (JOIN / LEAVE)
        if (msg.type && msg.type !== 'CHAT') {
            const systemAction = msg.type === 'JOIN' ? 'dołączył do czatu' : 'opuścił czat';
            messageHtml = `
                <div class="system-message">
                    <span class="content">
                        <strong>${msg.sender}</strong> ${systemAction} (${timeString})
                    </span>
                </div>
            `;
        } 
        // Handle Standard Chat Messages
        else {
            const isOwnMessage = currentUser && msg.sender === currentUser;
            const messageClass = isOwnMessage ? 'message own' : 'message';
            messageHtml = `
                <div class="${messageClass}">
                    <span class="sender">${msg.sender}</span> 
                    <span class="timestamp">${timeString}</span>
                    <p class="text">${msg.text}</p>
                </div>
            `;
        }

        ChatMessages.innerHTML += messageHtml;
    }

    async function renderAdminTable() {
        const tableBody = document.getElementById('user-table-body');
        if (!tableBody) return;

        tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center;">Ładowanie danych...</td></tr>`;

        try {
            const users = await api.getUsers();
            tableBody.innerHTML = '';

            users.forEach(user => {
                const dotColor = user.status === 'ONLINE' ? 'green' : 'gray';
                
                tableBody.innerHTML += `
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 12px 15px;">${user.username}</td>
                        <td style="padding: 12px 15px;"><strong>${user.role}</strong></td>
                        <td style="padding: 12px 15px;">
                            <span class="badge" style="background: #e2e3e5; color: #41464b;">${user.tags || 'Brak'}</span>
                        </td>
                        <td style="padding: 12px 15px;">
                            <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background-color:${dotColor}; margin-right:5px;"></span>
                            ${user.status}
                        </td>
                        <td style="padding: 12px 15px;">${formatTimeAgo(user.lastSeen)}</td>
                        <td style="padding: 12px 15px; display: flex; gap: 8px; flex-wrap: wrap;">
                            <button class="btn-small btn-edit" data-username="${user.username}">Zmień Hasło</button>
                            <button class="btn-small btn-edit btn-edit-tags" data-username="${user.username}" data-current-tags="${user.tags || ''}">Zmień Tagi</button>
                            <button class="btn-small btn-delete btn-danger" data-username="${user.username}">Usuń</button>
                        </td>
                    </tr>
                `;
            });
        } catch (error) {
            tableBody.innerHTML = `<tr><td colspan="5" style="color: red; text-align: center;">Error loading data</td></tr>`;
        }
    }

    // ==========================================
    // 2. SESSION RESTORE LOGIC (On Page Load)
    // ==========================================
    
    const savedUser = localStorage.getItem('korpo_user');
    const savedRole = localStorage.getItem('korpo_role');

    if (savedUser && savedRole) {
        // Hydrate state
        currentUser = savedUser;
        currentUserRole = savedRole;

        // Secure UI routing
        authView.classList.add('hidden');
        appView.classList.remove('hidden');

        if (currentUserRole === 'ADMIN') {
            adminControls.classList.remove('hidden');
        }

        // Initialize App
        await loadChannelList();
        await ensureAdminTagData();
        await loadChatHistory();
        connectWebSocket();
    } else {
        // Ensure user stays on login screen if no session
        authView.classList.remove('hidden');
        appView.classList.add('hidden');
    }
    // ==========================================
    // CHANNEL SWITCHING LOGIC
    // ==========================================
    const channelList = document.getElementById('channel-list');
    const currentChannelNameHeader = document.getElementById('current-channel-name');

    if (channelList) {
        channelList.addEventListener('click', (e) => {
            const clickedChannel = e.target.closest('.channel');
            if (!clickedChannel) return; //ignore clicks outside of channel items

            // UI change: Highlight the selected channel
            document.querySelectorAll('.channel').forEach(c => c.classList.remove('active'));
            clickedChannel.classList.add('active');

            // Assuming the channel name is the text content of the clicked element
            currentChannelNameHeader.textContent = clickedChannel.textContent;

            // get the channel ID from data attribute
            const newChannelId = clickedChannel.getAttribute('data-channel-id');

            // If the user clicks on the already active channel, do nothing
            if (newChannelId === currentChannelId) return;

            console.log(`Przełączanie z ${currentChannelId} na ${newChannelId}`);
            currentChannelId = newChannelId;

            // channel switch logic
            switchChannel();
        });
    }

    async function switchChannel() {
        // A) unsubscribe from the old channel to stop receiving messages
        if (currentSubscription) {
            currentSubscription.unsubscribe();
            console.log("Odpięto starą subskrypcję.");
        }

        // B) clear chat window and reset processed message IDs to avoid showing old messages as new
        ChatMessages.innerHTML = '';
        processedMessageIds.clear();

        // C) Load new channel history for NEW channel
        await loadChatHistory();

        // D) Subscribe to the new channel 
        if (stompClient && stompClient.connected) {
            currentSubscription = stompClient.subscribe(`/topic/channel/${currentChannelId}`, 
                (messageOutput) => {
                    const newMessage = JSON.parse(messageOutput.body);
                    
                    if (newMessage.id && processedMessageIds.has(newMessage.id)) return;
                    if (newMessage.id) processedMessageIds.add(newMessage.id);
                    
                    renderSingleMessage(newMessage);
                    ChatMessages.scrollTop = ChatMessages.scrollHeight;
                },
                { id: `sub-${currentChannelId}` }
            );
            console.log(`Subskrybowano /topic/channel/${currentChannelId}`);
        }
    }

    // ==========================================
    // EVENT LISTENERS
    // ==========================================

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault(); 
        
        const usernameInput = document.getElementById('login-username').value;
        const passwordInput = document.getElementById('login-password').value;

        try {
            const authData = await api.login(usernameInput, passwordInput);

            // SECURITY/SESSION: Save user identity
            localStorage.setItem('korpo_user', usernameInput);
            localStorage.setItem('korpo_role', authData.role);

            currentUser = usernameInput; 
            currentUserRole = authData.role; 
            
            if (authData.role === 'ADMIN') {
                adminControls.classList.remove('hidden');
            } else {
                adminControls.classList.add('hidden');
            }

            authView.classList.add('hidden');
            appView.classList.remove('hidden');

            document.title = "KorpoChat";
            await loadChannelList();
            await ensureAdminTagData();
            await loadChatHistory();
            connectWebSocket();         
        
        } catch (error) {
            alert("Error logging in!");
            console.error("Login error:", error);
        }
    });

    if (ChatForm) {
        ChatForm.addEventListener('submit', async (e) => {
            e.preventDefault(); 
            const text = messageInput.value.trim(); 

            if (text.length > 0) {
                try {
                    // Send via WebSocket (STOMP) for instant delivery
                    if (stompClient && stompClient.connected) {
                        const chatMessage = {
                            sender: currentUser,
                            text: text,
                            channelId: currentChannelId,
                            type: 'CHAT'
                        };
                        // Send serialized payload to Spring application prefix
                        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
                    } else {
                        // Trigger HTTP REST API if connection drops
                        await api.sendMessage(currentUser, text, currentChannelId);
                    }
                    
                    messageInput.value = '';
                } catch (error) {
                    console.error("Failed to route message:", error);
                }
            }
        });
    }
    const userTableBody = document.getElementById('user-table-body');
    if (userTableBody) {
        userTableBody.addEventListener('click', async (e) => {
            const deleteBtn = e.target.closest('.btn-delete');
            const editTagsBtn = e.target.closest('.btn-edit-tags');
            const editPassBtn = e.target.closest('.btn-edit:not(.btn-edit-tags)');

            if (deleteBtn) {
                const targetUsername = deleteBtn.getAttribute('data-username');
                if (confirm(`Czy na pewno chcesz usunąć użytkownika ${targetUsername}?`)) {
                    try {
                        await api.deleteUser(targetUsername);
                        alert(`Użytkownik ${targetUsername} został usunięty.`);
                        await renderAdminTable(); 
                    } catch (error) {
                        alert("Nie udało się usunąć użytkownika. Sprawdź logi serwera.");
                    }
                }
            }
            else if (editTagsBtn) {
                const targetUsername = editTagsBtn.getAttribute('data-username');
                const currentTags = editTagsBtn.getAttribute('data-current-tags') || '';
                const newTags = prompt(`Tagi dla ${targetUsername} (oddzielone przecinkiem):`, currentTags);
                
                if (newTags !== null) {
                    const tagList = newTags.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0);
                    try {
                        await api.updateUserTags(targetUsername, tagList);
                        alert(`Tagi dla ${targetUsername} zostały zaktualizowane.`);
                        await renderAdminTable(); 
                    } catch (error) {
                        alert("Nie udało się zaktualizować tagów. Sprawdź logi serwera.");
                    }
                }
            }
            else if (editPassBtn) {
                const targetUsername = editPassBtn.getAttribute('data-username');
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
            const selectedTags = Array.from(document.getElementById('new-tags').selectedOptions)
                .map(option => option.value)
                .filter(tag => tag && tag.trim().length > 0);

            try{
                await api.addUser(newUsernameInput, newPasswordInput, newRoleInput, selectedTags);
                addUserForm.reset();
                await renderAdminTable();
                await loadTagOptions();
                alert(`Dodano użytkownika: ${newUsernameInput} \nz rolą ${newRoleInput} \ni tagami ${selectedTags.join(', ')}`);
            }catch(error){
                alert(`Błąd: ${error.message}`);
            }
        });
    }

    const createTagBtn = document.getElementById('create-tag-btn');
    if (createTagBtn) {
        createTagBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            const tagNameInput = document.getElementById('new-tag-name');
            if (!tagNameInput) return;

            const newTagName = tagNameInput.value.trim();
            if (!newTagName) {
                alert('Wprowadź nazwę taga.');
                return;
            }

            try {
                await api.createTag(newTagName);
                tagNameInput.value = '';
                await loadTagOptions();
                await renderAdminTable();
                alert(`Tag "${newTagName}" został utworzony.`);
            } catch (error) {
                alert(`Nie udało się utworzyć taga: ${error.message}`);
            }
        });
    }

    goToAdminBtn.addEventListener('click', async () => {
        if(currentUserRole !== 'ADMIN'){
            alert("ZABRONIONY DOSTĘP! Nie masz uprawnień do tej sekcji.");
            return; 
        }
        appView.classList.add('hidden');
        adminView.classList.remove('hidden');
        document.title = "KorpoChat - Admin Panel";
        await renderAdminTable();
        await loadTagOptions();
    });

    backToChatBtn.addEventListener('click', () => {
        adminView.classList.add('hidden');
        appView.classList.remove('hidden');
        document.title = "KorpoChat";
    });

    logoutBtn.addEventListener('click', async () => {
        if(currentUser){
            await api.logout(currentUser); 
        }

        // SESSION CLEAR
        localStorage.removeItem('korpo_user');
        localStorage.removeItem('korpo_role');

        currentUser = null; 
        currentUserRole = null; 
        disconnectWebSocket(); 

        appView.classList.add('hidden');
        authView.classList.remove('hidden');
        adminView.classList.add('hidden');
        loginForm.reset();
        document.title = "KorpoChat - Logowanie";
    });

    if (sidebarToggleBtn && sidebarElement) {
        sidebarToggleBtn.addEventListener('click', () => {
            sidebarElement.classList.toggle('closed');
        });
    }
});