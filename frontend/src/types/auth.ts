// src/types/auth.ts

export enum UserRole {
  ADMIN = 'ADMIN',
  PROFESSOR = 'PROFESSOR',
  STAFF = 'STAFF'
}

export interface User {
  id: string;
  email: string;
  role: UserRole;
  isActive: boolean;
  createdAt: string | null;
  lastLogin: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenRefreshResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export enum ProfessorTitle {
  ASSISTANT_PROFESSOR = 'ASSISTANT_PROFESSOR',
  ASSOCIATE_PROFESSOR = 'ASSOCIATE_PROFESSOR',
  PROFESSOR = 'PROFESSOR',
  DISTINGUISHED_PROFESSOR = 'DISTINGUISHED_PROFESSOR',
  EMERITUS_PROFESSOR = 'EMERITUS_PROFESSOR',
  VISITING_PROFESSOR = 'VISITING_PROFESSOR',
  ADJUNCT_PROFESSOR = 'ADJUNCT_PROFESSOR'
}

export interface RegistrationRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  departmentId: string;
  title: ProfessorTitle;
  officeLocation?: string;
  phone?: string;
  researchInterests?: string[];
}