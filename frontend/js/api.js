    // frontend/js/api.js

    export const api = {
        BASE_URL: 'http://localhost:8080/api',

        login: async (username, password) => {
            try {
                //sending true HTTP POST request to Spring Boot server
                const response = await fetch(`${api.BASE_URL}/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ 
                        username: username, 
                        password: password 
                    }) //login request body
                });

                // If response is not 200 OK, throw an error to be caught in app.js
                if (!response.ok) {
                    throw new Error('Invalid username or password');
                }

                //if 200 OK, parse the JSON response
                const data = await response.json();
                
                //app.js awaits object with "role" field
                return data; 

            } catch (error) {
                console.error("server connection error:", error);
                throw error; 
            }
        },

        getUsers: async () => {
            try{
                const response = await fetch(`${api.BASE_URL}/users`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch users from server!');
                }

                const data = await response.json();
                return data; 
            }
            catch(error){
                console.error("server connection error:", error);
                throw error; 
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
                    text: 'to jest wiadomosc testowa',
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