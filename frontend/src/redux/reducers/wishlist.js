import { createReducer } from "@reduxjs/toolkit";

const initialState = {
  wishlist: (() => {
    try {
      const wishlistItems = localStorage.getItem("wishlistItems");
      return wishlistItems ? JSON.parse(wishlistItems) : [];
    } catch (error) {
      console.error('Error parsing wishlist from localStorage:', error);
      return [];
    }
  })(),
};

export const wishlistReducer = createReducer(initialState, {
  addToWishlist: (state, action) => {
    const item = action.payload;
    const isItemExist = state.wishlist.find((i) => i._id === item._id);
    if (isItemExist) {
      return {
        ...state,
        wishlist: state.wishlist.map((i) =>
          i._id === isItemExist._id ? item : i
        ),
      };
    } else {
      return {
        ...state,
        wishlist: [...state.wishlist, item],
      };
    }
  },

  removeFromWishlist: (state, action) => {
    return {
      ...state,
      wishlist: state.wishlist.filter((i) => i._id !== action.payload),
    };
  },
});
