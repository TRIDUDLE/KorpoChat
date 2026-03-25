    // frontend/js/api.js

    export const api = {
        // Base URL for API requests (relative to the frontend server because nginx will proxy /api to the backend)
        BASE_URL: '/api',

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
            try{
                const response = await fetch(`${api.BASE_URL}/users`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: username,
                        password: password,
                        role: role
                    })
                });
                if (!response.ok) {
                    throw new Error('Failed to add user!');
                }
                return await response.json();

            } catch (error) {
                console.error("server connection error when adding user:", error);
                throw error;
            }
        },
        getMessages: async () => {
            try {
                const response = await fetch(`${api.BASE_URL}/messages`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                if (!response.ok) {
                    throw new Error('Failed to fetch chat history from server!');
                }
                return await response.json();
            } catch(error){
                console.error("server connection error:", error);
                throw(error);
            }
        },
        sendMessage: async (sender, text) => {
            try {
                const response = await fetch(`${api.BASE_URL}/messages`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        sender: sender,
                        text: text
                    })
                });

                if (!response.ok) {
                    throw new Error('Failed to send message!');
                }

                return await response.json();

            } catch (error) {
                console.error("server connection error when sending message:", error);
                throw error;
            }
        },
        // handle logout
        logout: async (username) => {
            try{
                const response = await fetch(`${api.BASE_URL}/auth/logout`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'},
                    body: JSON.stringify({ username: username })
                });
                if (!response.ok) {
                    console.warn('Logout request failed, but proceeding with client-side logout.');
                }
            }catch(error){
                console.error("server connection error when logging out:", error);
            }
        }
    };