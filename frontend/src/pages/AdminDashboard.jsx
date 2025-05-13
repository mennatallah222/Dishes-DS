import React, { useState } from 'react';

const AdminDashboard = () => {
    const [companies, setCompanies] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [addCompanyResult, setAddCompanyResult] = useState('');
    const [newCompanies, setNewCompanies] = useState([
        { email: '', companyName: '' }
    ]);
    const [activeTab, setActiveTab] = useState('addCompanies');

    const handleGetCompanies = async () => {
        try {
            const res = await fetch('http://localhost:8080/admin-services/api/admin/get-companies');
            const data = await res.json();
            console.log(data);
            setCompanies(data);
            setActiveTab('viewCompanies');
        } catch (err) {
            console.error(err);
        }
    };

    const handleGetCustomers = async () => {
        try {
            const res = await fetch('http://localhost:8080/admin-services/api/admin/customers');
            const data = await res.json();
            setCustomers(data);
            setActiveTab('viewCustomers');
        } catch (err) {
            console.error(err);
        }
    };

    const handleAddCompany = async () => {
        try {
            const res = await fetch('http://localhost:8080/admin-services/api/admin/add-companies', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newCompanies)
            });

            const result = await res.json();
            setAddCompanyResult(JSON.stringify(result, null, 2));
            setTimeout(() => setAddCompanyResult(''), 3000);
        } catch (err) {
            setAddCompanyResult('Error adding companies');
            console.error(err);
            setTimeout(() => setAddCompanyResult(''), 3000);
        }
    };

    const handleCompanyInputChange = (index, field, value) => {
        const updated = [...newCompanies];
        updated[index][field] = value;
        setNewCompanies(updated);
    };

    const addNewCompanyField = () => {
        setNewCompanies([...newCompanies, { email: '', companyName: '' }]);
    };

    const removeCompanyField = (index) => {
        if (newCompanies.length > 1) {
            const updated = newCompanies.filter((_, i) => i !== index);
            setNewCompanies(updated);
        }
    };

    return (
        <div className="admin-dashboard" style={{
            minHeight: '100vh',
            padding: '2rem',
            backgroundColor: 'var(--background-color)',
            fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif' ",
            display: 'flex',
            justifyContent: 'center', 
            alignItems: 'center', 
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            color: 'var(--accent-color)',
        }}>
            <div className="dashboard-container" style={{
                maxWidth: '1200px',
                width: '100%',
                backgroundColor: 'white',
                borderRadius: '12px',
                boxShadow: '0 4px 20px rgba(0, 0, 0, 0.08)',
                overflow: 'hidden',
                padding: '2rem',
            }}>
                <header style={{
                    padding: '1.5rem 2rem',
                    borderBottom: '1px solid #eee',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                }}>
                    <h1 style={{
                        margin: 0,
                        fontSize: '1.8rem',
                        fontWeight: '600',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.5rem',
                        color: 'var(--primary-color)'
                    }}>
                        <span style={{
                            display: 'inline-flex',
                            padding: '0.5rem',
                            backgroundColor: '#f0f5ff',
                            borderRadius: '8px'
                        }}>🔧</span>
                        Admin Dashboard
                    </h1>
                    <div style={{
                        width: '36px',
                        height: '36px',
                        backgroundColor: '#FF7C71', 
                        color: 'white',
                        borderRadius: '50%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 'bold',
                        fontSize: '1rem',
                        marginLeft: '1rem'
                    }}>
                        AD
                    </div>
                </header>

                <div className="dashboard-content" style={{
                    display: 'flex',
                    minHeight: 'calc(100% - 72px)'
                }}>
                    <nav style={{
                        width: '220px',
                        borderRight: '1px solid #eee',
                        padding: '1.5rem 0'
                    }}>
                        <ul style={{
                            listStyle: 'none',
                            padding: 0,
                            margin: 0
                        }}>
                            <li style={{ marginBottom: '0.5rem' }}>
                                <button
                                    onClick={() => setActiveTab('addCompanies')}
                                    style={{
                                        width: '100%',
                                        textAlign: 'left',
                                        padding: '0.75rem 1.5rem',
                                        border: 'none',
                                        backgroundColor: activeTab === 'addCompanies' ? '#f0f5ff' : 'transparent',
                                        color: activeTab === 'addCompanies' ? 'var(--primary-color)' : '#333',
                                        cursor: 'pointer',
                                        fontWeight: activeTab === 'addCompanies' ? '600' : '400',
                                        transition: 'all 0.2s',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem'
                                    }}
                                >
                                    <span>➕</span> Add Companies
                                </button>
                            </li>
                            <li style={{ marginBottom: '0.5rem' }}>
                                <button
                                    onClick={() => {
                                        handleGetCompanies();
                                        setActiveTab('viewCompanies');
                                    }}
                                    style={{
                                        width: '100%',
                                        textAlign: 'left',
                                        padding: '0.75rem 1.5rem',
                                        border: 'none',
                                        backgroundColor: activeTab === 'viewCompanies' ? '#f0f5ff' : 'transparent',
                                        color: activeTab === 'viewCompanies' ? 'var(--primary-color)' : '#333',
                                        cursor: 'pointer',
                                        fontWeight: activeTab === 'viewCompanies' ? '600' : '400',
                                        transition: 'all 0.2s',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem'
                                    }}
                                >
                                    <span>🏢</span> View Companies
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => {
                                        handleGetCustomers();
                                        setActiveTab('viewCustomers');
                                    }}
                                    style={{
                                        width: '100%',
                                        textAlign: 'left',
                                        padding: '0.75rem 1.5rem',
                                        border: 'none',
                                        backgroundColor: activeTab === 'viewCustomers' ? '#f0f5ff' : 'transparent',
                                        color: activeTab === 'viewCustomers' ? 'var(--primary-color)' : '#333',
                                        cursor: 'pointer',
                                        fontWeight: activeTab === 'viewCustomers' ? '600' : '400',
                                        transition: 'all 0.2s',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem'
                                    }}
                                >
                                    <span>👤</span> View Customers
                                </button>
                            </li>
                        </ul>
                    </nav>

                    <main style={{
                        flex: 1,
                        padding: '2rem'
                    }}>
                        {activeTab === 'addCompanies' && (
                            <div className="add-companies-section">
                                <h2 style={{
                                    marginTop: 0,
                                    marginBottom: '1.5rem',
                                    fontSize: '1.4rem',
                                    fontWeight: '500',
                                    color: 'var(--primary-color)'
                                }}>Add New Companies</h2>

                                {newCompanies.map((company, index) => (
                                    <div key={index} style={{
                                        display: 'flex',
                                        gap: '1rem',
                                        marginBottom: '1rem',
                                        alignItems: 'center'
                                    }}>
                                        <input
                                            type="email"
                                            placeholder="Company Email"
                                            value={company.email}
                                            onChange={(e) => handleCompanyInputChange(index, 'email', e.target.value)}
                                            style={{
                                                flex: 1,
                                                padding: '0.75rem',
                                                border: '1px solid #ddd',
                                                borderRadius: '6px',
                                                fontSize: '0.9rem'
                                            }}
                                        />
                                        <input
                                            type="text"
                                            placeholder="Company Name"
                                            value={company.companyName}
                                            onChange={(e) => handleCompanyInputChange(index, 'companyName', e.target.value)}
                                            style={{
                                                flex: 1,
                                                padding: '0.75rem',
                                                border: '1px solid #ddd',
                                                borderRadius: '6px',
                                                fontSize: '0.9rem'
                                            }}
                                        />
                                        <button
                                            onClick={() => removeCompanyField(index)}
                                            style={{
                                                padding: '0.5rem',
                                                border: 'none',
                                                backgroundColor: '#ffebee',
                                                color: '#c62828',
                                                borderRadius: '6px',
                                                cursor: 'pointer',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                width: '36px',
                                                height: '36px',
                                                transition: 'all 0.2s'
                                            }}
                                            onMouseOver={(e) => e.target.backgroundColor = '#ffcdd2'}
                                            onMouseOut={(e) => e.target.backgroundColor = '#ffebee'}
                                        >
                                            ×
                                        </button>
                                    </div>
                                ))}

                                <div style={{
                                    display: 'flex',
                                    gap: '1rem',
                                    marginTop: '1.5rem'
                                }}>
                                    <button
                                        onClick={addNewCompanyField}
                                        style={{
                                            padding: '0.75rem 1.5rem',
                                            backgroundColor: 'transparent',
                                            border: '1px solid var(--primary-color)',
                                            color: 'var(--primary-color)',
                                            borderRadius: '6px',
                                            cursor: 'pointer',
                                            fontWeight: '500',
                                            transition: 'all 0.2s',
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: '0.5rem'
                                        }}
                                        onMouseOver={(e) => {
                                            e.target.backgroundColor = '#f0f5ff';
                                            e.target.transform = 'translateY(-1px)';
                                        }}
                                        onMouseOut={(e) => {
                                            e.target.backgroundColor = 'transparent';
                                            e.target.transform = 'translateY(0)';
                                        }}
                                    >
                                        <span>➕</span> Add Another Company
                                    </button>
                                    <button
                                        onClick={handleAddCompany}
                                        style={{
                                            padding: '0.75rem 1.5rem',
                                            backgroundColor: 'var(--primary-color)',
                                            border: 'none',
                                            color: 'white',
                                            borderRadius: '6px',
                                            cursor: 'pointer',
                                            fontWeight: '500',
                                            transition: 'all 0.2s'
                                        }}
                                        onMouseOver={(e) => {
                                            e.target.backgroundColor = '#e67e00';
                                            e.target.transform = 'translateY(-1px)';
                                        }}
                                        onMouseOut={(e) => {
                                            e.target.backgroundColor = 'var(--primary-color)';
                                            e.target.transform = 'translateY(0)';
                                        }}
                                    >
                                        Submit Companies
                                    </button>
                                </div>

                                {addCompanyResult && (
                                    <div style={{
                                        marginTop: '1.5rem',
                                        padding: '1rem',
                                        backgroundColor: '#e8f5e9',
                                        borderRadius: '6px',
                                        borderLeft: '4px solid #4caf50',
                                        fontSize: '0.9rem'
                                    }}>
                                        <pre style={{
                                            margin: 0,
                                            whiteSpace: 'pre-wrap',
                                            wordBreak: 'break-word',
                                            fontFamily: "'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace"
                                        }}>
                                            {addCompanyResult}
                                        </pre>
                                    </div>
                                )}
                            </div>
                        )}

                        {activeTab === 'viewCompanies' && (
                            <div className="view-companies-section">
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginBottom: '1.5rem'
                                }}>
                                    <h2 style={{
                                        margin: 0,
                                        fontSize: '1.4rem',
                                        fontWeight: '500',
                                        color: 'var(--primary-color)'
                                    }}>Company List</h2>
                                    <button
                                        onClick={handleGetCompanies}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            backgroundColor: 'var(--background-color)',
                                            border: 'none',
                                            color: 'var(--accent-color)',
                                            borderRadius: '6px',
                                            cursor: 'pointer',
                                            fontWeight: '500',
                                            transition: 'all 0.2s',
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: '0.5rem'
                                        }}
                                        onMouseOver={(e) => e.target.backgroundColor = '#e1ebfa'}
                                        onMouseOut={(e) => e.target.backgroundColor = '#f0f5ff'}
                                    >
                                        <span>🔄</span> Refresh
                                    </button>
                                </div>

                                {companies.length > 0 ? (
                                    <div style={{
                                        border: '1px solid #eee',
                                        borderRadius: '8px',
                                        overflow: 'hidden'
                                    }}>
                                        <div style={{
                                            display: 'grid',
                                            gridTemplateColumns: '1fr 1fr',
                                            backgroundColor: '#FCD6AE',
                                            padding: '0.75rem 1rem',
                                            fontWeight: '600',
                                            fontSize: '0.9rem'
                                        }}>
                                            <div>Company Name</div>
                                            <div>Email</div>
                                        </div>
                                        {companies.map((company, idx) => (
                                            <div
                                                key={idx}
                                                style={{
                                                    display: 'grid',
                                                    gridTemplateColumns: '1fr 1fr',
                                                    padding: '0.75rem 1rem',
                                                    borderBottom: '1px solid #eee',
                                                    transition: 'all 0.2s',
                                                    ':hover': {
                                                        backgroundColor: '#f9f9f9'
                                                    }
                                                }}
                                            >
                                                <div style={{ fontWeight: '500' }}>📦 {company.name}</div>
                                                <div>{company.email}</div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div style={{
                                        padding: '2rem',
                                        textAlign: 'center',
                                        backgroundColor: '#f5f5f5',
                                        borderRadius: '8px',
                                        color: '#666'
                                    }}>
                                        No companies found. Click "Refresh" to fetch companies.
                                    </div>
                                )}
                            </div>
                        )}

                        {activeTab === 'viewCustomers' && (
                            <div className="view-customers-section">
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginBottom: '1.5rem'
                                }}>
                                    <h2 style={{
                                        margin: 0,
                                        fontSize: '1.4rem',
                                        fontWeight: '500',
                                        color: 'var(--primary-color)'
                                    }}>Customer List</h2>
                                    <button
                                        onClick={handleGetCustomers}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            backgroundColor: '#fff8e1',
                                            border: 'none',
                                            color: '#ff8f00',
                                            borderRadius: '6px',
                                            cursor: 'pointer',
                                            fontWeight: '500',
                                            transition: 'all 0.2s',
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: '0.5rem'
                                        }}
                                        onMouseOver={(e) => e.target.backgroundColor = '#ffecb3'}
                                        onMouseOut={(e) => e.target.backgroundColor = '#fff8e1'}
                                    >
                                        <span>🔄</span> Refresh
                                    </button>
                                </div>

                                {customers.length > 0 ? (
                                    <div style={{
                                        border: '1px solid #eee',
                                        borderRadius: '8px',
                                        overflow: 'hidden'
                                    }}>
                                        <div style={{
                                            display: 'grid',
                                            gridTemplateColumns: '1fr',
                                            backgroundColor: '#FCD6AE',
                                            padding: '0.75rem 1rem',
                                            fontWeight: '600',
                                            fontSize: '0.9rem'
                                        }}>
                                            <div>Customer Details</div>
                                        </div>
                                        {customers.map((customer, idx) => (
                                            <div
                                                key={idx}
                                                style={{
                                                    padding: '0.75rem 1rem',
                                                    borderBottom: '1px solid #eee',
                                                    transition: 'all 0.2s',
                                                    ':hover': {
                                                        backgroundColor: '#f9f9f9'
                                                    }
                                                }}
                                            >
                                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                                    <span style={{
                                                        display: 'inline-flex',
                                                        width: '32px',
                                                        height: '32px',
                                                        backgroundColor: '#e3f2fd',
                                                        borderRadius: '50%',
                                                        alignItems: 'center',
                                                        justifyContent: 'center',
                                                        color: '#1976d2',
                                                        fontWeight: '500'
                                                    }}>
                                                        {customer.name ? customer.name.charAt(0) : '👤'}
                                                    </span>
                                                    <div>
                                                        <div style={{ fontWeight: '500' }}>{customer.name}</div>
                                                        <div>{customer.email}</div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div style={{
                                        padding: '2rem',
                                        textAlign: 'center',
                                        backgroundColor: '#f5f5f5',
                                        borderRadius: '8px',
                                        color: '#666'
                                    }}>
                                        No customers found. Click "Refresh" to fetch customers.
                                    </div>
                                )}
                            </div>
                        )}
                    </main>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
