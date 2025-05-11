import React, { useState } from 'react';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

   const handleSubmit = async (e) => {
    e.preventDefault();
    try {
        const response = await fetch('http://localhost:8081/auth/login', { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        const text = await response.text();
        let result;

        try {
            result = JSON.parse(text);
        } catch {
            result = { message: text };
        }

        if (response.ok) {
            alert(result.message || 'Login successful');
        } else {
            setErrorMessage(result.message || 'Invalid credentials');
        }
    } catch (error) {
        setErrorMessage('An error occurred. Please try again later.');
    }
};



    return (
        <div className="d-flex justify-content-center align-items-center" style={{
            height: '100vh',
            width: '100vw',
            position: 'fixed',
            top: 0,
            left: 0,
            backgroundColor: 'var(--background-color)', 
        }}>
            <div className="container p-4 border rounded shadow" style={{
                maxWidth: '400px',
                backgroundColor: 'white', 
            }}>
                <h2 className="text-center" style={{ color: 'var(--primary-color)' }}>Login</h2>
                {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input
                            type="email"
                            id="email"
                            className="form-control"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100" style={{
                        backgroundColor: 'var(--primary-color)',
                        borderColor: 'var(--primary-color)',
                    }}>Login</button>
                </form>
            </div>
        </div>
    );
};

export default Login;
