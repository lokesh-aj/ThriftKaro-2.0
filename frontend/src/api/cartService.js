import { cartApiInstance } from './directApiInstances';

// Cart API service functions - Direct connection to Cart Service
export const cartService = {
  // Create or fetch cart for a user
  createOrFetchCart: async (userId) => {
    if (!userId) {
      throw new Error('User ID is required to create or fetch cart');
    }
    try {
      const response = await cartApiInstance.post(`/api/v2/cart/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get user's cart
  getCart: async (userId) => {
    if (!userId) {
      throw new Error('User ID is required to get cart');
    }
    try {
      const response = await cartApiInstance.get(`/api/v2/cart/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Add item to cart
  addItemToCart: async (cartId, productId, quantity) => {
    try {
      if (!productId) {
        console.error('cartService.addItemToCart: productId is null or undefined', { cartId, productId, quantity });
        throw new Error('Product ID is required to add item to cart');
      }
      if (!cartId) {
        console.error('cartService.addItemToCart: cartId is null or undefined', { cartId, productId, quantity });
        throw new Error('Cart ID is required to add item to cart');
      }
      
      const requestBody = {
        productId: productId,
        quantity: quantity || 1
      };
      
      console.log('cartService.addItemToCart - Request:', {
        url: `/api/v2/cart/${cartId}/items`,
        body: requestBody
      });
      
      const response = await cartApiInstance.post(`/api/v2/cart/${cartId}/items`, requestBody);
      return response.data;
    } catch (error) {
      console.error('cartService.addItemToCart - Error:', error);
      throw error;
    }
  },

  // Remove item from cart
  removeItemFromCart: async (cartId, productId) => {
    try {
      const response = await cartApiInstance.delete(`/api/v2/cart/${cartId}/items/${productId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Clear entire cart
  clearCart: async (cartId) => {
    try {
      const response = await cartApiInstance.delete(`/api/v2/cart/${cartId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

export default cartService;
