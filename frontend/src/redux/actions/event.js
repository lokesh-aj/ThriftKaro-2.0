import { eventApiInstance } from "../../api/directApiInstances";

// create event
export const createevent = (data) => async (dispatch) => {
  try {
    dispatch({
      type: "eventCreateRequest",
    });
    
    console.log("Creating event with data:", {
      name: data.name,
      shopId: data.shopId,
      imagesCount: data.images?.length || 0,
      hasStartDate: !!data.start_Date,
      hasEndDate: !!data.Finish_Date
    });
    
    const res = await eventApiInstance.post("/api/v2/event/create-event", data);
    
    if (res.data.success && res.data.event) {
      dispatch({
        type: "eventCreateSuccess",
        payload: res.data.event,
      });
    } else {
      throw new Error(res.data.message || "Event creation failed");
    }
  } catch (error) {
    console.error("Event creation error:", error);
    const errorMessage = error.code === 'ECONNABORTED' 
      ? "Request timed out. Please check your connection and try again."
      : error.response?.data?.message || error.message || "Failed to create event";
    
    dispatch({
      type: "eventCreateFail",
      payload: errorMessage,
    });
  }
};

// get all events of a shop
export const getAllEventsShop = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "getAlleventsShopRequest",
    });
    const res = await eventApiInstance.get(`/api/v2/event/get-all-events/${id}`);
    dispatch({
      type: "getAlleventsShopSuccess",
      payload: res.data.events,
    });
  } catch (error) {
    dispatch({
      type: "getAlleventsShopFailed",
      payload: error.response?.data?.message || error.message,
    });
  }
};

// delete event of a shop
export const deleteEvent = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "deleteeventRequest",
    });
    const res = await eventApiInstance.delete(`/api/v2/event/delete-shop-event/${id}`);
    dispatch({
      type: "deleteeventSuccess",
      payload: res.data.message,
    });
  } catch (error) {
    dispatch({
      type: "deleteeventFailed",
      payload: error.response?.data?.message || error.message,
    });
  }
};

// get all events
export const getAllEvents = () => async (dispatch) => {
  try {
    dispatch({
      type: "getAlleventsRequest",
    });
    const res = await eventApiInstance.get("/api/v2/event/get-all-events");
    dispatch({
      type: "getAlleventsSuccess",
      payload: res.data.events,
    });
  } catch (error) {
    dispatch({
      type: "getAlleventsFailed",
      payload: error.response?.data?.message || error.message,
    });
  }
};
