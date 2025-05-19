import React, { useState } from 'react';
import { TextField, Button, Box, Typography } from '@mui/material';

const SpaceForm = ({ onSubmit }) => {
  const [count, setCount] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    const numCount = parseInt(count);
    if (!numCount || numCount <= 0) return;

    onSubmit({ count: numCount });
    setCount('');
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: 500, mx: 'auto', mt: 2 }}>
      <Typography variant="h6" gutterBottom>Добавить несколько рабочих мест</Typography>
      <TextField
        fullWidth
        label="Количество мест"
        type="number"
        value={count}
        onChange={(e) => setCount(e.target.value)}
        margin="normal"
        required
        inputProps={{ min: 1 }}
      />
      <Button type="submit" variant="contained" color="primary" fullWidth>
        Добавить места
      </Button>
    </Box>
  );
};

export default SpaceForm;