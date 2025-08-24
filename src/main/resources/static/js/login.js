document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');
    const errorMessageDiv = document.getElementById('error-message');

    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const formData = new FormData(loginForm);
            const data = Object.fromEntries(formData.entries());

            fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    // Try to get a meaningful error message from the server
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || '아이디 또는 비밀번호가 일치하지 않습니다.');
                    });
                }
            })
            .then(responseData => {
                // Save the token and redirect
                localStorage.setItem('jwt', responseData.token);
                window.location.href = '/';
            })
            .catch(error => {
                errorMessageDiv.textContent = error.message;
                errorMessageDiv.style.display = 'block';
                console.error('Login Error:', error);
            });
        });
    }
});