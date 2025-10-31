import axios from 'axios';

// Direct API instances for each microservice
export const userApiInstance = axios.create({
  baseURL: "http://localhost:8082", // User Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const shopApiInstance = axios.create({
  baseURL: "http://localhost:8089", // Shop Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const productApiInstance = axios.create({
  baseURL: "http://localhost:8083", // Product Service
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const cartApiInstance = axios.create({
  baseURL: "http://localhost:8088", // Cart Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const orderApiInstance = axios.create({
  baseURL: "http://localhost:8084", // Order Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const paymentApiInstance = axios.create({
  baseURL: "http://localhost:8085", // Payment Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const chatApiInstance = axios.create({
  baseURL: "http://localhost:8087", // Chat Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const notificationApiInstance = axios.create({
  baseURL: "http://localhost:8086", // Notification Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const eventApiInstance = axios.create({
  baseURL: "http://localhost:8090", // Event Service
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Common request interceptor for all instances
const addAuthInterceptor = (instance) => {
  instance.interceptors.request.use(
    (config) => {
      // Prefer seller token for seller-protected services; fall back to generic token
      const token = localStorage.getItem('sellerToken') || localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
};

// Common response interceptor for all instances
const addResponseInterceptor = (instance) => {
  instance.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      if (error.response?.status === 401) {
        // Token expired or invalid
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('seller');
        const currentPath = window.location.pathname;
        if (currentPath !== '/login' && currentPath !== '/shop-login') {
          window.location.replace('/login');
        }
      }
      return Promise.reject(error);
    }
  );
};

// Apply interceptors to all instances
const instances = [
  userApiInstance,
  shopApiInstance,
  productApiInstance,
  cartApiInstance,
  orderApiInstance,
  paymentApiInstance,
  chatApiInstance,
  notificationApiInstance,
  eventApiInstance
];

instances.forEach(instance => {
  addAuthInterceptor(instance);
  addResponseInterceptor(instance);
});

export default {
  user: userApiInstance,
  shop: shopApiInstance,
  product: productApiInstance,
  cart: cartApiInstance,
  order: orderApiInstance,
  payment: paymentApiInstance,
  chat: chatApiInstance,
  notification: notificationApiInstance,
  event: eventApiInstance
};
