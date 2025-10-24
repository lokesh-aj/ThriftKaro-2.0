import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children }) => {
  const { loading, isAuthenticated } = useSelector((state) => state.user);
  
  // Show loading state while checking authentication
  if (loading === true || loading === undefined) {
    return <div>Loading...</div>;
  }
  
  if (loading === false) {
    if (!isAuthenticated) {
      return <Navigate to="/login" replace />;
    }
    return children;
  }
  
  // Fallback
  return <div>Loading...</div>;
};

export default ProtectedRoute;
