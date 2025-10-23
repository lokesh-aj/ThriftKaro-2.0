import axiosInstance from './axiosInstance';

// Payment service for ThriftKaro 2.0 microservices
export const paymentService = {
  // Get Stripe public API key
  getStripeApiKey: async () => {
    try {
      const response = await axiosInstance.get('/payment/stripeapikey');
      return response.data;
    } catch (error) {
      console.error('Error fetching Stripe API key:', error);
      throw error;
    }
  },

  // Check payment service health
  checkHealth: async () => {
    try {
      const response = await axiosInstance.get('/payment/health');
      return response.data;
    } catch (error) {
      console.error('Error checking payment service health:', error);
      throw error;
    }
  },

  // Initiate payment (for future use with new microservice)
  initiatePayment: async (orderId, userId, amount) => {
    try {
      const response = await axiosInstance.post('/payment/initiate', {
        orderId,
        userId,
        amount
      });
      return response.data;
    } catch (error) {
      console.error('Error initiating payment:', error);
      throw error;
    }
  },

  // Get payment status
  getPaymentStatus: async (orderId) => {
    try {
      const response = await axiosInstance.get(`/payment/status/${orderId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting payment status:', error);
      throw error;
    }
  },

  // Process refund
  processRefund: async (orderId) => {
    try {
      const response = await axiosInstance.post(`/payment/refund/${orderId}`);
      return response.data;
    } catch (error) {
      console.error('Error processing refund:', error);
      throw error;
    }
  }
};

export default paymentService;


