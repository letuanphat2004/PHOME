import axios from "axios";

export const api = axios.create({ baseURL: "/api/v1", withCredentials: true });

let csrfToken;
export async function ensureCsrf() {
  if (!csrfToken) {
    const { data } = await api.get("/auth/csrf");
    csrfToken = data.token;
  }
  return csrfToken;
}

api.interceptors.request.use(async (config) => {
  if (
    config.method &&
    !["get", "head", "options"].includes(config.method.toLowerCase())
  ) {
    config.headers["X-XSRF-TOKEN"] = await ensureCsrf();
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const config = error.config;
    const method = config?.method?.toLowerCase();
    const isMutation = method && !["get", "head", "options"].includes(method);

    if (error.response?.status === 403 && isMutation && !config._csrfRetried) {
      config._csrfRetried = true;
      csrfToken = undefined;
      config.headers["X-XSRF-TOKEN"] = await ensureCsrf();
      return api.request(config);
    }

    return Promise.reject(error);
  },
);

export function errorMessage(error) {
  const message = error.response?.data?.message;
  if (message && message !== "No message available") return message;
  if (error.response?.status === 401) {
    return "Tên đăng nhập hoặc mật khẩu không đúng.";
  }
  if (error.response?.status === 403) {
    return "Phiên bảo mật đã hết hạn. Vui lòng thử lại.";
  }
  return "Không thể kết nối máy chủ. Vui lòng thử lại.";
}
