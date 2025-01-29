import { ProfessorTitle } from "@/types/professor";

export function formatProfessorTitle(title: ProfessorTitle): string {
  return title
    .split('_')
    .map(word => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ');
}

export function getDefaultEmail(firstName: string, lastName: string): string {
  // Remove any special characters and spaces, convert to lowercase
  const cleanFirstName = firstName.toLowerCase().replace(/[^a-z0-9]/g, '');
  const cleanLastName = lastName.toLowerCase().replace(/[^a-z0-9]/g, '');
  return `${cleanFirstName}${cleanLastName}@name.com`;
}

export function getProfessorEmail(professor: { email?: string; firstName: string; lastName: string }): string {
  return professor.email || getDefaultEmail(professor.firstName, professor.lastName);
}