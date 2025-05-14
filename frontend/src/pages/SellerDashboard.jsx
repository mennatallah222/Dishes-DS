import React, { useEffect, useState } from 'react';

const SellerDashboard = () => {
  const [dishes, setDishes] = useState([]);
  const [soldDishesDetails, setSoldDishesDetails] = useState([]);
  const [newDish, setNewDish] = useState({ name: '', price: '', amount: '', imageUrl: '' });
  const [editingDish, setEditingDish] = useState(null);
  const [showAvailable, setShowAvailable] = useState(false);
  const [showSold, setShowSold] = useState(false);
  
  const token = localStorage.getItem('token');

  const handleAddDish = async (e) => {
  e.preventDefault();
  try {
    const url = editingDish
      ? `http://localhost:8082/seller/products/update-dish/${editingDish.id}`
      : 'http://localhost:8082/seller/products/add-dish';

    const method = editingDish ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newDish)
    });

    if (!res.ok) {
      const error = await res.text();
      alert(`Error: ${error}`);
      return;
    }

    const data = await res.json();
    alert(editingDish ? 'Dish updated successfully' : 'Dish added successfully');
    setNewDish({ name: '', price: '', amount: '', imageUrl: '' });
    setEditingDish(null);
    fetchDishes();
    fetchSoldDishes();
  } catch (err) {
    console.error(err);
  }
};


  const fetchDishes = () => {
    fetch('http://localhost:8082/seller/products/get-available-dishes', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
      .then(res => res.json())
      .then(data => setDishes(data))
      .catch(err => console.error('Error fetching dishes:', err));
  };

  const fetchSoldDishes = () => {
    fetch('http://localhost:8082/seller/products/get-seller-dishes', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
      .then(res => res.json())
      .then(data => setSoldDishesDetails(data))
      .catch(err => console.error('Error fetching sold dishes:', err));
  };

  useEffect(() => {
    fetchDishes();
    fetchSoldDishes();
  }, []);

  const styles = {
    dashboard: {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      padding: '2rem',
      backgroundColor: 'var(--background-color)',
      backgroundImage: 'linear-gradient(to bottom right, var(--background-color), #fff)',
    },
    container: {
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      padding: '2.5rem',
      width: '90%',
      maxWidth: '900px',
      borderRadius: '16px',
      boxShadow: '0 10px 30px rgba(0,0,0,0.1)',
      fontFamily: "'Poppins', sans-serif",
      color: 'var(--accent-color)',
      border: '3px solid var(--secondary-color)',
      backdropFilter: 'blur(5px)',
    },
    title: {
      color: 'var(--primary-color)',
      marginBottom: '2rem',
      textAlign: 'center',
      fontSize: '2.2rem',
      fontWeight: '700',
      textShadow: '2px 2px 4px rgba(0,0,0,0.1)',
      letterSpacing: '1px'
    },
    form: {
      display: 'flex',
      flexDirection: 'column',
      gap: '1.5rem',
      marginBottom: '3rem',
      padding: '1.5rem',
      borderRadius: '12px',
      backgroundColor: 'rgba(252, 214, 174, 0.3)',
      border: '2px dashed var(--primary-color)'
    },
    inputGroup: {
      display: 'flex',
      flexDirection: 'column',
      gap: '0.5rem'
    },
    inputLabel: {
      color: 'var(--accent-color)',
      fontWeight: '600',
      fontSize: '0.9rem'
    },
    input: {
      padding: '0.8rem 1rem',
      borderRadius: '8px',
      border: '2px solid var(--secondary-color)',
      outline: 'none',
      backgroundColor: 'rgba(255, 255, 255, 0.8)',
      color: 'var(--accent-color)',
      fontSize: '1rem',
      transition: 'all 0.3s ease',
      boxShadow: 'inset 0 1px 3px rgba(0,0,0,0.1)'
    },
    button: {
      backgroundColor: 'var(--primary-color)',
      color: 'white',
      border: 'none',
      padding: '0.8rem',
      borderRadius: '8px',
      fontWeight: 'bold',
      cursor: 'pointer',
      transition: 'all 0.3s ease',
      fontSize: '1rem',
      textTransform: 'uppercase',
      letterSpacing: '1px'
    },
    listTitle: {
      color: 'var(--primary-color)',
      marginBottom: '1.5rem',
      textAlign: 'center',
      fontSize: '1.5rem',
      fontWeight: '600'
    },
    list: {
      listStyle: 'none',
      padding: 0,
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
      gap: '1.5rem'
    },
    listItem: {
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      border: '2px solid var(--primary-color)',
      padding: '1.5rem',
      borderRadius: '12px',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      color: 'var(--accent-color)',
      transition: 'all 0.3s ease',
      cursor: 'pointer'
    },
    dishName: {
      color: 'var(--primary-color)',
      fontSize: '1.2rem',
      fontWeight: '600',
      marginBottom: '0.5rem',
      textAlign: 'center'
    },
    dishDetails: {
      color: 'var(--accent-color)',
      fontSize: '0.9rem',
      textAlign: 'center',
      lineHeight: '1.6'
    },
    soldOutDish: {},
    soldOutText: {
      color: 'red',
      fontSize: '1rem',
      fontWeight: 'bold',
      marginTop: '1rem',
    },
    emptyState: {
      textAlign: 'center',
      color: 'var(--accent-color)',
      fontStyle: 'italic',
      padding: '2rem',
      gridColumn: '1 / -1'
    },
    checkboxContainer: {
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      fontSize: '1rem',
      fontFamily: "'Poppins', sans-serif",
      color: 'var(--accent-color)',
      cursor: 'pointer',
      backgroundColor: 'rgba(252, 214, 174, 0.3)',
      padding: '0.5rem 1rem',
      borderRadius: '8px',
      border: '1px solid var(--secondary-color)',
      boxShadow: '0 2px 6px rgba(0, 0, 0, 0.05)',
      transition: 'background-color 0.3s ease',
    },
    checkboxInput: {
      width: '18px',
      height: '18px',
      accentColor: 'var(--primary-color)',
      cursor: 'pointer',
    },
   dishImageContainer: {
    width: '120px',
    height: '120px',
    marginBottom: '1rem',
    position: 'relative',
    overflow: 'hidden',
    borderRadius: '12px',
    border: '2px solid var(--primary-color)',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    transition: 'all 0.3s ease',
    '&:hover': {
      transform: 'scale(1.05)',
      boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)'
    }
  },
  dishImage: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    display: 'block'
  },
  };

  return (
    <div style={styles.dashboard}>
      <div style={styles.container}>
        <h2 style={styles.title}>👨‍🍳 Seller Dashboard</h2>

        <form onSubmit={handleAddDish} style={styles.form}>
          <div style={styles.inputGroup}>
            <label style={styles.inputLabel}>Dish Name</label>
            <input
              type="text"
              placeholder="Enter dish name"
              style={styles.input}
              value={newDish.name}
              onChange={(e) => setNewDish({ ...newDish, name: e.target.value })}
              required
            />
          </div>
          <div style={styles.inputGroup}>
            <label style={styles.inputLabel}>Price ($)</label>
            <input
              type="number"
              placeholder="Enter price"
              style={styles.input}
              value={newDish.price}
              onChange={(e) => setNewDish({ ...newDish, price: e.target.value })}
              required
            />
          </div>
          <div style={styles.inputGroup}>
            <label style={styles.inputLabel}>Available Amount</label>
            <input
              type="number"
              placeholder="Enter available amount"
              style={styles.input}
              value={newDish.amount}
              onChange={(e) => setNewDish({ ...newDish, amount: e.target.value })}
              required
            />
          </div>
          <div style={styles.inputGroup}>
            <label style={styles.inputLabel}>Image URL</label>
            <input
              type="text"
              placeholder="https://example.com/image.jpg"
              style={styles.input}
              value={newDish.imageUrl}
              onChange={(e) => setNewDish({ ...newDish, imageUrl: e.target.value })}
            />
          </div>
          <button type="submit" style={styles.button}>
            {editingDish ? 'Update Dish' : 'Add Dish'}
          </button>
          {editingDish && (
            <button
              type="button"
              onClick={() => {
                setEditingDish(null);
                setNewDish({ name: '', price: '', amount: '', imageUrl: '' });
              }}
              style={{ ...styles.button, backgroundColor: 'gray', marginTop: '1rem' }}
            >
              Cancel Editing
            </button>
          )}
        </form>

        <div style={{ display: 'flex', justifyContent: 'center', gap: '2rem', marginBottom: '2rem' }}>
          <label style={styles.checkboxContainer}>
            <input
              type="checkbox"
              checked={showSold}
              onChange={() => setShowSold(prev => !prev)}
              style={styles.checkboxInput}
            />
            Show All Dishes
          </label>
          <label style={styles.checkboxContainer}>
            <input
              type="checkbox"
              checked={showAvailable}
              onChange={() => setShowAvailable(prev => !prev)}
              style={styles.checkboxInput}
            />
            Show Available Dishes
          </label>
        </div>

        {showSold && (
          <>
            <h4 style={styles.listTitle}>All Dishes</h4>
            <ul style={styles.list}>
              {soldDishesDetails.length > 0 ? (
                soldDishesDetails.map((dish, index) => (
                  <li
                    key={index}
                    style={{
                      ...styles.listItem,
                      opacity: dish.status === 'SOLD' ? 0.5 : 1
                    }}
                    onClick={() => {
                      setEditingDish(dish);
                      setNewDish({
                        name: dish.name,
                        price: dish.price,
                        amount: dish.amount,
                        imageUrl: dish.imageUrl || ''
                      });
                    }}
                  >
                    {dish.imageUrl && <img src={dish.imageUrl} alt={dish.name} style={styles.dishImage} />}
                    <div style={styles.dishName}>{dish.name}</div>
                    <div style={styles.dishDetails}>
                      Price: ${dish.price} <br />
                      Status: {dish.status}
                    </div>
                  </li>
                ))
              ) : (
                <div style={styles.emptyState}>No Dishes yet.</div>
              )}
            </ul>
          </>
        )}

        {showAvailable && (
          <>
            <h4 style={{ ...styles.listTitle, marginTop: '3rem' }}>Available Dishes</h4>
            <ul style={styles.list}>
              {dishes.length > 0 ? (
                dishes.map((dish, index) => (
                  <li
                    key={index}
                    style={{
                      ...styles.listItem,
                      ...(dish.amount === 0 && styles.soldOutDish)
                    }}
                    onClick={() => {
                      setEditingDish(dish);
                      setNewDish({
                        name: dish.name,
                        price: dish.price,
                        amount: dish.amount,
                        imageUrl: dish.imageUrl || ''
                      });
                    }}
                  >
                    {dish.imageUrl && <img src={dish.imageUrl} alt={dish.name} style={styles.dishImage} />}
                    <div style={styles.dishName}>{dish.name}</div>
                    <div style={styles.dishDetails}>
                      Price: ${dish.price}<br />
                      Available: {dish.amount}
                    </div>
                    {dish.amount === 0 && <div style={styles.soldOutText}>Sold Out</div>}
                  </li>
                ))
              ) : (
                <div style={styles.emptyState}>
                  No dishes available yet. Add your first dish above!
                </div>
              )}
            </ul>
          </>
        )}
      </div>
    </div>
  );
};

export default SellerDashboard;
