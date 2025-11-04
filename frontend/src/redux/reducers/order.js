import { createReducer } from "@reduxjs/toolkit";

const initialState = {
  isLoading: true,
};

export const orderReducer = createReducer(initialState, {
  // get all orders of user
  getAllOrdersUserRequest: (state) => {
    state.isLoading = true;
  },
  getAllOrdersUserSuccess: (state, action) => {
    state.isLoading = false;
    // Normalize orders to ensure they have id field for DataGrid
    state.orders = (action.payload || []).map((order, index) => ({
      ...order,
      id: order.id || order._id || `order-${index}`,
      _id: order._id || order.id || `order-${index}`,
      cart: Array.isArray(order.cart) ? order.cart : [],
      totalPrice: order.totalPrice || 0,
      status: order.status || "Unknown",
    }));
  },
  getAllOrdersUserFailed: (state, action) => {
    state.isLoading = false;
    state.error = action.payload;
  },
  
  // get all orders of shop
  getAllOrdersShopRequest: (state) => {
    state.isLoading = true;
  },
  getAllOrdersShopSuccess: (state, action) => {
    state.isLoading = false;
    // Normalize orders to ensure they have id field for DataGrid
    state.orders = (action.payload || []).map((order, index) => ({
      ...order,
      id: order.id || order._id || `order-${index}`,
      _id: order._id || order.id || `order-${index}`,
      cart: Array.isArray(order.cart) ? order.cart : [],
      totalPrice: order.totalPrice || 0,
      status: order.status || "Unknown",
    }));
  },
  getAllOrdersShopFailed: (state, action) => {
    state.isLoading = false;
    state.error = action.payload;
  },

  // get all orders for admin
  adminAllOrdersRequest: (state) => {
    state.adminOrderLoading = true;
  },
  adminAllOrdersSuccess: (state, action) => {
    state.adminOrderLoading = false;
    // Normalize orders to ensure they have id field for DataGrid
    state.adminOrders = (action.payload || []).map((order, index) => ({
      ...order,
      id: order.id || order._id || `order-${index}`,
      _id: order._id || order.id || `order-${index}`,
      cart: Array.isArray(order.cart) ? order.cart : [],
      totalPrice: order.totalPrice || 0,
      status: order.status || "Unknown",
    }));
  },
  adminAllOrdersFailed: (state, action) => {
    state.adminOrderLoading = false;
    state.error = action.payload;
  },

  clearErrors: (state) => {
    state.error = null;
  },
});
