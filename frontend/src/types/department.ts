// src/types/department.ts

export interface Department {
  id: string;
  name: string;
  description?: string;
  headProfessorId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DepartmentCategory {
  name: string;
  departments: Department[];
}

// Helper function to categorize departments
export function categorizeDepartments(departments: Department[]): DepartmentCategory[] {
  const categories: { [key: string]: Department[] } = {
    'Engineering & Technology': [],
    'Sciences': [],
    'Business & Economics': [],
    'Arts & Humanities': [],
    'Social Sciences': []
  };

  // Categorization rules
  departments.forEach(dept => {
    if (dept.name.includes('Engineering') || dept.name.includes('Computer')) {
      categories['Engineering & Technology'].push(dept);
    } else if (['Physics', 'Mathematics', 'Chemistry', 'Biology', 'Environmental Science'].some(s => dept.name.includes(s))) {
      categories['Sciences'].push(dept);
    } else if (['Business', 'Economics', 'Marketing', 'Accounting'].some(s => dept.name.includes(s))) {
      categories['Business & Economics'].push(dept);
    } else if (['English', 'History', 'Philosophy', 'Arts'].some(s => dept.name.includes(s))) {
      categories['Arts & Humanities'].push(dept);
    } else if (['Psychology', 'Sociology', 'Political', 'Communication'].some(s => dept.name.includes(s))) {
      categories['Social Sciences'].push(dept);
    }
  });

  return Object.entries(categories)
    .filter(([_, depts]) => depts.length > 0)
    .map(([name, departments]) => ({
      name,
      departments
    }));
}