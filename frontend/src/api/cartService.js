import axiosInstance from './axiosInstance';

// Cart API service functions
export const cartService = {
  // Create or fetch cart for a user
  createOrFetchCart: async (userId) => {
    try {
      const response = await axiosInstance.post(`/carts/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get user's cart
  getCart: async (userId) => {
    try {
      const response = await axiosInstance.get(`/carts/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Add item to cart
  addItemToCart: async (cartId, productId, quantity) => {
    try {
      const response = await axiosInstance.post(`/carts/${cartId}/items`, {
        productId,
        quantity
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Remove item from cart
  removeItemFromCart: async (cartId, productId) => {
    try {
      const response = await axiosInstance.delete(`/carts/${cartId}/items/${productId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Clear entire cart
  clearCart: async (cartId) => {
    try {
      const response = await axiosInstance.delete(`/carts/${cartId}/clear`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

export default cartService;
