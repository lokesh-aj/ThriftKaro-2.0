import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

const ProtectedAdminRoute = ({ children }) => {
  const { loading, isAuthenticated, user } = useSelector((state) => state.user);
  
  // Show loading state while checking authentication
  if (loading === true || loading === undefined) {
    return <div>Loading...</div>;
  }
  
  if (loading === false) {
    if (!isAuthenticated) {
      return <Navigate to="/login" replace />;
    } else if (user?.role !== "Admin") {
      return <Navigate to="/" replace />;
    }
    return children;
  }
  
  // Fallback
  return <div>Loading...</div>;
};

export default ProtectedAdminRoute;
