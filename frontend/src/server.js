export const server = process.env.REACT_APP_API_BASE || "http://localhost:8080/api/v2";
export const backend_url = process.env.REACT_APP_API_BASE?.replace('/api/v2', '') || "http://localhost:8080/";
