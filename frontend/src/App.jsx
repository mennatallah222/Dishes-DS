import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Welcome from './pages/WelcomePage'; 

import { Container } from 'react-bootstrap';

function App() {
    return (
        <Router>
            <Container fluid>
                <Routes>
                    <Route path="/" element={<Welcome />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                </Routes>
            </Container>
        </Router>
    );
}

export default App;
