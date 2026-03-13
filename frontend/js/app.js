// frontend/js/app.js

// Wait for the DOM to be fully loaded before attaching events
document.addEventListener('DOMContentLoaded', () => {

    //DOM elements views

    const authView = document.getElementById('auth-view');
    const appView = document.getElementById('app-view');
    const adminView = document.getElementById('admin-view');
    
    const loginForm = document.getElementById('login-form');
    const logoutBtn = document.getElementById('logout-btn');
    const goToAdminBtn = document.getElementById('go-to-admin-btn');
    const backToChatBtn = document.getElementById('back-to-chat-btn');
    const adminControls = document.getElementById('admin-controls');
    const addUserForm = document.getElementById('add-user-form');

    // Handle Login 
    loginForm.addEventListener('submit', (e) => {
        e.preventDefault(); 

        const mockUserRole = 'ADMIN'; // MOCK: Replace with real role from auth response

        if (mockUserRole === 'ADMIN') {
            adminControls.classList.remove('hidden');
        } else {
            //Ensure the container remains hidden for standard users
            adminControls.classList.add('hidden');
        }

        // Hide auth view, show app view
        authView.classList.add('hidden');
        appView.classList.remove('hidden');
    });
    goToAdminBtn.addEventListener('click', () => {
        // Hide app view, show admin view
        appView.classList.add('hidden');
        adminView.classList.remove('hidden');
    });
    backToChatBtn.addEventListener('click', () => {
        // Hide admin view, show app view
        adminView.classList.add('hidden');
        appView.classList.remove('hidden');
    });

    // 4. Handle Logout
    logoutBtn.addEventListener('click', () => {
        // Hide app view, show auth view
        appView.classList.add('hidden');
        authView.classList.remove('hidden');
        adminView.classList.add('hidden');
        
        // Clear the form inputs
        loginForm.reset();
    });
});
