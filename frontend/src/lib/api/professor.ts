// src/lib/api/professor.ts
import axios from 'axios';
import { Professor } from '@/types/professor';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const professorService = {
  async getProfessorById(id: string): Promise<Professor> {
    const token = localStorage.getItem('accessToken');
    const response = await axios.get<Professor>(`${API_URL}/professors/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  async getProfessorsByIds(ids: string[]): Promise<Professor[]> {
    const token = localStorage.getItem('accessToken');
    // Using Promise.all to fetch multiple professors in parallel
    const professors = await Promise.all(
      ids.map(id => 
        axios.get<Professor>(`${API_URL}/professors/${id}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })
      )
    );
    return professors.map(response => response.data);
  }
};