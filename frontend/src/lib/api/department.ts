// src/lib/api/department.ts
import axios from 'axios';
import { Department } from '@/types/department';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const departmentService = {
  async getPublicDepartments(): Promise<Department[]> {
    try {
      const response = await axios.get<Department[]>(`${API_URL}/public/departments`);
      return response.data;
    } catch (error) {
      console.error('Error fetching departments:', error);
      throw error;
    }
  },

  async getAllDepartments(): Promise<Department[]> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.get<Department[]>(`${API_URL}/departments`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  async getDepartmentById(id: string): Promise<Department> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.get<Department>(`${API_URL}/departments/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  }
};