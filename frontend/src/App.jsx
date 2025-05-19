import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate, Link, useLocation } from 'react-router-dom';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Container,
  CssBaseline,
  CircularProgress,
  Box
} from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { ru } from 'date-fns/locale/ru';
import LoginPage from './pages/LoginPage';
import CreateBookingPage from './pages/CreateBookingPage';
import BookingListPage from './pages/BookingListPage';
import ProtectedRoute from './components/ProtectedRoute';
import AdminPage from './pages/AdminPage';
import RegisterPage from './pages/RegisterPage';
import api from './api';

const App = () => {
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    const checkAdminStatus = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        setIsAdmin(false);
        setLoading(false);
        return;
      }

      try {
        const { data } = await api.admin.checkAdmin();
        setIsAdmin(data);
      } catch (error) {
        console.error('Admin check failed:', error);
        setIsAdmin(false);
      } finally {
        setLoading(false);
      }
    };

    checkAdminStatus();
    window.addEventListener('storage', checkAdminStatus);
    return () => window.removeEventListener('storage', checkAdminStatus);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('isAdmin');
    setIsAdmin(false);
    window.location.href = '/login';
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
      <CssBaseline />
      <AppBar position="static">
        <Toolbar>
          <Typography 
            variant="h6" 
            sx={{ flexGrow: 1 }} 
            component={Link} 
            to="/" 
            style={{ textDecoration: 'none', color: 'inherit' }}
          >
            Система бронирования
          </Typography>
          
          {localStorage.getItem('token') && (
            <>
              <Button color="inherit" component={Link} to="/bookings">Мои брони</Button>
              <Button color="inherit" component={Link} to="/create">Новое бронирование</Button>
              {isAdmin && (
                <Button 
                  color="inherit" 
                  component={Link} 
                  to="/admin"
                  state={{ from: location }}
                >
                  Админ-панель
                </Button>
              )}
            </>
          )}
          
          {!localStorage.getItem('token') ? (
            <>
              <Button color="inherit" component={Link} to="/login">Вход</Button>
              <Button color="inherit" component={Link} to="/register">Регистрация</Button>
            </>
          ) : (
            <Button color="inherit" onClick={handleLogout}>
              Выйти
            </Button>
          )}
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Routes>
          <Route path="/login" element={<LoginPage onLogin={() => setIsAdmin(true)} />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/bookings" element={
            <ProtectedRoute>
              <BookingListPage />
            </ProtectedRoute>} 
          />
          <Route path="/create" element={
            <ProtectedRoute>
              <CreateBookingPage />
            </ProtectedRoute>
          } />
          <Route path="/admin" element={
            <ProtectedRoute adminOnly>
              <AdminPage />
            </ProtectedRoute>
          } />
          <Route path="/" element={<Navigate to="/bookings" replace />} />
        </Routes>
      </Container>
    </LocalizationProvider>
  );
};

export default App;