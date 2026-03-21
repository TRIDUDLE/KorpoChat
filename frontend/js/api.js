// frontend/js/api.js

export const api = {
    BASE_URL: 'http://localhost:8080/api',

    login: async (username, password) => {
        // Fetch current users from local storage MOCK database
        const users = await api.getUsers();

        // Search for a user matching BOTH username and password
        const foundUser = users.find(u => u.username === username && u.password === password);

        // Simulate network delay and return outcome
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (foundUser) {
                    // G: Return mock token and the actual role of the found user
                    resolve({ token: 'mock-jwt-token', role: foundUser.role });
                } else {
                    // fail: User not found or wrong password
                    reject(new Error('Invalid credentials'));
                }
            }, 300);
        });
    },

    getUsers: async () => {
        //MOCK
        const storedUsers = localStorage.getItem('users');
        if (storedUsers) {
            return JSON.parse(storedUsers);
        }else {
            const defaultUsers = [
                { id: 1, username: 'admin',password:'admin', role: 'ADMIN', status: 'ONLINE', last_seen: 'Teraz' },
                { id: 2, username: 'bartosz',password:'elza', role: 'USER', status: 'OFFLINE', last_seen: '15 min temu' },
                { id: 3, username: 'marcel',password:'luna', role: 'USER', status: 'ONLINE', last_seen: 'Teraz' },
            ];
            localStorage.setItem('default-users', JSON.stringify(defaultUsers));
            return defaultUsers;
        }    
    
    },
    addUser: async (username,password,role) => {
        const users = await api.getUsers();
        const newUser = {
            id: users.length + 1, // Mock ID generation
            username: username,
            password: password, //plaintext for mock frontend
            role: role,
            status: 'OFFLINE', //default status for new users
            last_seen: 'N/A'
        };
        users.push(newUser);
        localStorage.setItem('users', JSON.stringify(users)); // Mock persistence

        return newUser;
    },
    getMessages: async () => {
        //MOCK
        const storedMessages= localStorage.getItem('chat_messages');
        if (storedMessages){
            return JSON.parse(storedMessages);
        }else{
            //default messge if emty
            const defaultMessages=[
                {
                id: Date.now(),
                sender: 'system',
                text: 'to jest wiadomosc testowa!',
                timestamp: new Date().toISOString()
                }
            ];
            localStorage.setItem('chat_messages', JSON.stringify(defaultMessages));
            return defaultMessages;
        }
    },
    sendMessage: async (sender, text) => {
        // Retrieve existing messages
        const messages = await api.getMessages();
        
        // Create new message object
        const newMessage = {
            id: Date.now(), // MOCK ID
            sender: sender,
            text: text,
            timestamp: new Date().toISOString()
        };
        
        // Append and save to storage
        messages.push(newMessage);
        localStorage.setItem('chat_messages', JSON.stringify(messages));
        
        return newMessage;
    }
};