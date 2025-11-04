import React, { useEffect, useState } from "react";
import {
  AiFillHeart,
  AiOutlineHeart,
  AiOutlineMessage,
  AiOutlineShoppingCart,
} from "react-icons/ai";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { getAllProductsShop } from "../../redux/actions/product";
import axiosInstance from "../../api/axiosInstance";
import styles from "../../styles/styles";
import {
  addToWishlist,
  removeFromWishlist,
} from "../../redux/actions/wishlist";
import { addTocart } from "../../redux/actions/cart";
import { toast } from "react-toastify";
import Ratings from "./Ratings";
 

const ProductDetails = ({ data }) => {
  const { wishlist } = useSelector((state) => state.wishlist);
  const { items } = useSelector((state) => state.cart);
  const { user, isAuthenticated } = useSelector((state) => state.user);
  const { seller, isSeller } = useSelector((state) => state.seller);
  const { products } = useSelector((state) => state.products);
  const [count, setCount] = useState(1);
  const [click, setClick] = useState(false);
  const [select, setSelect] = useState(0);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  useEffect(() => {
    if (data && data?.shop && data?.shop?._id) {
      dispatch(getAllProductsShop(data.shop._id));
    }
    if (wishlist && wishlist.find((i) => i._id === data?._id)) {
      setClick(true);
    } else {
      setClick(false);
    }
  }, [data, wishlist, dispatch]);

  const incrementCount = () => {
    setCount(count + 1);
  };

  const decrementCount = () => {
    if (count > 1) {
      setCount(count - 1);
    }
  };

  const removeFromWishlistHandler = (data) => {
    setClick(!click);
    dispatch(removeFromWishlist(data));
  };

  const addToWishlistHandler = (data) => {
    setClick(!click);
    dispatch(addToWishlist(data));
  };

  const addToCartHandler = (id) => {
    // Check if user or seller is logged in and has a valid ID
    const currentUser = user || seller;
    const currentUserId = user?._id || seller?._id;
    
    if (!currentUser || !currentUserId) {
      toast.error("Please login to add items to cart!");
      return;
    }
    
    const isItemExists = items && items.find((item) => item.product._id === id);
    if (isItemExists) {
      toast.error("Item already in cart!");
    } else {
      if (!data?.stock || data.stock < 1) {
        toast.error("Product stock limited!");
      } else {
        const cartData = { ...data, qty: count };
        dispatch(addTocart(cartData, currentUserId));
      }
    }
  };

  const totalReviewsLength =
    products &&
    products.reduce((acc, product) => acc + (product?.reviews?.length || 0), 0);

  const totalRatings =
    products &&
    products.reduce(
      (acc, product) =>
        acc + (product?.reviews || []).reduce((sum, review) => sum + (review?.rating || 0), 0),
      0
    );

  const avg = totalRatings / totalReviewsLength || 0;

  const averageRating = avg.toFixed(2);


  const handleMessageSubmit = async () => {
    const currentUser = user || seller;
    const currentUserId = user?._id || seller?._id;
    const isUserAuthenticated = isAuthenticated || isSeller;
    
    if (!data?.shop?._id) {
      toast.error("Unable to send message: Shop information not available");
      return;
    }
    
    if (isUserAuthenticated && currentUser) {
      const groupTitle = data._id + currentUserId;
      const userId = currentUserId;
      const sellerId = data.shop._id;
      await axiosInstance
        .post(`/conversation/create-new-conversation`, {
          groupTitle,
          userId,
          sellerId,
        })
        .then((res) => {
          navigate(`/inbox?${res.data.conversation._id}`);
        })
        .catch((error) => {
          toast.error(error.response.data.message);
        });
    } else {
      toast.error("Please login to create a conversation");
    }
  };

  return (
    <div className="bg-white">
      {data ? (
        <div className={`${styles.section} w-[92%] 800px:w-[88%]`}>
          <div className="w-full py-6 grid grid-cols-1 800px:grid-cols-12 gap-8">
            <div className="800px:col-span-6">
              <div className="w-full 800px:sticky 800px:top-6 bg-white rounded-lg border border-gray-100 shadow-sm p-4">
                <img
                  src={`${data && data.images[select]?.url}`}
                  alt={data?.name || "Product image"}
                  className="w-full max-h-[440px] object-contain rounded-md"
                />
                <div className="w-full mt-4 grid grid-cols-4 sm:grid-cols-5 gap-3">
                  {data &&
                    data.images.map((i, index) => (
                      <button
                        key={index}
                        type="button"
                        className={`border rounded-md p-1 bg-white hover:shadow-sm transition ${
                          select === index ? "border-[#6443d1] ring-2 ring-[#6443d1]/20" : "border-gray-200"
                        }`}
                        onClick={() => setSelect(index)}
                        aria-label={`View image ${index + 1}`}
                      >
                        <img src={`${i?.url}`} alt="thumbnail" className="h-[70px] w-full object-contain" />
                      </button>
                    ))}
                </div>
              </div>
            </div>
            <div className="800px:col-span-6">
              <div className="w-full bg-white rounded-lg border border-gray-100 shadow-sm p-5">
                <h1 className={`${styles.productTitle} leading-tight`}>{data.name}</h1>
                <div className="flex items-end gap-3 mt-3">
                  <h4 className={`${styles.productDiscountPrice} !text-[28px] !leading-none`}>₹{data.discountPrice}</h4>
                  {data.originalPrice ? (
                    <h3 className={`${styles.price} line-through !text-gray-500 !text-[16px] !mb-[2px]`}>
                      ₹{data.originalPrice}
                    </h3>
                  ) : null}
                  <div className="ml-auto flex items-center text-sm text-gray-600">
                    <Ratings rating={data?.ratings} />
                    <span className="ml-2">({averageRating})</span>
                  </div>
                </div>
                <p className="mt-3 text-gray-700 leading-6 whitespace-pre-line">{data.description}</p>
                <div className="mt-6 flex items-center justify-between gap-4">
                  <div className="inline-flex items-center rounded-md border border-gray-200 overflow-hidden">
                    <button onClick={decrementCount} className="px-3 py-2 text-gray-700 hover:bg-gray-50 active:bg-gray-100" aria-label="Decrease quantity">-</button>
                    <span className="px-4 py-2 text-gray-900 font-medium select-none">{count}</span>
                    <button onClick={incrementCount} className="px-3 py-2 text-gray-700 hover:bg-gray-50 active:bg-gray-100" aria-label="Increase quantity">+</button>
                  </div>
                  <div className="flex items-center">
                    {click ? (
                      <AiFillHeart size={28} className="cursor-pointer" onClick={() => removeFromWishlistHandler(data)} color={click ? "red" : "#333"} title="Remove from wishlist" />
                    ) : (
                      <AiOutlineHeart size={28} className="cursor-pointer" onClick={() => addToWishlistHandler(data)} color={click ? "red" : "#333"} title="Add to wishlist" />
                    )}
                  </div>
                </div>
                <div className="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <button onClick={() => addToCartHandler(data._id)} className={`${styles.button} !w-full !h-[44px] !rounded-md flex items-center justify-center`}>
                    <span className="text-white flex items-center">Add to cart <AiOutlineShoppingCart className="ml-2" /></span>
                  </button>
                  <button onClick={handleMessageSubmit} className={`${styles.button} !w-full !h-[44px] !rounded-md bg-[#6443d1] flex items-center justify-center`}>
                    <span className="text-white flex items-center">Contact Seller <AiOutlineMessage className="ml-2" /></span>
                  </button>
                </div>
                {data?.shop && (
                  <div className="mt-6 flex items-center">
                    <Link to={`/shop/preview/${data?.shop._id}`} className="shrink-0">
                      <img src={`${data?.shop?.avatar?.url}`} alt={data?.shop?.name || "Shop avatar"} className="w-[48px] h-[48px] rounded-full mr-3 border border-gray-200" />
                    </Link>
                    <div className="pr-8">
                      <Link to={`/shop/preview/${data?.shop._id}`}>
                        <h3 className={`${styles.shop_name} pb-1 pt-1`}>{data.shop.name}</h3>
                      </Link>
                      <p className="text-[13px] text-gray-600">({averageRating}/5) Ratings</p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
          <ProductDetailsInfo data={data} products={products} totalReviewsLength={totalReviewsLength} averageRating={averageRating} />
          <br />
          <br />
        </div>
      ) : null}
    </div>
  );
};

const ProductDetailsInfo = ({
  data,
  products,
  totalReviewsLength,
  averageRating,
}) => {
  const [active, setActive] = useState(1);

  return (
    <div className="bg-[#f5f6fb] px-3 800px:px-10 py-2 rounded">
      <div className="w-full flex justify-between border-b pt-10 pb-2">
        <div className="relative">
          <h5
            className={
              "text-[#000] text-[18px] px-1 leading-5 font-[600] cursor-pointer 800px:text-[20px]"
            }
            onClick={() => setActive(1)}
          >
            Product Details
          </h5>
          {active === 1 ? (
            <div className={`${styles.active_indicator}`} />
          ) : null}
        </div>
        <div className="relative">
          <h5
            className={
              "text-[#000] text-[18px] px-1 leading-5 font-[600] cursor-pointer 800px:text-[20px]"
            }
            onClick={() => setActive(2)}
          >
            Product Reviews
          </h5>
          {active === 2 ? (
            <div className={`${styles.active_indicator}`} />
          ) : null}
        </div>
        <div className="relative">
          <h5
            className={
              "text-[#000] text-[18px] px-1 leading-5 font-[600] cursor-pointer 800px:text-[20px]"
            }
            onClick={() => setActive(3)}
          >
            Seller Information
          </h5>
          {active === 3 ? (
            <div className={`${styles.active_indicator}`} />
          ) : null}
        </div>
      </div>
      {active === 1 ? (
        <>
          <p className="py-2 text-[18px] leading-8 pb-10 whitespace-pre-line">
            {data.description}
          </p>
        </>
      ) : null}

      {active === 2 ? (
        <div className="w-full min-h-[40vh] flex flex-col items-center py-3 overflow-y-scroll">
          {data && data?.reviews &&
            data.reviews.map((item, index) => (
              <div className="w-full flex my-2">
                <img
                  src={`${item?.user?.avatar?.url}`}
                  alt=""
                  className="w-[50px] h-[50px] rounded-full"
                />
                <div className="pl-2 ">
                  <div className="w-full flex items-center">
                    <h1 className="font-[500] mr-3">{item?.user?.name || 'Anonymous'}</h1>
                    <Ratings rating={data?.ratings} />
                  </div>
                  <p>{item.comment}</p>
                </div>
              </div>
            ))}

          <div className="w-full flex justify-center">
            {data && (!data?.reviews || data.reviews.length === 0) && (
              <h5>No Reviews have for this product!</h5>
            )}
          </div>
        </div>
      ) : null}

      {active === 3 && data?.shop && (
        <div className="w-full block 800px:flex p-5">
          <div className="w-full 800px:w-[50%]">
            <Link to={`/shop/preview/${data.shop._id}`}>
              <div className="flex items-center">
                <img
                  src={`${data?.shop?.avatar?.url}`}
                  className="w-[50px] h-[50px] rounded-full"
                  alt=""
                />
                <div className="pl-3">
                  <h3 className={`${styles.shop_name}`}>{data.shop.name}</h3>
                  <h5 className="pb-2 text-[15px]">
                    ({averageRating}/5) Ratings
                  </h5>
                </div>
              </div>
            </Link>
            <p className="pt-2">{data.shop.description}</p>
          </div>
          <div className="w-full 800px:w-[50%] mt-5 800px:mt-0 800px:flex flex-col items-end">
            <div className="text-left">
              <h5 className="font-[600]">
                Joined on:{" "}
                <span className="font-[500]">
                  {data.shop?.createdAt?.slice(0, 10)}
                </span>
              </h5>
              <h5 className="font-[600] pt-3">
                Total Products:{" "}
                <span className="font-[500]">
                  {products && products.length}
                </span>
              </h5>
              <h5 className="font-[600] pt-3">
                Total Reviews:{" "}
                <span className="font-[500]">{totalReviewsLength}</span>
              </h5>
              <Link to="/">
                <div
                  className={`${styles.button} !rounded-[4px] !h-[39.5px] mt-3`}
                >
                  <h4 className="text-white">Visit Shop</h4>
                </div>
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductDetails;
