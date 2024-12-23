import React, { useEffect, useState } from 'react'
import AdminHeader from '../components/Layout/AdminHeader'
import AdminSideBar from '../components/Admin/Layout/AdminSideBar'
import AllUsers from "../components/Admin/AllUsers";
import axios from 'axios';
import { server } from '../server';

const AdminDashboardUsers = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    axios
      .get(`${server}/user/admin-all-users`, {
        withCredentials: true,
      })
      .then((res) => {
        setData(res.data.users);
      })
      .catch((error) => {
        console.log(error.response.data.message);
      });
  }, []);

  // Displays users in a data grid with columns:
  // - User ID
  // - Name
  // - Email
  // - Role (user/admin)
  // - Joined At
  // - Actions (delete user)

  return (
    <div>
    <AdminHeader />
    <div className="w-full flex">
      <div className="flex items-start justify-between w-full">
        <div className="w-[80px] 800px:w-[330px]">
          <AdminSideBar active={4} />
        </div>
        <AllUsers />
      </div>
    </div>
  </div>
  )
}

export default AdminDashboardUsers