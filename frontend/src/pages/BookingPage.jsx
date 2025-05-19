import React, { useState, useEffect } from 'react';
import { DateTimePicker } from '@mui/x-date-pickers';
import { Button, List, ListItem, TextField, Typography } from '@mui/material';
import api from '../api';

const BookingPage = () => {
  const [workplaces, setWorkplaces] = useState([]);
  const [filteredWorkplaces, setFilteredWorkplaces] = useState([]);
  const [startTime, setStartTime] = useState(null);
  const [endTime, setEndTime] = useState(null);
  const [bookingData, setBookingData] = useState({
    title: '',
    description: ''
  });

  useEffect(() => {
    api.workplace.getAll()
      .then(res => setWorkplaces(res.data))
      .catch(err => console.error('Ошибка загрузки мест:', err));
  }, []);

  const handleBook = async (workplaceId) => {
    try {
      await api.workplace.book(workplaceId, {
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        ...bookingData
      });
      alert('Бронирование успешно создано!');
      setBookingData({ title: '', description: '' });
      setStartTime(null);
      setEndTime(null);
    } catch (err) {
      alert(`Ошибка: ${err.response?.data?.message || err.message}`);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <Typography variant="h4" gutterBottom>Бронирование коворкинга</Typography>
      
      <div style={{ marginBottom: '20px' }}>
        <DateTimePicker
          label="Начало"
          value={startTime}
          onChange={setStartTime}
          sx={{ marginRight: '10px' }}
        />
        <DateTimePicker
          label="Конец"
          value={endTime}
          onChange={setEndTime}
        />
        <Button 
          onClick={handleFilter} 
          variant="contained" 
          sx={{ marginLeft: '10px' }}
        >
          Найти свободные
        </Button>
      </div>

      <TextField
        label="Название брони"
        value={bookingData.title}
        onChange={(e) => setBookingData({...bookingData, title: e.target.value})}
        fullWidth
        sx={{ marginBottom: '10px' }}
      />
      <TextField
        label="Описание"
        value={bookingData.description}
        onChange={(e) => setBookingData({...bookingData, description: e.target.value})}
        fullWidth
        multiline
        rows={2}
      />

      <Typography variant="h6" sx={{ marginTop: '20px' }}>
        {filteredWorkplaces.length ? 'Свободные места' : 'Все места'}
      </Typography>
      <List>
        {(filteredWorkplaces.length ? filteredWorkplaces : workplaces).map(wp => (
          <ListItem key={wp.id} divider>
            <ListItemText
              primary={wp.name}
              secondary={`Вместимость: ${wp.capacity} | ${wp.description}`}
            />
            <Button 
              variant="outlined" 
              onClick={() => handleBook(wp.id)}
            >
              Забронировать
            </Button>
          </ListItem>
        ))}
      </List>
    </div>
  );
};

export default BookingPage;