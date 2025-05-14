import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Welcome from './pages/WelcomePage'; 
import AdminDashboard from './pages/AdminDashboard';
import { Container } from 'react-bootstrap';
import SellerDashboard from './pages/SellerDashboard';
import CustomerDashboard from './pages/CustomerDashboard';
function App() {
    return (
        <Router>
            <Container fluid>
                <Routes>
                    <Route path="/" element={<Welcome />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/admin/dashboard" element={<AdminDashboard />} />
                    <Route path="/seller/dashboard" element={<SellerDashboard />} />
                     <Route path="/customer/dashboard" element={<CustomerDashboard />} />
                </Routes>
            </Container>
        </Router>
    );
}

export default App;
