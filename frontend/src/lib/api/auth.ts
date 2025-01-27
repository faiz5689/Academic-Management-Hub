// src/lib/api/auth.ts
import axios from 'axios';
import { AuthResponse, LoginRequest, TokenRefreshRequest, TokenRefreshResponse, ChangePasswordRequest } from '@/types/auth';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
const AUTH_API = `${API_URL}/api/auth`;

export const authApi = {
  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(`${AUTH_API}/login`, data);
    return response.data;
  },

  async refreshToken(data: TokenRefreshRequest): Promise<TokenRefreshResponse> {
    const response = await axios.post<TokenRefreshResponse>(`${AUTH_API}/refresh`, data);
    return response.data;
  },

  async logout(): Promise<void> {
    await axios.post(`${AUTH_API}/logout`);
  },

  async changePassword(data: ChangePasswordRequest): Promise<void> {
    await axios.post(`${AUTH_API}/password/change`, data);
  }
};