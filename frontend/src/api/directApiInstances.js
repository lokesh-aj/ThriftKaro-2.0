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
      } else {
        // Only warn for POST/PUT/DELETE requests that likely require authentication
        // GET requests to public endpoints don't need warnings
        const isPublicGet = config.method?.toLowerCase() === 'get' && 
          (config.url?.includes('/get-all-products') || 
           config.url?.includes('/get-all-events') ||
           config.url?.includes('/health') ||
           config.url?.includes('/debug'));
        
        if (!isPublicGet) {
          // Only warn for protected endpoints, silently skip for public GETs
          console.warn(`[${instance.defaults.baseURL}] No token found in localStorage for ${config.method?.toUpperCase()} ${config.url}`);
        }
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
        // Token expired or invalid - this is definitely an authentication issue
        console.error('[Auth] 401 Unauthorized - Token expired or invalid');
        const currentPath = window.location.pathname;
        // Only redirect if not already on a login page
        if (currentPath !== '/login' && currentPath !== '/shop-login' && currentPath !== '/sign-up' && currentPath !== '/shop-create') {
          localStorage.removeItem('token');
          localStorage.removeItem('sellerToken');
          localStorage.removeItem('user');
          localStorage.removeItem('seller');
          // Redirect to appropriate login based on current context
          if (currentPath.includes('/dashboard') || currentPath.includes('/shop')) {
            window.location.replace('/shop-login');
          } else {
            window.location.replace('/login');
          }
        }
      } else if (error.response?.status === 403) {
        // Access denied - could be invalid token, expired token, or insufficient permissions
        const errorData = error.response?.data || {};
        const errorMessage = errorData.message || '';
        
        // Check if this is an authentication failure (missing/invalid token) vs authorization (wrong role)
        // Authentication failures typically have messages about missing authentication or invalid tokens
        const isAuthFailure = (errorMessage.includes('Authentication required') && !errorMessage.includes('Only sellers')) || 
                              errorMessage.includes('invalid token') || 
                              errorMessage.includes('expired token') ||
                              (errorMessage.toLowerCase().includes('authentication') && !errorMessage.includes('Only sellers'));
        
        // Check if this is just a permission issue (user is authenticated but lacks role)
        // Permission issues happen when user is authenticated but doesn't have the right role
        const isPermissionIssue = errorMessage.includes('Only sellers can') ||
                                  errorMessage.includes('Only ') && errorMessage.includes(' can ') ||
                                  errorMessage.includes('insufficient permissions');
        
        if (isAuthFailure) {
          // Clear authentication and redirect only on true auth failures
          console.error('[Auth] 403 Forbidden - Authentication failure:', errorMessage);
          const currentPath = window.location.pathname;
          if (currentPath !== '/login' && currentPath !== '/shop-login' && currentPath !== '/sign-up' && currentPath !== '/shop-create') {
            localStorage.removeItem('token');
            localStorage.removeItem('sellerToken');
            localStorage.removeItem('user');
            localStorage.removeItem('seller');
            window.location.replace('/shop-login');
          }
        } else if (isPermissionIssue) {
          // This is a permission issue, not auth - just log it, don't redirect
          console.warn('[Auth] 403 Forbidden - Permission denied:', errorMessage);
          // Don't redirect for permission issues - let the UI handle it
        } else {
          // Unknown 403 - log but don't redirect
          console.error('[Auth] 403 Forbidden - Unknown reason:', errorData);
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
