// src/lib/api/auth.ts
import axios, { AxiosError } from 'axios';
import { 
  AuthResponse, 
  LoginRequest, 
  TokenRefreshRequest, 
  TokenRefreshResponse, 
  ChangePasswordRequest, 
  RegistrationRequest 
} from '@/types/auth';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export interface ApiError {
  message: string;
  fieldErrors?: Record<string, string>;
  statusCode?: number;
}

class AuthenticationError extends Error {
  constructor(
    message: string,
    public statusCode: number,
    public fieldErrors?: Record<string, string>
  ) {
    super(message);
    this.name = 'AuthenticationError';
  }
}

export const authService = {
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await axios.post<AuthResponse>(`${API_URL}/auth/login`, data, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
      
      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
      }
      
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const apiError = error.response?.data as ApiError;
        throw new AuthenticationError(
          apiError?.message || 'Login failed',
          error.response?.status || 500,
          apiError?.fieldErrors
        );
      }
      throw new AuthenticationError('Network error occurred', 500);
    }
  },

  async register(data: RegistrationRequest): Promise<void> {
    try {
      await axios.post(`${API_URL}/auth/register/professor`, data, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const apiError = error.response?.data as ApiError;
        throw new AuthenticationError(
          apiError?.message || 'Registration failed',
          error.response?.status || 500,
          apiError?.fieldErrors
        );
      }
      throw new AuthenticationError('Network error occurred', 500);
    }
  },

  async refreshToken(data: TokenRefreshRequest): Promise<TokenRefreshResponse> {
    try {
      const response = await axios.post<TokenRefreshResponse>(
        `${API_URL}/auth/refresh`,
        data,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
      
      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
      }
      
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const apiError = error.response?.data as ApiError;
        throw new AuthenticationError(
          apiError?.message || 'Token refresh failed',
          error.response?.status || 500
        );
      }
      throw new AuthenticationError('Network error occurred', 500);
    }
  },

  async logout(): Promise<void> {
    const token = localStorage.getItem('accessToken');
    try {
      if (token) {
        await axios.post(
          `${API_URL}/auth/logout`,
          null,
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json',
            }
          }
        );
      }
    } catch (error) {
      console.error('Logout error:', error);
      // Continue with cleanup even if the API call fails
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  },

  async changePassword(data: ChangePasswordRequest): Promise<void> {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      throw new AuthenticationError('Not authenticated', 401);
    }

    try {
      await axios.post(
        `${API_URL}/auth/password/change`,
        data,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          }
        }
      );
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const apiError = error.response?.data as ApiError;
        throw new AuthenticationError(
          apiError?.message || 'Password change failed',
          error.response?.status || 500,
          apiError?.fieldErrors
        );
      }
      throw new AuthenticationError('Network error occurred', 500);
    }
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('accessToken');
  },

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  },

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  },

  clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
};