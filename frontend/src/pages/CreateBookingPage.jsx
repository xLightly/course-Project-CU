import React, { useState } from 'react';
import { 
  Box, TextField, Button, Typography, 
  List, ListItem, ListItemText,
  Alert, CircularProgress
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { format } from 'date-fns';
import { ru } from 'date-fns/locale/ru';
import api from '../api';
import { useNavigate } from 'react-router-dom';

const CreateBookingPage = () => {
  const [formData, setFormData] = useState({
    title: '',
    description: ''
  });
  const [timeRange, setTimeRange] = useState({
    startTime: null,
    endTime: null
  });
  const [availableWorkplaces, setAvailableWorkplaces] = useState([]);
  const [loading, setLoading] = useState({
    search: false,
    booking: false
  });
  const [error, setError] = useState('');

  const navigate = useNavigate();

  const handleSearchAvailable = async () => {
    if (!timeRange.startTime || !timeRange.endTime) {
      return setError('Укажите время начала и окончания');
    }
    
    if (timeRange.startTime >= timeRange.endTime) {
      return setError('Время окончания должно быть позже времени начала');
    }

    try {
      setLoading(prev => ({...prev, search: true}));
      setError('');
      setAvailableWorkplaces([]);

      const formatForApi = (date) => {
        return format(date, "yyyy-MM-dd'T'HH:mm");
      };

      const response = await api.get(
        `/api/booking/findByTime/${formatForApi(timeRange.startTime)}/${formatForApi(timeRange.endTime)}`
      );
      
      setAvailableWorkplaces(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при поиске свободных мест');
      console.error('Search error:', err);
    } finally {
      setLoading(prev => ({...prev, search: false}));
    }
  };

  const handleBookWorkplace = async (workplaceId) => {
    if (!formData.title) {
      return setError('Укажите название бронирования');
    }

    try {
      setLoading(prev => ({...prev, booking: true}));
      setError('');

      const bookingData = {
        title: formData.title,
        description: formData.description,
        workplaceId: Number(workplaceId),
        startTime: format(timeRange.startTime, "yyyy-MM-dd'T'HH:mm"),
        endTime: format(timeRange.endTime, "yyyy-MM-dd'T'HH:mm")
      };

      await api.booking.create(bookingData);
      navigate('/bookings', { state: { success: 'Бронирование успешно создано' } });
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка создания бронирования');
      console.error('Booking error:', err);
    } finally {
      setLoading(prev => ({...prev, booking: false}));
    }
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
      <Box sx={{ maxWidth: 800, mx: 'auto', mt: 4, p: 3 }}>
        <Typography variant="h5" gutterBottom align="center">
          Новое бронирование
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
          <DateTimePicker
            label="Время начала"
            value={timeRange.startTime}
            onChange={(newValue) => setTimeRange({...timeRange, startTime: newValue})}
            format="dd.MM.yyyy HH:mm"
            ampm={false}
            sx={{ flex: 1 }}
          />
          <DateTimePicker
            label="Время окончания"
            value={timeRange.endTime}
            onChange={(newValue) => setTimeRange({...timeRange, endTime: newValue})}
            format="dd.MM.yyyy HH:mm"
            ampm={false}
            sx={{ flex: 1 }}
          />
          <Button
            variant="contained"
            onClick={handleSearchAvailable}
            disabled={loading.search || loading.booking}
            sx={{ height: '56px' }}
          >
            {loading.search ? <CircularProgress size={24} /> : 'Найти свободные места'}
          </Button>
        </Box>


        <TextField
          fullWidth
          label="Название бронирования"
          value={formData.title}
          onChange={(e) => setFormData({...formData, title: e.target.value})}
          margin="normal"
          required
        />
        <TextField
          fullWidth
          label="Описание"
          value={formData.description}
          onChange={(e) => setFormData({...formData, description: e.target.value})}
          margin="normal"
          multiline
          rows={3}
        />

        {availableWorkplaces.length > 0 && (
  <Box sx={{ mt: 3 }}>
    <Typography variant="h6" gutterBottom>
      Доступные рабочие места
    </Typography>
    <List>
      {availableWorkplaces.map((workplace, index) => (
        <ListItem 
          key={`workplace-${workplace.id}-${index}`}
          secondaryAction={
            <Button
              variant="outlined"
              onClick={() => handleBookWorkplace(workplace.id)}
              disabled={loading.booking}
            >
              Забронировать
            </Button>
          }
        >
          <ListItemText
            primary={`Рабочее место #${workplace.id}`}
            secondary={workplace.isBooked ? 'Занято' : 'Свободно'}
          />
        </ListItem>
            ))}
        </List>
    </Box>
    )}
      </Box>
    </LocalizationProvider>
  );
};

export default CreateBookingPage;