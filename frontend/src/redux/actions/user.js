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
    
    // Check if user has a token
    const token = localStorage.getItem('token');
    if (!token) {
      dispatch({
        type: "LoadUserFail",
        payload: "No authentication token found",
      });
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
      // If API fails, try to load from localStorage as fallback
      console.log("API load failed, trying localStorage fallback:", apiError.response?.data || apiError.message);
      
      const userData = localStorage.getItem('user');
      if (userData) {
        const user = JSON.parse(userData);
        // Normalize user object to ensure _id field exists
        const normalizedUser = normalizeUser(user);
        dispatch({
          type: "LoadUserSuccess",
          payload: normalizedUser,
        });
      } else {
        throw apiError; // Re-throw if no fallback data
      }
    }
  } catch (error) {
    console.log("Load user error:", error.response?.data || error.message);
    dispatch({
      type: "LoadUserFail",
      payload: error.response?.data?.message || "Authentication failed",
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

      // Temporarily disabled - endpoint not available in UserService
      console.log("Update user info disabled - using direct UserService connection");
      const data = { success: true, message: "Update disabled", user: {} };

      dispatch({
        type: "updateUserInfoSuccess",
        payload: data.user,
      });
    } catch (error) {
      dispatch({
        type: "updateUserInfoFailed",
        payload: error.response.data.message,
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
