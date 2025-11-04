import { orderApiInstance } from "../../api/directApiInstances";

// get all orders of user
export const getAllOrdersOfUser = (userId) => async (dispatch) => {
  try {
    dispatch({
      type: "getAllOrdersUserRequest",
    });

    const response = await orderApiInstance.get(`/api/v2/order/get-all-orders/${userId}`);
    
    if (response.data.success && response.data.orders) {
      dispatch({
        type: "getAllOrdersUserSuccess",
        payload: response.data.orders,
      });
    } else {
      dispatch({
        type: "getAllOrdersUserSuccess",
        payload: [],
      });
    }
  } catch (error) {
    console.error("Error fetching user orders:", error);
    dispatch({
      type: "getAllOrdersUserFailed",
      payload: error.response?.data?.message || error.message || "Failed to fetch orders",
    });
    // Still dispatch success with empty array to prevent UI breaking
    dispatch({
      type: "getAllOrdersUserSuccess",
      payload: [],
    });
  }
};

// get all orders of seller
export const getAllOrdersOfShop = (shopId) => async (dispatch) => {
  try {
    if (!shopId) {
      console.warn("getAllOrdersOfShop called without shopId");
      dispatch({
        type: "getAllOrdersShopSuccess",
        payload: [],
      });
      return;
    }

    dispatch({
      type: "getAllOrdersShopRequest",
    });

    console.log("Fetching orders for shopId:", shopId);
    const response = await orderApiInstance.get(`/api/v2/order/get-seller-all-orders/${shopId}`);
    
    console.log("Shop orders response:", response.data);
    
    if (response.data.success && response.data.orders) {
      console.log(`Found ${response.data.orders.length} orders for shop ${shopId}`);
      dispatch({
        type: "getAllOrdersShopSuccess",
        payload: response.data.orders,
      });
    } else {
      console.log("No orders found or response not successful");
      dispatch({
        type: "getAllOrdersShopSuccess",
        payload: [],
      });
    }
  } catch (error) {
    console.error("Error fetching shop orders:", error);
    console.error("Error details:", {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    });
    dispatch({
      type: "getAllOrdersShopFailed",
      payload: error.response?.data?.message || error.message || "Failed to fetch orders",
    });
    // Still dispatch success with empty array to prevent UI breaking
    dispatch({
      type: "getAllOrdersShopSuccess",
      payload: [],
    });
  }
};

// get all orders of Admin
export const getAllOrdersOfAdmin = () => async (dispatch) => {
  try {
    dispatch({
      type: "adminAllOrdersRequest",
    });

    const response = await orderApiInstance.get(`/api/v2/order/admin-all-orders`);
    
    if (response.data.success && response.data.orders) {
      dispatch({
        type: "adminAllOrdersSuccess",
        payload: response.data.orders,
      });
    } else {
      dispatch({
        type: "adminAllOrdersSuccess",
        payload: [],
      });
    }
  } catch (error) {
    console.error("Error fetching admin orders:", error);
    dispatch({
      type: "adminAllOrdersFailed",
      payload: error.response?.data?.message || error.message || "Failed to fetch orders",
    });
    // Still dispatch success with empty array to prevent UI breaking
    dispatch({
      type: "adminAllOrdersSuccess",
      payload: [],
    });
  }
};
