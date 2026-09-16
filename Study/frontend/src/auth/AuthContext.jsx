import { createContext, useContext, useEffect } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api, resetCsrfToken, setUnauthorizedHandler } from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const queryClient = useQueryClient();
  useEffect(() => setUnauthorizedHandler(() => {
    queryClient.clear();
    queryClient.setQueryData(["current-user"], null);
  }), [queryClient]);

  const { data: user, isLoading } = useQuery({
    queryKey: ["current-user"],
    queryFn: () => api.get("/auth/me").then((r) => r.data),
    retry: false,
  });
  const login = async (credentials) => {
    const { data } = await api.post("/auth/login", credentials);
    resetCsrfToken();
    queryClient.clear();
    queryClient.setQueryData(["current-user"], data);
    return data;
  };
  const logout = async () => {
    try {
      await api.post("/auth/logout");
    } catch {
      // Local auth state still needs to be cleared when the server session expired.
    }
    resetCsrfToken();
    queryClient.clear();
    queryClient.setQueryData(["current-user"], null);
  };
  return (
    <AuthContext.Provider
      value={{ user: user || null, isLoading, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
