import React, { useEffect, useState } from 'react';
import { Typography, Box, CircularProgress, Alert } from '@mui/material';
import BookingList from '../components/BookingList.jsx';
import api from '../api.js';

const BookingListPage = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const currentUserId = Number(localStorage.getItem('userId'));
  const isAdmin = localStorage.getItem('isAdmin') === 'true';

  const handleDeleteBooking = async (bookingId) => {
    if (!bookingId) return;

    try {
      await api.booking.delete(bookingId);
      setBookings(prev => prev.filter(b => b.id !== bookingId));
    } catch (err) {
      setError(err.message || 'Ошибка при удалении');
    }
  };

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const res = await api.get('/api/booking');
        setBookings(res.data);
      } catch (err) {
        setError('Не удалось загрузить бронирования');
      } finally {
        setLoading(false);
      }
    };
    fetchBookings();
  }, []);

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Box sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Мои бронирования</Typography>
      <BookingList 
        bookings={bookings} 
        currentUserId={currentUserId} 
        isAdmin={isAdmin} 
        onDelete={handleDeleteBooking}
      />
    </Box>
  );
};

export default BookingListPage;