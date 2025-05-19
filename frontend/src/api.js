import axios from 'axios';

const api = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000,
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('isAdmin');
      window.location.href = '/login';
    }
    
    if (error.response) {
      error.message = error.response.data?.message || 
                     `Server error: ${error.response.status}`;
    }
    
    return Promise.reject(error);
  }
);

api.booking = {
  create: (data) => api.post('/api/booking', data),
  delete: (id) => api.delete(`/api/booking/${id}`),
  findByTime: (startTime, endTime) => 
    api.get(`/api/booking/findByTime/${startTime}/${endTime}`)
};

api.workplace = {
  getAll: () => api.get('/api/workplace'),
  getById: (id) => api.get(`/api/workplace/${id}`)
};

api.auth = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData)
};

api.admin = {
  createWorkplace: (count) => api.post(`/api/admin/workplace/${count}`),
  updateWorkplace: (id, data) => api.put(`/api/admin/workplace/${id}`, data),
  deleteWorkplace: (id) => api.delete(`/api/admin/workplace/${id}`),
  
  getAllBookings: () => api.get('/api/admin/bookings'),
  deleteBooking: (id) => api.delete(`/api/admin/bookings/${id}`),
  
  promoteToAdmin: (login) => api.post(`/api/admin/promoteToAdmin/${login}`,),
  checkAdmin: () => api.get('/api/admin/check')
};

export default api;