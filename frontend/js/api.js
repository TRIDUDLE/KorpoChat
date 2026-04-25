    // frontend/js/api.js

    export const api = {
        // Base URL for API requests (relative to the frontend server because nginx will proxy /api to the backend)
        BASE_URL: '/api',

        //handle login request to backend
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
        //handle fetching users for admin panel
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
        //handle adding user
        addUser: async (username,password,role,tags) => {
            try{
                const response = await fetch(`${api.BASE_URL}/users`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: username,
                        password: password,
                        role: role,
                        tags: tags
                    })
                });
                if (!response.ok) {
                    // Extract raw text from response
                    const errorText = await response.text();
                    let friendlyMessage = errorText;

                    try {
                        // Attempt to parse it as JSON (in case it's Spring Boot's default error)
                        const errorJson = JSON.parse(errorText);
                        
                        // If it's a default 500 error from Spring, make it user-friendly
                        if (errorJson.status === 500 && errorJson.error === "Internal Server Error") {
                            friendlyMessage = `Użytkownik o loginie "${username}" prawdopodobnie już istnieje.`;
                        } else if (errorJson.message) {
                            // If backend eventually sends a custom error object with a 'message' field
                            friendlyMessage = errorJson.message;
                        }
                    } catch (parseError) {
                        // If it's not JSON, keep the original errorText
                    }

                    throw new Error(friendlyMessage || 'Wystąpił nieznany błąd zapisu.');
                }
                
                return await response.json();

            } catch (error) {
                console.error("Server connection error when adding user:", error);
                throw error;
            }
        },
        //handle deleting user
        deleteUser: async (username) => {
            try{
                const response = await fetch(`${api.BASE_URL}/users/${username}`, {
                    method: 'DELETE',
                });
                if (!response.ok) {
                    throw new Error('Nie udało się usunąć użytkownika! Sprawdź logi serwera.');
                }
                return true; // Return true to indicate successful deletion

            } catch (error) {
                console.error("błąd serwera podczas usuwania użytkownika", error);
                throw error;
            }
        },

        //handle updating user password
        updateUser: async (username, newPassword) => {
            try{
                const response = await fetch(`${api.BASE_URL}/users/${username}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        password: newPassword
                    })
                });
                if (!response.ok) {
                    throw new Error('Nie udało się zaktualizować hasła! Sprawdź logi serwera.');
                }
                return await response.json();

            } catch (error) {
                console.error("błąd serwera podczas aktualizacji hasła", error);
                throw error;
            }
        },
        //handle updating user tags
        updateUserTags: async (username, tags) => {
            try {
                const response = await fetch(`${api.BASE_URL}/users/${username}/tags`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ tags: tags })
                });
                
                if (!response.ok) {
                    throw new Error('Nie udało się zaktualizować tagów! Sprawdź logi serwera.');
                }
            } catch (error) {
                console.error("błąd API podczas aktualizacji tagów:", error);
                throw error;
            }
        },

        //handle fetching chat history
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
        //handle sending new message to server
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