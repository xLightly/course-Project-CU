import React from 'react';
import { List, ListItem, ListItemText, IconButton, Box, Typography } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';

const SpaceList = ({ spaces, onDelete, onEdit }) => (
  <List>
    {spaces.map((space) => (
      <ListItem key={space.id} divider>
        <ListItemText
          primary={`${space.name} (Занят: ${space.isBooked})`}
          secondary={space.description}
        />
        <Box>
          <IconButton edge="end" aria-label="edit" onClick={() => onEdit(space)}>
            <EditIcon color="primary" />
          </IconButton>
          <IconButton edge="end" aria-label="delete" onClick={() => onDelete(space.id)}>
            <DeleteIcon color="error" />
          </IconButton>
        </Box>
      </ListItem>
    ))}
  </List>
);

export default SpaceList;