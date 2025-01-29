// src/lib/api/professor.ts
import axios from 'axios';
import { Professor } from '@/types/professor';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const professorService = {
  async getAllProfessors(): Promise<Professor[]> {
    const token = localStorage.getItem('accessToken');
    // Get professors with their department information included
    const response = await axios.get<Professor[]>(`${API_URL}/professors`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      params: {
        include: 'department' // Assuming your API supports this query parameter
      }
    });
    return response.data;
  },

  async getProfessorById(id: string): Promise<Professor> {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      throw new Error('No authentication token found');
    }

    try {
      const response = await axios.get<Professor>(`${API_URL}/professors/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching professor:', error);
      throw error;
    }
  },

  async getProfessorsByIds(ids: string[]): Promise<Professor[]> {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      throw new Error('No authentication token found');
    }

    if (ids.length === 0) {
      return [];
    }

    try {
      // Using Promise.all to fetch multiple professors in parallel
      const professors = await Promise.all(
        ids.map(id => 
          this.getProfessorById(id)
        )
      );
      return professors;
    } catch (error) {
      console.error('Error fetching professors:', error);
      throw error;
    }
  },

  async updateProfessor(
    id: string,
    data: {
      title: string;
      firstName: string;
      lastName: string;
      officeLocation?: string;
      phone?: string;
      researchInterests?: string[];
    }
  ): Promise<Professor> {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      throw new Error('No authentication token found');
    }
  
    try {
      const response = await axios.put<Professor>(
        `${API_URL}/professors/${id}`,
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
          throw new Error('Forbidden: You do not have permission to update this professor');
        } else if (error.response?.status === 400) {
          throw new Error(error.response.data?.message || 'Invalid professor data');
        }
      }
      throw new Error('Failed to update professor');
    }
  },

  async createProfessor(data: {
    title: string;
    firstName: string;
    lastName: string;
    email: string;
    officeLocation?: string;
    phone?: string;
    researchInterests?: string[];
    departmentId?: string;
  }): Promise<Professor> {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      throw new Error('No authentication token found');
    }
  
    try {
      const response = await axios.post<Professor>(
        `${API_URL}/professors`,
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
          throw new Error('Forbidden: You do not have permission to create professors');
        } else if (error.response?.status === 400) {
          throw new Error(error.response.data?.message || 'Invalid professor data');
        }
      }
      throw new Error('Failed to create professor');
    }
  }
};