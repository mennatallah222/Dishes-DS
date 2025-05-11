import React from 'react';
import { Link } from 'react-router-dom';

const WelcomePage = () => {
    return (
        <div className="d-flex justify-content-center align-items-center" style={{
            height: '100vh',
            width: '100vw',
            position: 'fixed',
            top: 0,
            left: 0,
            backgroundColor: 'var(--background-color)', 
            color: 'var(--accent-color)',
        }}>
            <div className="text-center">
                <h1 style={{ color: 'var(--primary-color)' }}>Welcome to Homemade Dishes</h1>
                <p>Order your favorite dishes from the comfort of your home</p>
                <div className="mt-4">
                    <Link to="/login">
                        <button className="btn btn-primary btn-lg me-3" style={{ backgroundColor: 'var(--primary-color)', borderColor: 'var(--primary-color)' }}>Login</button>
                    </Link>
                    <Link to="/register">
                        <button className="btn btn-secondary btn-lg" style={{ backgroundColor: 'var(--secondary-color)', borderColor: 'var(--secondary-color)' }}>Register</button>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default WelcomePage;
