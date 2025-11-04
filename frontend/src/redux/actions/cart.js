import cartService from '../../api/cartService';
import { toast } from 'react-toastify';

// Action types
export const CART_LOADING = 'CART_LOADING';
export const CART_SUCCESS = 'CART_SUCCESS';
export const CART_ERROR = 'CART_ERROR';
export const ADD_TO_CART_SUCCESS = 'ADD_TO_CART_SUCCESS';
export const REMOVE_FROM_CART_SUCCESS = 'REMOVE_FROM_CART_SUCCESS';
export const CLEAR_CART_SUCCESS = 'CLEAR_CART_SUCCESS';

// Load user's cart
export const loadCart = (userId) => async (dispatch) => {
  if (!userId) {
    console.error('loadCart called without userId');
    dispatch({ 
      type: CART_ERROR, 
      payload: 'User ID is required to load cart' 
    });
    return;
  }
  
  try {
    dispatch({ type: CART_LOADING });
    const cart = await cartService.getCart(userId);
    dispatch({ 
      type: CART_SUCCESS, 
      payload: cart 
    });
    return cart;
  } catch (error) {
    console.error('Error loading cart:', error);
    dispatch({ 
      type: CART_ERROR, 
      payload: error.response?.data?.message || error.message || 'Failed to load cart' 
    });
    throw error;
  }
};

// Add to cart
export const addTocart = (productData, userId) => async (dispatch, getState) => {
  if (!userId) {
    console.error('addTocart called without userId');
    dispatch({ 
      type: CART_ERROR, 
      payload: 'User ID is required to add items to cart' 
    });
    toast.error('User ID is required to add items to cart');
    return;
  }
  
  try {
    dispatch({ type: CART_LOADING });
    
    // Validate product data
    const productId = productData._id || productData.id;
    if (!productId) {
      console.error('addTocart: productData missing _id or id', productData);
      toast.error('Product ID is missing. Cannot add to cart.');
      dispatch({ 
        type: CART_ERROR, 
        payload: 'Product ID is required to add items to cart' 
      });
      return;
    }
    
    // Use the simpler endpoint that automatically handles cart creation
    const updatedCart = await cartService.addItemToUserCart(
      productId, 
      productData.qty || 1
    );
    
    dispatch({ 
      type: ADD_TO_CART_SUCCESS, 
      payload: updatedCart 
    });
    
    toast.success('Item added to cart successfully!');
    return updatedCart;
  } catch (error) {
    console.error('Error adding to cart:', error);
    const errorMessage = error.response?.data?.message || error.message || 'Failed to add item to cart';
    dispatch({ 
      type: CART_ERROR, 
      payload: errorMessage
    });
    toast.error(errorMessage);
    throw error;
  }
};

// Remove from cart
export const removeFromCart = (productId, userId) => async (dispatch, getState) => {
  try {
    dispatch({ type: CART_LOADING });
    
    const { cart } = getState().cart;
    const cartId = cart?.cartId || cart?.id;
    
    if (!cartId) {
      throw new Error('No cart found');
    }
    
    const updatedCart = await cartService.removeItemFromCart(cartId, productId);
    
    dispatch({ 
      type: REMOVE_FROM_CART_SUCCESS, 
      payload: updatedCart 
    });
    
    toast.success('Item removed from cart successfully!');
    return updatedCart;
  } catch (error) {
    dispatch({ 
      type: CART_ERROR, 
      payload: error.response?.data?.message || 'Failed to remove item from cart' 
    });
    toast.error(error.response?.data?.message || 'Failed to remove item from cart');
    throw error;
  }
};

// Clear entire cart
export const clearCart = (userId) => async (dispatch, getState) => {
  try {
    dispatch({ type: CART_LOADING });
    
    const { cart } = getState().cart;
    const cartId = cart?.cartId || cart?.id;
    
    if (!cartId) {
      throw new Error('No cart found');
    }
    
    await cartService.clearCart(cartId);
    
    dispatch({ 
      type: CLEAR_CART_SUCCESS 
    });
    
    toast.success('Cart cleared successfully!');
  } catch (error) {
    dispatch({ 
      type: CART_ERROR, 
      payload: error.response?.data?.message || 'Failed to clear cart' 
    });
    toast.error(error.response?.data?.message || 'Failed to clear cart');
    throw error;
  }
};

// Update item quantity
export const updateCartItemQuantity = (productId, newQuantity, userId) => async (dispatch, getState) => {
  try {
    dispatch({ type: CART_LOADING });
    
    const { cart } = getState().cart;
    const cartId = cart?.cartId || cart?.id;
    
    if (!cartId) {
      throw new Error('No cart found');
    }
    
    if (newQuantity <= 0) {
      // Remove item if quantity is 0 or less
      return dispatch(removeFromCart(productId, userId));
    }
    
    // Update quantity by removing and re-adding with new quantity
    await cartService.removeItemFromCart(cartId, productId);
    const updatedCart = await cartService.addItemToCart(cartId, productId, newQuantity);
    
    dispatch({ 
      type: ADD_TO_CART_SUCCESS, 
      payload: updatedCart 
    });
    
    return updatedCart;
  } catch (error) {
    dispatch({ 
      type: CART_ERROR, 
      payload: error.response?.data?.message || 'Failed to update item quantity' 
    });
    toast.error(error.response?.data?.message || 'Failed to update item quantity');
    throw error;
  }
};
