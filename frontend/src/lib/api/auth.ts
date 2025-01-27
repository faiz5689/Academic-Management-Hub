// src/lib/api/auth.ts
import axios, { AxiosError } from 'axios';
import { AuthResponse, LoginRequest, TokenRefreshRequest, TokenRefreshResponse, ChangePasswordRequest } from '@/types/auth';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const authService = {
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      console.log('Making login request to:', `${API_URL}/auth/login`);
      
      const response = await axios.post<AuthResponse>(`${API_URL}/auth/login`, data, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
      
      console.log('Login response:', response.data);
      
      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
      }
      
      return response.data;
    } catch (error) {
      if (error instanceof AxiosError && error.response) {
        console.error('Login error response:', error.response.data);
        console.error('Status code:', error.response.status);
      }
      throw error;
    }
  },

  async refreshToken(data: TokenRefreshRequest): Promise<TokenRefreshResponse> {
    const response = await axios.post<TokenRefreshResponse>(`${API_URL}/auth/refresh`, data);
    return response.data;
  },

  async logout(): Promise<void> {
    const token = localStorage.getItem('accessToken');
    try {
      await axios.post(`${API_URL}/auth/logout`, null, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  },

  async changePassword(data: ChangePasswordRequest): Promise<void> {
    const token = localStorage.getItem('accessToken');
    await axios.post(`${API_URL}/auth/password/change`, data, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
};