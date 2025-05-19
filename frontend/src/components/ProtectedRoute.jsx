import { useState, useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import api from '../api';

const ProtectedRoute = ({ children, adminOnly = false }) => {
  const [auth, setAuth] = useState({ 
    loading: true, 
    isAdmin: false, 
    error: null
  });
  const location = useLocation();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setAuth({ loading: false, isAdmin: false, error: null });
      return;
    }

    const checkAuth = async () => {
      try {
        const { data: isAdmin } = await api.admin.checkAdmin();
        
        if (adminOnly && !isAdmin) {
          setAuth({ loading: false, isAdmin: false, error: 'Доступ запрещен' });
        } else {
          setAuth({ loading: false, isAdmin, error: null });
        }
      } catch (err) {
        setAuth({ 
          loading: false, 
          isAdmin: false, 
          error: err.response?.data?.message || 'Ошибка проверки прав' 
        });
      }
    };

    checkAuth();
  }, [adminOnly, location.pathname]);

  if (auth.loading) return <div>Загрузка...</div>;
  if (auth.error) return <div>Ошибка: {auth.error}</div>;
  if (!localStorage.getItem('token')) return <Navigate to="/login" replace />;
  if (adminOnly && !auth.isAdmin) return <Navigate to="/" replace />;

  return children;
};

export default ProtectedRoute;