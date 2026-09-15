import { createContext, useContext } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const queryClient = useQueryClient();
  const { data: user, isLoading } = useQuery({
    queryKey: ["current-user"],
    queryFn: () => api.get("/auth/me").then((r) => r.data),
    retry: false,
  });
  const login = async (credentials) => {
    const { data } = await api.post("/auth/login", credentials);
    queryClient.setQueryData(["current-user"], data);
    return data;
  };
  const logout = async () => {
    await api.post("/auth/logout");
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
