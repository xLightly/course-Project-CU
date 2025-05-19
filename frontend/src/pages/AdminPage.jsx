import React, { useState, useEffect } from 'react';
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
  Chip,
  Alert
} from '@mui/material';
import { 
  Delete as DeleteIcon, 
  Edit as EditIcon,
  PersonAdd as PersonAddIcon
} from '@mui/icons-material';
import api from '../api';
import SpaceForm from '../components/SpaceForm';
import BookingList from '../components/BookingList';

const AdminPage = () => {
  const [tabValue, setTabValue] = useState(0);
  const [bookings, setBookings] = useState([]);
  const [spaces, setSpaces] = useState([]);
  const [editingSpace, setEditingSpace] = useState(null);
  const [promoteLogin, setPromoteLogin] = useState('');
  const [loading, setLoading] = useState({
    bookings: false,
    spaces: false,
    action: false
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    fetchAdminData();
  }, []);

  const fetchAdminData = async () => {
    try {
      setLoading(prev => ({...prev, bookings: true, spaces: true}));
      setError(null);
      
      const [bookingsRes, spacesRes] = await Promise.all([
        api.admin.getAllBookings(),
        api.workplace.getAll()
      ]);
      
      setBookings(bookingsRes.data);
      setSpaces(spacesRes.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(prev => ({...prev, bookings: false, spaces: false}));
    }
  };

  const handlePromoteUser = async () => {
    try {
      setLoading(prev => ({...prev, action: true}));
      setError(null);
      setSuccess(null);
      
      await api.admin.promoteToAdmin(promoteLogin);
      
      setSuccess(`Пользователь ${promoteLogin} успешно назначен администратором`);
      setPromoteLogin('');
      setDialogOpen(false);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(prev => ({...prev, action: false}));
    }
  };

  const handleDeleteBooking = async (bookingId) => {
    if (!bookingId) {
      console.error('Не указан ID бронирования для удаления');
      return;
    }
  
    try {
      setLoading(prev => ({...prev, action: true}));
      await api.admin.deleteBooking(bookingId);
      setBookings(prev => prev.filter(b => b.id !== bookingId));
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Ошибка удаления');
    } finally {
      setLoading(prev => ({...prev, action: false}));
    }
  };

  const handleDeleteSpace = async (spaceId) => {
    try {
      setLoading(prev => ({...prev, action: true}));
      await api.admin.deleteWorkplace(spaceId);
      setSpaces(spaces.filter(s => s.id !== spaceId));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(prev => ({...prev, action: false}));
    }
  };

  const handleSubmitSpace = async (spaceData) => {
    try {
      setLoading(prev => ({...prev, action: true}));
      setError(null);
      
      await api.admin.createWorkplace(spaceData.count);
      
      setSuccess(`${spaceData.count} рабочих мест успешно добавлено`);
      
      const res = await api.workplace.getAll();
      setSpaces(res.data);
    } catch (err) {
      setError(err.message || 'Ошибка при добавлении мест');
    } finally {
      setLoading(prev => ({...prev, action: false}));
    }
  };


  return (
    <Box sx={{ mt: 4, p: 2 }}>
      <Typography variant="h4" gutterBottom>Панель администратора</Typography>
      
      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}
      
      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
          {success}
        </Alert>
      )}
      
      <Paper sx={{ mb: 2 }}>
        <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
          <Tab label="Бронирования" />
          <Tab label="Рабочие места" />
          <Tab label="Пользователи" />
        </Tabs>
      </Paper>

      {tabValue === 0 && (
        <Box>
          <Typography variant="h5" gutterBottom>Все бронирования</Typography>
          {loading.bookings ? (
            <CircularProgress />
          ) : (
            <BookingList 
              bookings={bookings} 
              isAdmin={true} 
              onDelete={handleDeleteBooking}
            />
          )}
        </Box>
      )}

      
{tabValue === 1 && (
        <Box>
          <Typography variant="h5" gutterBottom>Управление рабочими местами</Typography>
          <SpaceForm 
            onSubmit={handleSubmitSpace} 
          />
          {loading.spaces ? (
            <CircularProgress />
          ) : (
            <List>
              {spaces.map((space) => (
                <ListItem key={space.id} divider>
                  <ListItemText
                    primary={`Рабочее место #${space.id}`}
                    secondary={
                      <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                        <Chip 
                          label={space.isBooked ? 'Занято' : 'Свободно'} 
                          color={space.isBooked ? 'error' : 'success'} 
                          size="small"
                        />
                        {space.isBooked && (
                          <Typography variant="body2" sx={{ ml: 2 }}>
                            {space.bookedUntil && `До: ${new Date(space.bookedUntil).toLocaleString()}`}
                          </Typography>
                        )}
                      </Box>
                    }
                  />
                  <Box>
                    <IconButton 
                      edge="end" 
                      onClick={() => handleDeleteSpace(space.id)}
                      disabled={loading.action}
                    >
                      <DeleteIcon color="error" />
                    </IconButton>
                  </Box>
                </ListItem>
              ))}
            </List>
          )}
        </Box>
      )}

      {tabValue === 2 && (
        <Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="h5">Пользователи</Typography>
            <Button 
              variant="contained" 
              startIcon={<PersonAddIcon />}
              onClick={() => setDialogOpen(true)}
            >
              Назначить администратора
            </Button>
          </Box>
          
          <Typography>Введите логин пользователя для повышения до администратора.</Typography>
        </Box>
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
        <DialogTitle>Назначить администратора</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Логин пользователя"
            fullWidth
            value={promoteLogin}
            onChange={(e) => setPromoteLogin(e.target.value)}
            helperText="Введите логин пользователя, которого хотите назначить администратором"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Отмена</Button>
          <Button 
            onClick={handlePromoteUser}
            disabled={!promoteLogin || loading.action}
            color="primary"
          >
            {loading.action ? <CircularProgress size={24} /> : 'Назначить'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AdminPage;