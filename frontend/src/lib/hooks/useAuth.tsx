// src/lib/hooks/useAuth.tsx
'use client';

import { createContext, useContext, useEffect, useState } from 'react';
import { User, AuthResponse } from '@/types/auth';
import { authService } from '@/lib/api/auth';

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check for stored token and validate
    const token = localStorage.getItem('accessToken');
    console.log('Stored token:', token ? 'exists' : 'none');
    setIsLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    try {
      console.log('AuthContext: Attempting login...');
      const response = await authService.login({ email, password });
      console.log('AuthContext: Login response received:', response);
      
      // Check if we have the user data in the response
      if (response.user) {
        console.log('AuthContext: Setting user data:', response.user);
        setUser(response.user);
      } else {
        console.error('AuthContext: No user data in response');
        throw new Error('No user data received');
      }
    } catch (error) {
      console.error('AuthContext: Login error:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
      setUser(null);
    } catch (error) {
      console.error('AuthContext: Logout error:', error);
      // Still clear the user state even if API call fails
      setUser(null);
    }
  };

  const value = {
    user,
    login,
    logout,
    isLoading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};