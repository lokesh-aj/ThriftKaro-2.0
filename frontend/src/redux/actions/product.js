import { productApiInstance } from "../../api/directApiInstances";

// create product
export const createProduct = (productData) => async (dispatch) => {
  try {
    dispatch({
      type: "productCreateRequest",
    });

    console.log("Creating product with data:", productData);
    
    const response = await productApiInstance.post("/api/v2/product", productData);
    
    console.log("Product creation response:", response.data);
    
    dispatch({
      type: "productCreateSuccess",
      payload: response.data.product,
    });
  } catch (error) {
    console.error("Product creation error:", error);
    dispatch({
      type: "productCreateFail",
      payload: error.response?.data?.message || error.message || "Failed to create product",
    });
  }
};

// get All Products of a shop
export const getAllProductsShop = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "getAllProductsShopRequest",
    });

    console.log("Getting products for shop:", id);
    const response = await productApiInstance.get(`/api/v2/product/get-all-products-shop/${id}`);
    
    dispatch({
      type: "getAllProductsShopSuccess",
      payload: response.data.products,
    });
  } catch (error) {
    console.error("Get shop products error:", error);
    dispatch({
      type: "getAllProductsShopFailed",
      payload: error.response?.data?.message || error.message || "Failed to get shop products",
    });
  }
};

// delete product of a shop
export const deleteProduct = (id) => async (dispatch) => {
  try {
    dispatch({
      type: "deleteProductRequest",
    });

    console.log("Deleting product:", id);
    const response = await productApiInstance.delete(`/api/v2/product/${id}`);
    
    dispatch({
      type: "deleteProductSuccess",
      payload: response.data.message,
    });
  } catch (error) {
    console.error("Delete product error:", error);
    dispatch({
      type: "deleteProductFailed",
      payload: error.response?.data?.message || error.message || "Failed to delete product",
    });
  }
};

// get all products
export const getAllProducts = () => async (dispatch) => {
  try {
    dispatch({
      type: "getAllProductsRequest",
    });

    console.log("Getting all products");
    const response = await productApiInstance.get("/api/v2/product/get-all-products");
    
    dispatch({
      type: "getAllProductsSuccess",
      payload: response.data.products,
    });
  } catch (error) {
    console.error("Get all products error:", error);
    dispatch({
      type: "getAllProductsFailed",
      payload: error.response?.data?.message || error.message || "Failed to get products",
    });
  }
};
