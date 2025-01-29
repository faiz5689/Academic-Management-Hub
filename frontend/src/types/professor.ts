// src/types/professor.ts

// Match the ProfessorTitle enum from Java
export enum ProfessorTitle {
  ASSISTANT_PROFESSOR = 'ASSISTANT_PROFESSOR',
  ASSOCIATE_PROFESSOR = 'ASSOCIATE_PROFESSOR',
  PROFESSOR = 'PROFESSOR',
  DISTINGUISHED_PROFESSOR = 'DISTINGUISHED_PROFESSOR',
  EMERITUS_PROFESSOR = 'EMERITUS_PROFESSOR',
  VISITING_PROFESSOR = 'VISITING_PROFESSOR',
  ADJUNCT_PROFESSOR = 'ADJUNCT_PROFESSOR'
}

export interface Professor {
  id: string;
  userId: string;  // From the @OneToOne User relationship
  departmentId: string;  // From the @ManyToOne Department relationship
  title: ProfessorTitle;
  firstName: string;
  lastName: string;
  officeLocation?: string;
  phone?: string;
  researchInterests?: string[];  // We'll receive this as an array from the API
  createdAt: string;
  updatedAt: string;
}

// Additional interfaces for specific views or responses
export interface ProfessorWithDepartment extends Professor {
  department: {
    id: string;
    name: string;
  };
}

export interface ProfessorDetails extends Professor {
  email: string;  // From associated user
  isActive: boolean;  // From associated user
  lastLogin?: string;  // From associated user
}