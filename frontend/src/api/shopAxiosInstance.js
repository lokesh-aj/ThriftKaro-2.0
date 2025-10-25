import axios from 'axios';

// Create axios instance for shop operations - Direct connection to Shop Service
const shopAxiosInstance = axios.create({
  baseURL: "http://localhost:8089", // Direct connection to Shop Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token to all requests
shopAxiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration
shopAxiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      localStorage.removeItem('seller');
      window.location.href = '/shop-login';
    }
    return Promise.reject(error);
  }
);

export default shopAxiosInstance;

