import { de } from "date-fns/locale";
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
    Box, 
    Typography, 
    Tabs, 
    Tab, 
    Paper,
    Button,
    List,
    ListItem,
    ListItemText,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    CircularProgress,
    Alert
  } from '@mui/material';
  import api from '../api';

 

const LoginPage = ({ onLogin }) => {
    const [credentials, setCredentials] = useState({
      login: '',
      password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();
  
    const handleSubmit = async (e) => {
      e.preventDefault();
        
      if (!credentials.login.trim() || !credentials.password) {
        return setError('Заполните все поля');
      }
    
      setLoading(true);
      setError('');
      
      try {
        const response = await api.auth.login(credentials);
        if (!response.data?.token) {
          throw new Error('Токен не получен');
        }
        
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('userId', response.data.userId);
        
        const { data: isAdmin } = await api.admin.checkAdmin();
        localStorage.setItem('isAdmin', isAdmin.toString());
        
        if (isAdmin && onLogin) {
          onLogin();
        }
        
        navigate('/bookings');
      } catch (err) {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('isAdmin');
        setError(err.response?.data?.message || 'Ошибка входа');
      } finally {
        setLoading(false);
      }
    };
  
    return (
      <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: 400, mx: 'auto', mt: 10 }}>
        <Typography variant="h4" gutterBottom>Вход в систему</Typography>
        
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        
        <TextField
          autoFocus
          fullWidth
          label="Логин"
          value={credentials.login}
          onChange={(e) => setCredentials({...credentials, login: e.target.value.trim()})}
          margin="normal"
          required
        />
        
        <TextField
          fullWidth
          label="Пароль"
          type="password"
          value={credentials.password}
          onChange={(e) => setCredentials({...credentials, password: e.target.value})}
          margin="normal"
          required
        />
        
        <Button 
          type="submit" 
          variant="contained" 
          fullWidth 
          sx={{ mt: 2 }}
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} color="inherit" /> : 'Войти'}
        </Button>
      </Box>
    );
  };
export default LoginPage;