import React, { useState } from 'react';

const Login = () => {
    const [role, setRole] = useState('customer'); // default role
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [companyName, setCompanyName] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        let url = '';
        let bodyData = { email, password };

        switch (role) {
            case 'admin':
                url = 'http://localhost:8080/admin-services/api/admin/login';
                break;
            case 'seller':
                url = 'http://localhost:8080/seller/login';
                bodyData = { email, password, companyName }; 
                break;
            default:
                url = 'http://localhost:8081/auth/login'; 
        }

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bodyData),
            });

            const text = await response.text();
            let result;
            try {
                result = JSON.parse(text);
                console.log(response)
            } catch {
                result = { message: text };
            }

            if (response.ok) {
                alert(result.message || 'Login successful');
            } else {
                setErrorMessage(result.message || 'Invalid credentials');
            }
        } catch (error) {
            setErrorMessage('An error occurred. Please try again later.'+error);
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
                        <label className="form-label">Role</label>
                        <select className="form-select" value={role} onChange={(e) => setRole(e.target.value)}>
                            <option value="customer">Customer</option>
                            <option value="admin">Admin</option>
                            <option value="seller">Seller</option>
                        </select>
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
                    {role === 'seller' && (
                        <div className="mb-3">
                            <label htmlFor="company" className="form-label">Company Name</label>
                            <input
                                type="text"
                                id="company"
                                className="form-control"
                                value={companyName}
                                onChange={(e) => setCompanyName(e.target.value)}
                                required
                            />
                        </div>
                    )}
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
