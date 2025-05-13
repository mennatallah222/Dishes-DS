import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [name, setName] = useState('');
    const navigate = useNavigate();
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setErrorMessage('Passwords do not match');
            return;
        }

        try {
            const response = await fetch('http://localhost:8081/api/customers/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, email, password }),
            });

            const text = await response.text();
            let result;

            try {
                result = JSON.parse(text);
            } catch {
                result = { message: text };
            }

            if (response.ok) {
                alert(result.message || 'Registration successful');
                navigate('/login'); 
            } else {
                setErrorMessage(result.message || 'Registration failed');
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
                <h2 className="text-center" style={{ color: 'var(--primary-color)' }}>Register</h2>
                {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label htmlFor="name" className="form-label">Name</label>
                        <input
                            type="text"
                            id="name"
                            className="form-control"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>

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
                    <div className="mb-3">
                        <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            className="form-control"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100" style={{
                        backgroundColor: 'var(--primary-color)',
                        borderColor: 'var(--primary-color)',
                    }}>Register</button>
                </form>
            </div>
        </div>
    );
};

export default Register;
