import { Button } from "@material-ui/core";
import { DataGrid } from "@material-ui/data-grid";
import { orderApiInstance } from "../../api/directApiInstances";
import React, { useEffect, useState } from "react";
import { AiOutlineDelete } from "react-icons/ai";
import { RxCross1 } from "react-icons/rx";
import { useDispatch, useSelector } from "react-redux";
import styles from "../../styles/styles";
import Loader from "../Layout/Loader";
import { toast } from "react-toastify";
import { getAllProductsShop } from "../../redux/actions/product";

const AllCoupons = () => {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [coupouns,setCoupouns] = useState([]);
  const [minAmount, setMinAmout] = useState("");
  const [maxAmount, setMaxAmount] = useState("");
  const [selectedProducts, setSelectedProducts] = useState("");
  const [value, setValue] = useState("");
  const { seller } = useSelector((state) => state.seller);
  const { products } = useSelector((state) => state.products);

  const dispatch = useDispatch();

  useEffect(() => {
    if (seller?._id) {
      setIsLoading(true);
      orderApiInstance
        .get(`/api/v2/coupon/get-coupon/${seller._id}`)
        .then((res) => {
          setIsLoading(false);
          if (res.data.success) {
            setCoupouns(res.data.couponCodes || []);
          } else {
            setCoupouns([]);
          }
        })
        .catch((error) => {
          console.error("Error fetching coupons:", error);
          setIsLoading(false);
          setCoupouns([]);
        });
    }
  }, [dispatch, seller?._id]);

  useEffect(() => {
    if (seller?._id) dispatch(getAllProductsShop(seller._id));
  }, [dispatch, seller]);

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this coupon?")) {
      return;
    }
    
    try {
      setIsLoading(true);
      const res = await orderApiInstance.delete(`/api/v2/coupon/delete-coupon/${id}`);
      
      if (res.data.success) {
        toast.success("Coupon code deleted successfully!");
        // Refresh the coupon list instead of reloading the page
        const refreshRes = await orderApiInstance.get(`/api/v2/coupon/get-coupon/${seller._id}`);
        if (refreshRes.data.success) {
          setCoupouns(refreshRes.data.couponCodes);
        }
      } else {
        toast.error(res.data.message || "Failed to delete coupon");
      }
    } catch (error) {
      console.error("Delete coupon error:", error);
      toast.error(error.response?.data?.message || error.message || "Failed to delete coupon");
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    await orderApiInstance
      .post("/api/v2/coupon/create-coupon-code", {
        name,
        minAmount,
        maxAmount,
        selectedProduct: selectedProducts, // fix backend field name
        value,
        shopId: seller._id,
      })
      .then((res) => {
        toast.success("Coupon code created successfully!");
        setOpen(false);
        window.location.reload();
      })
      .catch((error) => {
        toast.error(error.response?.data?.message || error.message);
      });
  };

  const columns = [
    { field: "id", headerName: "Id", minWidth: 150, flex: 0.7 },
    {
      field: "name",
      headerName: "Coupon Code",
      minWidth: 180,
      flex: 1.4,
    },
    {
      field: "price",
      headerName: "Value",
      minWidth: 100,
      flex: 0.6,
    },
    {
      field: "Delete",
      flex: 0.8,
      minWidth: 120,
      headerName: "",
      type: "number",
      sortable: false,
      renderCell: (params) => {
        return (
          <>
            <Button onClick={() => handleDelete(params.id)}>
              <AiOutlineDelete size={20} />
            </Button>
          </>
        );
      },
    },
  ];

  const row = [];

  coupouns &&
  coupouns.forEach((item) => {
      row.push({
        id: item.id || item._id,
        name: item.name,
        price: item.value + " %",
        sold: 10,
      });
    });

  return (
    <>
      {isLoading ? (
        <Loader />
      ) : (
        <div className="w-full mx-8 pt-1 mt-10 bg-white">
          <div className="w-full flex justify-end">
            <div
              className={`${styles.button} !w-max !h-[45px] px-3 !rounded-[5px] mr-3 mb-3`}
              onClick={() => setOpen(true)}
            >
              <span className="text-white">Create Coupon Code</span>
            </div>
          </div>
          <DataGrid
            rows={row}
            columns={columns}
            pageSize={10}
            rowsPerPageOptions={[10, 20, 50]}
            disableSelectionOnClick
            autoHeight
            getRowId={(r) => r.id || r._id || r.name}
          />
          {open && (
            <div className="fixed top-0 left-0 w-full h-screen bg-[#00000062] z-[20000] flex items-center justify-center">
              <div className="w-[90%] 800px:w-[40%] h-[80vh] bg-white rounded-md shadow p-4">
                <div className="w-full flex justify-end">
                  <RxCross1
                    size={30}
                    className="cursor-pointer"
                    onClick={() => setOpen(false)}
                  />
                </div>
                <h5 className="text-[30px] font-Poppins text-center">
                  Create Coupon code
                </h5>
                {/* create coupoun code */}
                <form onSubmit={handleSubmit} aria-required={true}>
                  <br />
                  <div>
                    <label className="pb-2">
                      Name <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      name="name"
                      required
                      value={name}
                      className="mt-2 appearance-none block w-full px-3 h-[35px] border border-gray-300 rounded-[3px] placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      onChange={(e) => setName(e.target.value)}
                      placeholder="Enter your coupon code name..."
                    />
                  </div>
                  <br />
                  <div>
                    <label className="pb-2">
                      Discount Percentenge{" "}
                      <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      name="value"
                      value={value ?? ""}
                      required
                      className="mt-2 appearance-none block w-full px-3 h-[35px] border border-gray-300 rounded-[3px] placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      onChange={(e) => setValue(e.target.value)}
                      placeholder="Enter your coupon code value..."
                    />
                  </div>
                  <br />
                  <div>
                    <label className="pb-2">Min Amount</label>
                    <input
                      type="number"
                      name="value"
                      value={minAmount ?? ""}
                      className="mt-2 appearance-none block w-full px-3 h-[35px] border border-gray-300 rounded-[3px] placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      onChange={(e) => setMinAmout(e.target.value)}
                      placeholder="Enter your coupon code min amount..."
                    />
                  </div>
                  <br />
                  <div>
                    <label className="pb-2">Max Amount</label>
                    <input
                      type="number"
                      name="value"
                      value={maxAmount ?? ""}
                      className="mt-2 appearance-none block w-full px-3 h-[35px] border border-gray-300 rounded-[3px] placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      onChange={(e) => setMaxAmount(e.target.value)}
                      placeholder="Enter your coupon code max amount..."
                    />
                  </div>
                  <br />
                  <div>
                    <label className="pb-2">Selected Product</label>
                    <select
                      className="w-full mt-2 border h-[35px] rounded-[5px]"
                      value={selectedProducts || ""}
                      onChange={(e) => setSelectedProducts(e.target.value)}
                    >
                      <option disabled value="">
                        Choose a selected product
                      </option>
                      {Array.isArray(products) &&
                        products.map((i) => (
                          <option value={i.name} key={i._id || i.name}>
                            {i.name}
                          </option>
                        ))}
                    </select>
                  </div>
                  <br />
                  <div>
                    <button
                      type="submit"
                      className={`${styles.button} !w-full !h-[40px] !rounded-[5px] !bg-[#3bb77e] hover:!bg-[#2ca96b]`}
                    >
                      <span className="text-white">Create</span>
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
        </div>
      )}
    </>
  );
};

export default AllCoupons;
