import React, { useState, useEffect } from 'react';

const CustomerDashboard = () => {
  const token = localStorage.getItem('token');
  const [products, setProducts] = useState([]);
  const [message, setMessage] = useState('');
  const [quantities, setQuantities] = useState({});
  const [errorMessages, setErrorMessages] = useState({});
  const [orderId, setOrderId] = useState(null);
  const [selectedProducts, setSelectedProducts] = useState([]);
    const [customerOrders, setCustomerOrders] = useState([]);

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

  const handleSelectProduct = (product) => {
  const quantity = quantities[product.id] || 1;

  if (quantity > product.amount) {
    setErrorMessages(prev => ({
      ...prev,
      [product.id]: `Cannot order more than ${product.amount} items`
    }));
    return;
  }

  const alreadySelected = selectedProducts.find(item => item.productId === product.id);
  if (alreadySelected) {
    setMessage(`Product "${product.name}" is already selected.`);
    return;
  }

  setSelectedProducts(prev => [
    ...prev,
    {
      productId: product.id,
      sellerId: product.sellerId,
      quantity: quantity,
      price: product.price
    }
  ]);

  setMessage(`✅ Added "${product.name}" (x${quantity}) to order list`);
};

const handleSubmitOrder = async () => {
  if (selectedProducts.length === 0) {
    setMessage("❌ No products selected for order");
    return;
  }

  const orderPayload = {
    items: selectedProducts,
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
    console.log("✅ Order response:", data); 

    setOrderId(data.id);
    setMessage("✅ Order placed successfully");
    setSelectedProducts([]);
  } catch (error) {
    console.error(error);
    setMessage("❌ Could not place the order.");
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

    if (!res.ok) throw new Error("Checkout failed");

    const result = await res.json();
    console.log("✅ Checkout response:", result); 

    setMessage("✅ Order confirmed successfully!");
    setOrderId(null);
  } catch (err) {
    console.error(err);
    setMessage("❌ Failed to checkout order.The order should be greater than 50 .");
  }
};

const handleGetCustomerOrders = async () => {
  try {
    const res = await fetch('http://localhost:8081/api/customers/customer-orders', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      }
    });

    if (!res.ok) throw new Error('Failed to fetch customer orders');
    const data = await res.json();
    setCustomerOrders(data);

  } catch (err) {
    console.error(err);
    setMessage("Failed to fetch customer orders.");
  }
};

  const styles = {
     pageBackground: {
    minHeight: '100vh',
    background: 'var(--background-color)',
    padding: '5rem',
  },
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
    <div style={styles.pageBackground}>
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
              <button onClick={() => handleQuantityChange(product.id, -1, product.amount)} style={styles.button}>−</button>
              <span style={styles.quantity}>Qty: {quantities[product.id] || 1}</span>
              <button onClick={() => handleQuantityChange(product.id, 1, product.amount)} style={styles.button}>+</button>
            </div>

            {errorMessages[product.id] && (
              <div style={styles.errorText}>{errorMessages[product.id]}</div>
            )}

            <button
              style={{ ...styles.button, marginTop: '0.5rem' }}
              onClick={() => handleSelectProduct(product)}
            >
              ➕ Select Product
            </button>
          </div>
        </div>
      ))}

      {selectedProducts.length > 0 && (
        <div style={{ textAlign: 'center', marginTop: '2rem' }}>
          <button onClick={handleSubmitOrder} style={styles.button}>
            🚚 Submit Order ({selectedProducts.length} items)
          </button>
        </div>
      )}

      {orderId && (
        <div style={{ textAlign: 'center', marginTop: '1rem' }}>
          <button onClick={handleCheckout} style={styles.button}>
            🧾 Complete Checkout
          </button>
        </div>
      )}
     <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
  <button onClick={handleGetCustomerOrders} style={{ ...styles.button, backgroundColor: '#ffa502' }}>
    📋 Show My Orders
  </button>
</div>

{customerOrders.length > 0 && (
  <div style={{ marginTop: '2rem' }}>
    <h3 style={{ 
      textAlign: 'center', 
      color: '#2f3542', 
      fontSize: '1.8rem', 
      marginBottom: '1.5rem' 
    }}>
      🧾 Your Orders
    </h3>
    
    {customerOrders.map(order => (
      <div key={order.id} style={{ 
        backgroundColor: '#fffdf9',
        border: '2px solid #ffd59a',
        borderRadius: '16px',
        padding: '1.5rem',
        marginBottom: '2rem',
        boxShadow: '0 6px 18px rgba(0, 0, 0, 0.08)'
      }}>
        <div style={{ marginBottom: '1rem' }}>
          <p><strong>🆔 Order ID:</strong> <span style={{ color: '#ff6348' }}>{order.id}</span></p>
          <p><strong>📦 Status:</strong> <span style={{ color: '#1e90ff' }}>{order.status}</span></p>
          <p><strong>💰 Total:</strong> <span style={{ color: '#2ed573' }}>${order.total}</span></p>
          <p><strong>🚚 Shipping:</strong> {order.shippingCompany}</p>
        </div>

        <h4 style={{ marginBottom: '0.5rem', color: '#3742fa' }}>🛒 Items:</h4>
        <ul style={{ paddingLeft: '1.5rem' }}>
          {order.items?.map((item, index) => (
            <li key={index} style={{ 
              marginBottom: '1rem',
              listStyleType: 'circle',
              padding: '0.5rem',
              borderBottom: '1px dashed #ccc'
            }}>
              <p><strong>Product ID:</strong> {item.productId}</p>
              <p><strong>Quantity:</strong> {item.quantity}</p>
              <p><strong>Price:</strong> ${item.price}</p>
              {item.productName && <p><strong>Name:</strong> {item.productName}</p>}
              {item.productImageUrl && (
                <img 
                  src={item.productImageUrl}
                  alt="product"
                  style={{
                    width: '80px',
                    height: '80px',
                    borderRadius: '8px',
                    border: '1px solid #ddd',
                    objectFit: 'cover',
                    marginTop: '0.5rem'
                  }}
                />
              )}
            </li>
          ))}
        </ul>
      </div>
    ))}
  </div>
)}


      {message && <div style={styles.message}>{message}</div>}
    </div>
    </div>
  );
};

export default CustomerDashboard;