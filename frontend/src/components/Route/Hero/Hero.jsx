import React from "react";
import { Link } from "react-router-dom";
import styles from "../../../styles/styles";

const Hero = () => {
  return (
    <div
      className={`relative min-h-[70vh] 800px:min-h-[80vh] w-full bg-no-repeat ${styles.noramlFlex}`}
      style={{
        backgroundImage:
          "url(https://themes.rslahmed.dev/rafcart/assets/images/banner-2.jpg)",
      }}
    >
      <div className={`${styles.section} w-[90%] 800px:w-[60%]`}>
        <h1
          className={`text-[35px] leading-[1.2] 800px:text-[60px] text-[#880E4F] font-[600] capitalize`}
        >
          Your Sustainable Fashion <br /> Journey Starts Here! 
        </h1>
        <p className="pt-5 text-[16px] font-[Poppins] font-[400] text-[#5F5F5F]">
        Explore, connect, and transform your shopping experience with ThriftKaro, <br />the ultimate hub for thrift enthusiasts and eco-conscious shoppers. <br />
        Whether you're hunting for stylish thrifted finds or showcasing your own pre-loved treasures, <br />ThriftKaro makes it easy, secure, and fun!
        </p>
        <Link to="/products" className="inline-block">
            <div className={`${styles.button} mt-5`}>
                 <span className="text-[#fff] font-[Poppins] text-[18px]">
                    Shop Now
                 </span>
            </div>
        </Link>
      </div>
    </div>
  );
};

export default Hero;
