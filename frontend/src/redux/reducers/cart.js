import { createReducer } from "@reduxjs/toolkit";
import {
  CART_LOADING,
  CART_SUCCESS,
  CART_ERROR,
  ADD_TO_CART_SUCCESS,
  REMOVE_FROM_CART_SUCCESS,
  CLEAR_CART_SUCCESS
} from "../actions/cart";

const initialState = {
  cart: null,
  items: [],
  loading: false,
  error: null,
};

export const cartReducer = createReducer(initialState, {
  [CART_LOADING]: (state) => {
    state.loading = true;
    state.error = null;
  },

  [CART_SUCCESS]: (state, action) => {
    state.loading = false;
    state.cart = action.payload;
    // Map cartItems to items (backend returns cartItems, frontend expects items)
    // Also transform product.id to product._id for frontend compatibility
    const cartItems = action.payload?.cartItems || action.payload?.items || [];
    state.items = cartItems.map(item => {
      if (item.product && item.product.id && !item.product._id) {
        item.product._id = item.product.id;
      }
      return item;
    });
    state.error = null;
  },

  [ADD_TO_CART_SUCCESS]: (state, action) => {
    state.loading = false;
    state.cart = action.payload;
    // Map cartItems to items (backend returns cartItems, frontend expects items)
    // Also transform product.id to product._id for frontend compatibility
    const cartItems = action.payload?.cartItems || action.payload?.items || [];
    state.items = cartItems.map(item => {
      if (item.product && item.product.id && !item.product._id) {
        item.product._id = item.product.id;
      }
      return item;
    });
    state.error = null;
  },

  [REMOVE_FROM_CART_SUCCESS]: (state, action) => {
    state.loading = false;
    state.cart = action.payload;
    // Map cartItems to items (backend returns cartItems, frontend expects items)
    // Also transform product.id to product._id for frontend compatibility
    const cartItems = action.payload?.cartItems || action.payload?.items || [];
    state.items = cartItems.map(item => {
      if (item.product && item.product.id && !item.product._id) {
        item.product._id = item.product.id;
      }
      return item;
    });
    state.error = null;
  },

  [CLEAR_CART_SUCCESS]: (state) => {
    state.loading = false;
    state.cart = null;
    state.items = [];
    state.error = null;
  },

  [CART_ERROR]: (state, action) => {
    state.loading = false;
    state.error = action.payload;
  },
});
