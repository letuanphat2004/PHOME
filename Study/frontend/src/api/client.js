import axios from "axios";

export const api = axios.create({ baseURL: "/api/v1", withCredentials: true });

let csrfToken;
let csrfRequest;
let unauthorizedHandler;

export function resetCsrfToken() {
  csrfToken = undefined;
  csrfRequest = undefined;
}

export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
  return () => {
    if (unauthorizedHandler === handler) unauthorizedHandler = undefined;
  };
}

export async function ensureCsrf() {
  if (!csrfToken) {
    csrfRequest ??= api.get("/auth/csrf")
      .then(({ data }) => {
        csrfToken = data.token;
        return csrfToken;
      })
      .finally(() => {
        csrfRequest = undefined;
      });
    return csrfRequest;
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
      resetCsrfToken();
      config.headers["X-XSRF-TOKEN"] = await ensureCsrf();
      return api.request(config);
    }

    const isAuthCheck = ["/auth/login", "/auth/me"].includes(config?.url);
    if (error.response?.status === 401 && !isAuthCheck) {
      resetCsrfToken();
      unauthorizedHandler?.();
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
