import React from 'react';
import { List, ListItem, ListItemText, IconButton, Chip, Box, Alert, Typography } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

const BookingList = ({ 
  bookings = [], 
  isAdmin = false, 
  onDelete = null,
  currentUserId = null 
}) => {
  const formatDateTime = (dateTimeStr) => {
    return new Date(dateTimeStr).toLocaleString('ru-RU', {
      day: 'numeric',
      month: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <List>
      {bookings.map((booking) => {
        console.log('booking:', booking);

        return (
          <ListItem key={booking.id || `${booking.workplaceId}-${booking.startTime}`} divider>
            <ListItemText
              primary={
                <Typography variant="body1">
                  {booking.title || 'Без названия'} (с {formatDateTime(booking.startTime)} до {formatDateTime(booking.endTime)})
                </Typography>
              }
              secondary={
                <>
                  <Box component="span" sx={{ display: 'block' }}>
                    ID места: {booking.workplaceId} | Пользователь: {booking.userLogin}
                  </Box>
                  {booking.userId === currentUserId && (
                    <Chip label="Ваша бронь" color="primary" size="small" sx={{ mt: 1 }} />
                  )}
                </>
              }
            />

            

            {(isAdmin || booking.userId === currentUserId) && onDelete && (
              <IconButton onClick={() => onDelete(booking.id)}>
                <DeleteIcon color="error" />
              </IconButton>
            )}
          </ListItem>
        );
      })}
    </List>
  );
};

export default BookingList;