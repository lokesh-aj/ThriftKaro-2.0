import axiosInstance from "../../api/axiosInstance";

// create event
export const createevent = (data) => async (dispatch) => {
  try {
    dispatch({
      type: "eventCreateRequest",
    });

    // Temporarily disabled - EventService not available when connecting directly to UserService
    console.log("Create event disabled - using direct UserService connection");
    const d = { success: true, message: "Event creation disabled" };
    dispatch({
      type: "eventCreateSuccess",
      payload: d.event,
    });
  } catch (error) {
    dispatch({
      type: "eventCreateFail",
      payload: error.response.data.message,
    });
  }
};

// get all events of a shop
export const getAllEventsShop = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "getAlleventsShopRequest",
    });

    // Temporarily disabled - EventService not available when connecting directly to UserService
    console.log("Get all events disabled - using direct UserService connection");
    const data = { events: [] };
    dispatch({
      type: "getAlleventsShopSuccess",
      payload: data.events,
    });
  } catch (error) {
    dispatch({
      type: "getAlleventsShopFailed",
      payload: error.response.data.message,
    });
  }
};

// delete event of a shop
export const deleteEvent = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "deleteeventRequest",
    });

    // Temporarily disabled - EventService not available when connecting directly to UserService
    console.log("Delete event disabled - using direct UserService connection");
    const data = { success: true, message: "Event deletion disabled" };

    dispatch({
      type: "deleteeventSuccess",
      payload: data.message,
    });
  } catch (error) {
    dispatch({
      type: "deleteeventFailed",
      payload: error.response.data.message,
    });
  }
};

// get all events
export const getAllEvents = () => async (dispatch) => {
  try {
    dispatch({
      type: "getAlleventsRequest",
    });

    // Temporarily disabled - EventService not available when connecting directly to UserService
    console.log("Get all events disabled - using direct UserService connection");
    const data = { events: [] };
    dispatch({
      type: "getAlleventsSuccess",
      payload: data.events,
    });
  } catch (error) {
    dispatch({
      type: "getAlleventsFailed",
      payload: error.response.data.message,
    });
  }
};
