import React, { useState, useEffect } from 'react';

const CustomerDashboard = () => {
  const token = localStorage.getItem('token');
  const [products, setProducts] = useState([]);
  const [message, setMessage] = useState('');
  const [quantities, setQuantities] = useState({});
  const [errorMessages, setErrorMessages] = useState({});
  const [orderId, setOrderId] = useState(null);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await fetch('http://localhost:8082/public/products/get-all-available-products', {
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });
        const data = await res.json();
        setProducts(data);
        const initialQuantities = {};
        data.forEach(product => {
          initialQuantities[product.id] = 1;
        });
        setQuantities(initialQuantities);
      } catch (err) {
        console.error(err);
        setMessage('Failed to load products');
      }
    };

    fetchProducts();
  }, [token]);

  const handleQuantityChange = (productId, delta, maxQuantity) => {
    setQuantities(prev => {
      const currentQty = prev[productId] || 1;
      const newQty = Math.max(1, currentQty + delta);
      setErrorMessages(prevErrors => ({
        ...prevErrors,
        [productId]: newQty > maxQuantity ? `Only ${maxQuantity} items available` : ''
      }));

      return {
        ...prev,
        [productId]: newQty > maxQuantity ? maxQuantity : newQty
      };
    });
  };

  const handleAddToOrder = async (product) => {
    const quantity = quantities[product.id] || 1;

    if (quantity > product.amount) {
      setErrorMessages(prev => ({
        ...prev,
        [product.id]: `Cannot order more than ${product.amount} items`
      }));
      return;
    }

    const orderPayload = {
      customerId: 1,
      items: [
        {
          productId: product.id,
          sellerId: product.sellerId,
          quantity: quantity,
          price: product.price,
        }
      ],
      shippingCompanyName: "Flyo"
    };

    try {
      const response = await fetch('http://localhost:8081/orders/add-order', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(orderPayload),
      });

      if (!response.ok) throw new Error('Failed to place order');

      const data = await response.json();
      setOrderId(data.id); // Save the returned order ID

      setMessage(`Order placed for "${product.name}" (x${quantity})`);
    } catch (error) {
      console.error(error);
      setMessage(`Could not place order for "${product.name}"`);
    }
  };

  const handleCheckout = async () => {
  if (!orderId) {
    setMessage("No order to checkout");
    return;
  }

  try {
    const res = await fetch(`http://localhost:8081/orders/checkout/${orderId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!res.ok) {
      // Try to get error message from response JSON
      const errorData = await res.json();
      
      // If the error message is about order minimum
      if (errorData.message && errorData.message.toLowerCase().includes("minimum") || errorData.message.toLowerCase().includes("50")) {
        throw new Error("Order should be more than 50");
      } else {
        throw new Error(errorData.message || "Checkout failed");
      }
    }

    setMessage("✅ Order successfully checked out!");
    setOrderId(null); // Reset after checkout
  } catch (err) {
    console.error(err);
    setMessage(`❌ ${err.message || "Failed to checkout order."}`);
  }
};


  const styles = {
    dashboard: {
      background: 'linear-gradient(to bottom right, #fff5e1, #ffeedd)',
      padding: '2rem',
      borderRadius: '20px',
      border: '2px solid #fcbf49',
      boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
      maxWidth: '800px',
      margin: '2rem auto',
    },
    heading: {
      textAlign: 'center',
      color: '#ff6b6b',
      fontSize: '2rem',
      marginBottom: '2rem',
      fontWeight: 'bold',
    },
    productCard: {
      backgroundColor: '#fff',
      border: '2px dashed #ffb6b6',
      borderRadius: '16px',
      padding: '1.5rem',
      marginBottom: '1.5rem',
      boxShadow: '0 6px 18px rgba(0, 0, 0, 0.07)',
      display: 'flex',
      gap: '1.5rem',
      alignItems: 'center',
    },
    productImage: {
      width: '100px',
      height: '100px',
      objectFit: 'cover',
      borderRadius: '12px',
      border: '1px solid #ddd',
    },
    productInfo: {
      flex: 1,
      display: 'flex',
      flexDirection: 'column',
      gap: '0.5rem',
    },
    productName: {
      fontSize: '1.3rem',
      fontWeight: '600',
      color: '#333',
    },
    productPrice: {
      color: '#ff6b6b',
      fontWeight: 'bold',
    },
    quantity: {
      color: '#555',
      fontSize: '0.95rem',
    },
    message: {
      color: 'red',
      textAlign: 'center',
      marginTop: '1rem',
    },
    button: {
      backgroundColor: '#ff6b6b',
      color: 'white',
      border: 'none',
      padding: '0.5rem 1rem',
      borderRadius: '8px',
      cursor: 'pointer',
      fontWeight: 'bold',
    },
    quantityControl: {
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      marginTop: '0.5rem',
    },
    errorText: {
      color: 'red',
      fontSize: '0.85rem',
      marginTop: '0.25rem',
    }
  };

  return (
    <div style={styles.dashboard}>
      <h2 style={styles.heading}>🛍️ Available Products</h2>

      {products.length === 0 && <p style={{ textAlign: 'center' }}>Loading products...</p>}

      {products.map(product => (
        <div key={product.id} style={styles.productCard}>
          <img src={product.imageUrl} alt={product.name} style={styles.productImage} />
          <div style={styles.productInfo}>
            <div style={styles.productName}>{product.name}</div>
            <div style={styles.productPrice}>Price: ${product.price}</div>
            <div style={styles.quantity}>Quantity Available: {product.amount}</div>

            <div style={styles.quantityControl}>
              <button
                onClick={() => handleQuantityChange(product.id, -1, product.amount)}
                style={styles.button}
              >−</button>

              <span style={styles.quantity}>Qty: {quantities[product.id] || 1}</span>

              <button
                onClick={() => handleQuantityChange(product.id, 1, product.amount)}
                style={styles.button}
              >+</button>
            </div>

            {errorMessages[product.id] && (
              <div style={styles.errorText}>
                {errorMessages[product.id]}
              </div>
            )}

            <button
              style={{ ...styles.button, marginTop: '0.5rem' }}
              onClick={() => handleAddToOrder(product)}
            >
              ➕ Add to Order
            </button>
          </div>
        </div>
      ))}

      {orderId && (
        <div style={{ textAlign: 'center', marginTop: '2rem' }}>
          <button onClick={handleCheckout} style={styles.button}>
            🧾 Complete Checkout
          </button>
        </div>
      )}

      {message && <div style={styles.message}>{message}</div>}
    </div>
  );
};

export default CustomerDashboard;
