import axiosInstance from "../../api/axiosInstance";

// get all sellers --- admin
export const getAllSellers = () => async (dispatch) => {
  try {
    dispatch({
      type: "getAllSellersRequest",
    });

    // Temporarily disabled - ShopService not available when connecting directly to UserService
    console.log("Get all sellers disabled - using direct UserService connection");
    const data = { sellers: [] };

    dispatch({
      type: "getAllSellersSuccess",
      payload: data.sellers,
    });
  } catch (error) {
    dispatch({
      type: "getAllSellerFailed",
    //   payload: error.response.data.message,
    });
  }
};
