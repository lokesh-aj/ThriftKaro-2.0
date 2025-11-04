import axiosInstance from "../../api/axiosInstance";
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import styles from "../../styles/styles";
import Loader from "../Layout/Loader";
import { useDispatch, useSelector } from "react-redux";
import { getAllProductsShop } from "../../redux/actions/product";

const ShopInfo = ({ isOwner }) => {
  const [data,setData] = useState({});
  const {products} = useSelector((state) => state.products);
  const [isLoading,setIsLoading] = useState(false);
  const {id} = useParams();
  const dispatch = useDispatch();

  useEffect(() => {
    if (id) {
      dispatch(getAllProductsShop(id));
    }
    setIsLoading(true);
    // Temporarily disabled - ShopService not available when connecting directly to UserService
    console.log("Get shop info disabled - using direct UserService connection");
    const res = { data: { shop: {} } };
    // Simulate async behavior
    setTimeout(() => {
      setData(res.data.shop);
      setIsLoading(false);
    }, 100);
  }, [id])
  

  const logoutHandler = async () => {
    // Clear localStorage tokens and data
    localStorage.removeItem('token');
    localStorage.removeItem('sellerToken');
    localStorage.removeItem('user');
    localStorage.removeItem('seller');
    window.location.reload();
  };

  // Safely calculate totals with null checks
  const totalReviewsLength =
    products && Array.isArray(products) && products.length > 0
      ? products.reduce((acc, product) => acc + (product?.reviews?.length || 0), 0)
      : 0;

  const totalRatings = 
    products && Array.isArray(products) && products.length > 0
      ? products.reduce((acc, product) => {
          const productReviews = product?.reviews || [];
          const productRating = productReviews.reduce((sum, review) => sum + (review?.rating || 0), 0);
          return acc + productRating;
        }, 0)
      : 0;

  const averageRating = totalReviewsLength > 0 ? Number((totalRatings / totalReviewsLength).toFixed(1)) : 0;

  return (
   <>
   {
    isLoading  ? (
      <Loader />
    ) : (
      <div>
      <div className="w-full py-5">
        <div className="w-full flex item-center justify-center">
          <img
            src={`${data.avatar?.url}`}
            alt=""
            className="w-[150px] h-[150px] object-cover rounded-full"
          />
        </div>
        <h3 className="text-center py-2 text-[20px]">{data.name}</h3>
        <p className="text-[16px] text-[#000000a6] p-[10px] flex items-center">
          {data.description}
        </p>
      </div>
      <div className="p-3">
        <h5 className="font-[600]">Address</h5>
        <h4 className="text-[#000000a6]">{data.address}</h4>
      </div>
      <div className="p-3">
        <h5 className="font-[600]">Phone Number</h5>
        <h4 className="text-[#000000a6]">{data.phoneNumber}</h4>
      </div>
      <div className="p-3">
        <h5 className="font-[600]">Total Products</h5>
        <h4 className="text-[#000000a6]">{products && products.length}</h4>
      </div>
      <div className="p-3">
        <h5 className="font-[600]">Shop Ratings</h5>
        <h4 className="text-[#000000b0]">{averageRating}/5</h4>
      </div>
      <div className="p-3">
        <h5 className="font-[600]">Joined On</h5>
        <h4 className="text-[#000000b0]">{data?.createdAt?.slice(0, 10)}</h4>
      </div>
      {isOwner && (
        <div className="py-3 px-4">
           <Link to="/settings">
           <div className={`${styles.button} !w-full !h-[42px] !rounded-[5px]`}>
            <span className="text-white">Edit Shop</span>
          </div>
           </Link>
          <div className={`${styles.button} !w-full !h-[42px] !rounded-[5px]`}
          onClick={logoutHandler}
          >
            <span className="text-white">Log Out</span>
          </div>
        </div>
      )}
    </div>
    )
   }
   </>
  );
};

export default ShopInfo;
