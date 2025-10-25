import { paymentApiInstance } from './directApiInstances';

// Payment service for ThriftKaro 2.0 microservices - Direct connection to Payment Service
export const paymentService = {
  // Get Stripe public API key
  getStripeApiKey: async () => {
    try {
      const response = await paymentApiInstance.get('/api/v2/payment/stripe/key');
      return response.data;
    } catch (error) {
      console.error('Error fetching Stripe API key:', error);
      throw error;
    }
  },

  // Check payment service health
  checkHealth: async () => {
    try {
      const response = await paymentApiInstance.get('/api/v2/payment/health');
      return response.data;
    } catch (error) {
      console.error('Error checking payment service health:', error);
      throw error;
    }
  },

  // Initiate payment
  initiatePayment: async (orderId, userId, amount) => {
    try {
      const response = await paymentApiInstance.post('/api/v2/payment/create', {
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
      const response = await paymentApiInstance.get(`/api/v2/payment/status/${orderId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting payment status:', error);
      throw error;
    }
  },

  // Process refund
  processRefund: async (orderId) => {
    try {
      const response = await paymentApiInstance.post(`/api/v2/payment/refund/${orderId}`);
      return response.data;
    } catch (error) {
      console.error('Error processing refund:', error);
      throw error;
    }
  }
};

export default paymentService;







