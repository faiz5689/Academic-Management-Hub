export interface User {
  id: string;
  name: string;
  email: string;
  role: 'PROFESSOR' | 'ADMIN';
  department: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}