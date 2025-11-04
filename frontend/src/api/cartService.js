import { cartApiInstance } from './directApiInstances';

// Cart API service functions - Direct connection to Cart Service
export const cartService = {
  // Create or fetch cart for a user
  createOrFetchCart: async (userId) => {
    if (!userId) {
      throw new Error('User ID is required to create or fetch cart');
    }
    try {
      // First try to get existing cart
      const getResponse = await cartApiInstance.get(`/api/v2/cart/${userId}`).catch(() => null);
      if (getResponse && getResponse.data) {
        return getResponse.data;
      }
      // If no cart exists, create one
      const response = await cartApiInstance.post(`/api/v2/cart/${userId}`);
      return response.data;
    } catch (error) {
      console.error('createOrFetchCart error:', {
        userId,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
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

  // Add item to user's cart (simpler endpoint that handles cart creation automatically)
  addItemToUserCart: async (productId, quantity) => {
    try {
      if (!productId) {
        console.error('cartService.addItemToUserCart: productId is null or undefined', { productId, quantity });
        throw new Error('Product ID is required to add item to cart');
      }
      
      const requestBody = {
        productId: productId,
        quantity: quantity || 1
      };
      
      console.log('cartService.addItemToUserCart - Request:', {
        url: `/api/v2/cart/items`,
        body: requestBody
      });
      
      const response = await cartApiInstance.post(`/api/v2/cart/items`, requestBody);
      return response.data;
    } catch (error) {
      console.error('cartService.addItemToUserCart - Error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
      throw error;
    }
  },

  // Add item to cart (requires cartId - kept for backward compatibility)
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
