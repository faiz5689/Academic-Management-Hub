import { ProfessorTitle } from "@/types/professor";

export function formatProfessorTitle(title: ProfessorTitle): string {
  return title
    .split('_')
    .map(word => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ');
}