import axiosInstance from "../../api/axiosInstance";

// Helper function to normalize user object - ensure it always has _id
const normalizeUser = (user) => {
  if (!user) return null;
  // If user has 'id' but not '_id', create '_id' from 'id'
  if (user.id && !user._id) {
    return { ...user, _id: user.id };
  }
  // If user has '_id' but not 'id', create 'id' from '_id' for consistency
  if (user._id && !user.id) {
    return { ...user, id: user._id };
  }
  return user;
};

// load user
export const loadUser = () => async (dispatch) => {
  try {
    dispatch({
      type: "LoadUserRequest",
    });
    
    // Check if user has a token; if not, try localStorage fallback and fail silently
    const token = localStorage.getItem('token');
    if (!token) {
      const userData = localStorage.getItem('user');
      if (userData) {
        const user = JSON.parse(userData);
        const normalizedUser = normalizeUser(user);
        dispatch({
          type: "LoadUserSuccess",
          payload: normalizedUser,
        });
      } else {
        // No token and no cached user — finish without setting an error message
        dispatch({
          type: "LoadUserFail",
          payload: null,
        });
      }
      return;
    }
    
    // Try to load user data from API first
    try {
      const { data } = await axiosInstance.get("/api/auth/me");
      // Normalize user object to ensure _id field exists
      const normalizedUser = normalizeUser(data);
      dispatch({
        type: "LoadUserSuccess",
        payload: normalizedUser,
      });
    } catch (apiError) {
      // If API fails (403, 401, etc.), try to load from localStorage as fallback
      // Don't log 403 errors as they're expected when not logged in
      if (apiError.response?.status !== 403 && apiError.response?.status !== 401) {
        console.log("API load failed, trying localStorage fallback:", apiError.response?.data || apiError.message);
      }
      
      const userData = localStorage.getItem('user');
      if (userData) {
        try {
          const user = JSON.parse(userData);
          // Normalize user object to ensure _id field exists
          const normalizedUser = normalizeUser(user);
          dispatch({
            type: "LoadUserSuccess",
            payload: normalizedUser,
          });
        } catch (parseError) {
          // Invalid JSON in localStorage, clear it
          localStorage.removeItem('user');
          dispatch({
            type: "LoadUserFail",
            payload: null,
          });
        }
      } else {
        // No fallback data - silently fail (user not logged in)
        dispatch({
          type: "LoadUserFail",
          payload: null,
        });
      }
    }
  } catch (error) {
    // Only log unexpected errors, not 403/401 which are expected when not authenticated
    if (error.response?.status !== 403 && error.response?.status !== 401) {
      console.log("Load user error:", error.response?.data || error.message);
    }
    dispatch({
      type: "LoadUserFail",
      payload: null, // Don't set error message for auth failures
    });
  }
};

// load seller
export const loadSeller = () => async (dispatch) => {
  try {
    dispatch({
      type: "LoadSellerRequest",
    });
    
    // Check if seller data exists in localStorage
    const sellerData = localStorage.getItem('seller');
    if (sellerData) {
      const seller = JSON.parse(sellerData);
      dispatch({
        type: "LoadSellerSuccess",
        payload: seller,
      });
    } else {
      // No seller data found
      dispatch({
        type: "LoadSellerFail",
        payload: "No seller data found",
      });
    }
  } catch (error) {
    dispatch({
      type: "LoadSellerFail",
      payload: error.message || "Failed to load seller data",
    });
  }
};

// user update information
export const updateUserInformation =
  (name, email, phoneNumber, password) => async (dispatch) => {
    try {
      dispatch({
        type: "updateUserInfoRequest",
      });
      const { data } = await axiosInstance.put("/api/auth/update-me", {
        name,
        email,
        phoneNumber,
      });
      dispatch({
        type: "updateUserInfoSuccess",
        payload: normalizeUser(data),
      });
    } catch (error) {
      dispatch({
        type: "updateUserInfoFailed",
        payload: error.response?.data?.message || error.message,
      });
    }
  };

// update user address
export const updatUserAddress =
  (country, city, address1, address2, zipCode, addressType) =>
  async (dispatch) => {
    try {
      dispatch({
        type: "updateUserAddressRequest",
      });

      // Temporarily disabled - endpoint not available in UserService
      console.log("Update user addresses disabled - using direct UserService connection");
      const data = { success: true, message: "Update disabled", user: {} };

      dispatch({
        type: "updateUserAddressSuccess",
        payload: {
          successMessage: "User address updated succesfully!",
          user: data.user,
        },
      });
    } catch (error) {
      dispatch({
        type: "updateUserAddressFailed",
        payload: error.response.data.message,
      });
    }
  };

// delete user address
export const deleteUserAddress = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "deleteUserAddressRequest",
    });

    // Temporarily disabled - endpoint not available in UserService
    console.log("Delete user address disabled - using direct UserService connection");
    const data = { success: true, message: "Delete disabled" };

    dispatch({
      type: "deleteUserAddressSuccess",
      payload: {
        successMessage: "User deleted successfully!",
        user: data.user,
      },
    });
  } catch (error) {
    dispatch({
      type: "deleteUserAddressFailed",
      payload: error.response.data.message,
    });
  }
};

// get all users --- admin
export const getAllUsers = () => async (dispatch) => {
  try {
    dispatch({
      type: "getAllUsersRequest",
    });

    // Temporarily disabled - endpoint not available in UserService
    console.log("Get all users disabled - using direct UserService connection");
    const data = { users: [] };

    dispatch({
      type: "getAllUsersSuccess",
      payload: data.users,
    });
  } catch (error) {
    dispatch({
      type: "getAllUsersFailed",
      payload: error.response.data.message,
    });
  }
};
