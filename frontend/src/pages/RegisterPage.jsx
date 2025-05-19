import React, { useState } from 'react';
import { Box, TextField, Button, Typography, Alert, Link } from '@mui/material';
import api from '../api';
import { useNavigate } from 'react-router-dom';
import { CircularProgress } from '@mui/material';

const RegisterPage = () => {
  const [credentials, setCredentials] = useState({
    login: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const response = await api.auth.register(credentials);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка регистрации');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: 400, mx: 'auto', mt: 10 }}>
      <Typography variant="h4" gutterBottom>Регистрация</Typography>
      
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      
      <TextField
        fullWidth
        label="Логин"
        value={credentials.login}
        onChange={(e) => setCredentials({...credentials, login: e.target.value})}
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
        {loading ? <CircularProgress size={24} /> : 'Зарегистрироваться'}
      </Button>

      <Typography variant="body2" sx={{ mt: 2, textAlign: 'center' }}>
        Уже есть аккаунт? <Link href="/login">Войти</Link>
      </Typography>
    </Box>
  );
};

export default RegisterPage;