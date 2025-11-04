import { productApiInstance } from "../../api/directApiInstances";

// create product
export const createProduct = (productData) => async (dispatch) => {
  try {
    dispatch({
      type: "productCreateRequest",
    });

    console.log("Creating product with data:", productData);
    
    const response = await productApiInstance.post("/api/v2/product/create-product", productData);
    
    console.log("Product creation response:", response.data);
    
    dispatch({
      type: "productCreateSuccess",
      payload: response.data.product,
    });
  } catch (error) {
    console.error("Product creation error:", error);
    console.error("Error response:", error.response?.data);
    console.error("Error status:", error.response?.status);
    console.error("Error headers:", error.response?.headers);
    
    // Check if server provided detailed error info
    if (error.response?.data) {
      const errorData = error.response.data;
      console.error("Server error details:", {
        message: errorData.message,
        userRole: errorData.userRole,
        requiredRole: errorData.requiredRole,
        success: errorData.success
      });
    }
    
    // Check token status
    const sellerToken = localStorage.getItem('sellerToken');
    const regularToken = localStorage.getItem('token');
    console.log("Available tokens:", {
      hasSellerToken: !!sellerToken,
      hasRegularToken: !!regularToken,
      sellerTokenPreview: sellerToken ? sellerToken.substring(0, 20) + "..." : "none"
    });
    
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
    const rawProducts = response?.data?.products || [];
    const normalized = rawProducts.map((p) => {
      if (p && p.id && !p._id) p._id = p.id;
      if (!Array.isArray(p?.images)) p.images = [];
      if (!p.shop && (p.shopId || id)) p.shop = { _id: p.shopId || id };
      if (p.soldOut != null && p.sold_out == null) p.sold_out = p.soldOut;
      return p;
    });
    
    dispatch({
      type: "getAllProductsShopSuccess",
      payload: normalized,
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
    const response = await productApiInstance.delete(`/api/v2/product/delete-shop-product/${id}`);
    
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
    // Normalize product shape for frontend expectations
    const rawProducts = response?.data?.products || [];
    const normalized = rawProducts.map((p) => {
      // Ensure _id exists (backend returns id)
      if (p && p.id && !p._id) p._id = p.id;
      // Ensure images is an array
      if (!Array.isArray(p?.images)) p.images = [];
      // Ensure shop object consistency if only shopId present
      if (!p.shop && p.shopId) p.shop = { _id: p.shopId };
      // Normalize naming differences
      if (p.soldOut != null && p.sold_out == null) p.sold_out = p.soldOut;
      return p;
    });
    
    dispatch({
      type: "getAllProductsSuccess",
      payload: normalized,
    });
  } catch (error) {
    console.error("Get all products error:", error);
    dispatch({
      type: "getAllProductsFailed",
      payload: error.response?.data?.message || error.message || "Failed to get products",
    });
  }
};
