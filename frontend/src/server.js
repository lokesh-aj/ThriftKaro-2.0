// Updated to use UserService directly - bypassing gateway
export const server = process.env.REACT_APP_API_BASE || "http://localhost:8082";
export const backend_url = process.env.REACT_APP_API_BASE?.replace('/api/v2', '') || "http://localhost:8082";
