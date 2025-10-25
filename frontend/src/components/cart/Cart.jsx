import React, { useState, useEffect } from "react";
import { RxCross1 } from "react-icons/rx";
import { IoBagHandleOutline } from "react-icons/io5";
import { HiOutlineMinus, HiPlus } from "react-icons/hi";
import styles from "../../styles/styles";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { removeFromCart, updateCartItemQuantity, loadCart } from "../../redux/actions/cart";
import { toast } from "react-toastify";

const Cart = ({ setOpenCart }) => {
  const { cart, items, loading, error } = useSelector((state) => state.cart);
  const { user } = useSelector((state) => state.user);
  const { seller } = useSelector((state) => state.seller);
  const dispatch = useDispatch();

  // Get current user (either regular user or seller)
  const currentUser = user || seller;
  const currentUserId = user?._id || seller?._id;

  // Load cart when component mounts
  useEffect(() => {
    if (currentUser && currentUserId) {
      dispatch(loadCart(currentUserId));
    }
  }, [dispatch, currentUser, currentUserId]);

  const removeFromCartHandler = async (productId) => {
    if (currentUser && currentUserId) {
      try {
        await dispatch(removeFromCart(productId, currentUserId));
      } catch (error) {
        console.error('Error removing item from cart:', error);
      }
    }
  };

  const totalPrice = items.reduce(
    (acc, item) => acc + item.quantity * item.product.discountPrice,
    0
  );

  const quantityChangeHandler = async (productId, newQuantity) => {
    if (currentUser && currentUserId) {
      try {
        await dispatch(updateCartItemQuantity(productId, newQuantity, currentUserId));
      } catch (error) {
        console.error('Error updating item quantity:', error);
      }
    }
  };

  if (loading) {
    return (
      <div className="fixed top-0 left-0 w-full bg-[#0000004b] h-screen z-10">
        <div className="fixed top-0 right-0 h-full w-[80%] 800px:w-[25%] bg-white flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#e44343] mx-auto mb-4"></div>
            <p>Loading cart...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed top-0 left-0 w-full bg-[#0000004b] h-screen z-10">
      <div className="fixed top-0 right-0 h-full w-[80%] 800px:w-[25%] bg-white flex flex-col overflow-y-scroll justify-between shadow-sm">
        {!items || items.length === 0 ? (
          <div className="w-full h-screen flex items-center justify-center">
            <div className="flex w-full justify-end pt-5 pr-5 fixed top-3 right-3">
              <RxCross1
                size={25}
                className="cursor-pointer"
                onClick={() => setOpenCart(false)}
              />
            </div>
            <h5>Cart Items is empty!</h5>
          </div>
        ) : (
          <>
            <div>
              <div className="flex w-full justify-end pt-5 pr-5">
                <RxCross1
                  size={25}
                  className="cursor-pointer"
                  onClick={() => setOpenCart(false)}
                />
              </div>
              {/* Item length */}
              <div className={`${styles.noramlFlex} p-4`}>
                <IoBagHandleOutline size={25} />
                <h5 className="pl-2 text-[20px] font-[500]">
                  {items && items.length} items
                </h5>
              </div>

              {/* cart Single Items */}
              <br />
              <div className="w-full border-t">
                {items &&
                  items.map((item, index) => (
                    <CartSingle
                      key={index}
                      item={item}
                      quantityChangeHandler={quantityChangeHandler}
                      removeFromCartHandler={removeFromCartHandler}
                    />
                  ))}
              </div>
            </div>

            <div className="px-5 mb-3">
              {/* checkout buttons */}
              <Link to="/checkout">
                <div
                  className={`h-[45px] flex items-center justify-center w-[100%] bg-[#e44343] rounded-[5px]`}
                >
                  <h1 className="text-[#fff] text-[18px] font-[600]">
                    Checkout Now (₹ {totalPrice})
                  </h1>
                </div>
              </Link>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

const CartSingle = ({ item, quantityChangeHandler, removeFromCartHandler }) => {
  const [value, setValue] = useState(item.quantity);
  const totalPrice = item.product.discountPrice * value;

  const increment = () => {
    if (item.product.stock < value) {
      toast.error("Product stock limited!");
    } else {
      const newQuantity = value + 1;
      setValue(newQuantity);
      quantityChangeHandler(item.product._id, newQuantity);
    }
  };

  const decrement = () => {
    const newQuantity = value === 1 ? 1 : value - 1;
    setValue(newQuantity);
    quantityChangeHandler(item.product._id, newQuantity);
  };

  return (
    <div className="border-b p-4">
      <div className="w-full flex items-center">
        <div>
          <div
            className={`bg-[#e44343] border border-[#e4434373] rounded-full w-[25px] h-[25px] ${styles.noramlFlex} justify-center cursor-pointer`}
            onClick={increment}
          >
            <HiPlus size={18} color="#fff" />
          </div>
          <span className="pl-[10px]">{value}</span>
          <div
            className="bg-[#a7abb14f] rounded-full w-[25px] h-[25px] flex items-center justify-center cursor-pointer"
            onClick={decrement}
          >
            <HiOutlineMinus size={16} color="#7d879c" />
          </div>
        </div>
        <img
          src={`${item.product?.images[0]?.url}`}
          alt=""
          className="w-[130px] h-min ml-2 mr-2 rounded-[5px]"
        />
        <div className="pl-[5px]">
          <h1>{item.product.name}</h1>
          <h4 className="font-[400] text-[15px] text-[#00000082]">
            ₹{item.product.discountPrice} * {value}
          </h4>
          <h4 className="font-[600] text-[17px] pt-[3px] text-[#d02222] font-Roboto">
            ₹{totalPrice}
          </h4>
        </div>
        <RxCross1
          className="cursor-pointer w-12 h-12"
          onClick={() => removeFromCartHandler(item.product._id)}
        />
      </div>
    </div>
  );
};

export default Cart;
