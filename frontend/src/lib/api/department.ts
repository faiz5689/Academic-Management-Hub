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

  async assignHeadProfessor(departmentId: string, professorId: string): Promise<Department> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.put<Department>(
      `${API_URL}/departments/${departmentId}/assign-head/${professorId}`,
      {},
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  },

  async getAllDepartments(): Promise<Department[]> {
    const token = localStorage.getItem('accessToken');
    // Use the details endpoint to get professors data
    const response = await axios.get<Department[]>(`${API_URL}/departments`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    
    // Fetch details for each department
    const departmentsWithDetails = await Promise.all(
      response.data.map(async (dept) => {
        try {
          const detailsResponse = await axios.get<Department>(
            `${API_URL}/departments/${dept.id}/details`,
            {
              headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
              }
            }
          );
          return detailsResponse.data;
        } catch (error) {
          console.error(`Error fetching details for department ${dept.id}:`, error);
          return dept; // Return original department if details fetch fails
        }
      })
    );

    return departmentsWithDetails;
  },

  async getDepartmentById(id: string): Promise<Department> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.get<Department>(`${API_URL}/departments/${id}/details`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  async getDepartmentDetails(id: string): Promise<Department> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.get<Department>(`${API_URL}/departments/${id}/details`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  async updateDepartment(
    id: string,
    data: { name: string; description: string }
  ): Promise<Department> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.put<Department>(
      `${API_URL}/departments/${id}`,
      data,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  },

  async createDepartment(data: { 
    name: string; 
    description: string 
  }): Promise<Department> {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      throw new Error('No authentication token found');
    }
  
    try {
      const response = await axios.post<Department>(
        `${API_URL}/departments`,
        data,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Unauthorized: Please log in again');
        } else if (error.response?.status === 403) {
          throw new Error('Forbidden: You do not have permission to create departments');
        } else if (error.response?.status === 400) {
          throw new Error(error.response.data?.message || 'Invalid department data');
        }
      }
      throw new Error('Failed to create department');
    }
  }
};