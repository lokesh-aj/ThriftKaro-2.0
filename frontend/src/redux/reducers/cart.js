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
    state.items = action.payload?.items || [];
    state.error = null;
  },

  [ADD_TO_CART_SUCCESS]: (state, action) => {
    state.loading = false;
    state.cart = action.payload;
    state.items = action.payload?.items || [];
    state.error = null;
  },

  [REMOVE_FROM_CART_SUCCESS]: (state, action) => {
    state.loading = false;
    state.cart = action.payload;
    state.items = action.payload?.items || [];
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
