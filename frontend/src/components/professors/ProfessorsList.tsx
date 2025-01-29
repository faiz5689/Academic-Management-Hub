// src/components/professors/ProfessorsList.tsx
import { useEffect, useState } from "react";
import { Professor, ProfessorTitle } from "@/types/professor";
import { professorService } from "@/lib/api/professor";
import { Card } from "@/components/ui/card";
import { UserCircle } from "lucide-react";
import Link from "next/link";
import { formatProfessorTitle } from "@/lib/utils/professor";

interface ProfessorsListProps {
  professorIds: string[];
}

export function ProfessorsList({ professorIds }: ProfessorsListProps) {
  const [professors, setProfessors] = useState<Professor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfessors = async () => {
      try {
        if (professorIds.length === 0) {
          setProfessors([]);
          setLoading(false);
          return;
        }

        const data = await professorService.getProfessorsByIds(professorIds);
        setProfessors(data);
      } catch (err) {
        console.error("Error fetching professors:", err);
        setError("Failed to load professors");
      } finally {
        setLoading(false);
      }
    };

    fetchProfessors();
  }, [professorIds]);

  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {[...Array(3)].map((_, i) => (
          <Card key={i} className="h-32 p-6">
            <div className="flex items-start space-x-4">
              <div className="h-12 w-12 rounded-full bg-gray-200 animate-pulse" />
              <div className="flex-1 space-y-3">
                <div className="h-4 w-3/4 bg-gray-200 rounded animate-pulse" />
                <div className="h-4 w-1/2 bg-gray-200 rounded animate-pulse" />
              </div>
            </div>
          </Card>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-4">
        <p className="text-red-600">{error}</p>
      </div>
    );
  }

  if (professors.length === 0) {
    return (
      <div className="text-center py-4">
        <p className="text-gray-600">
          No professors assigned to this department.
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {professors.map((professor) => (
        <Link key={professor.id} href={`/dashboard/professors/${professor.id}`}>
          <Card className="h-32 p-6 hover:shadow-md transition-shadow">
            <div className="flex items-start space-x-4">
              <UserCircle className="h-12 w-12 text-gray-400 flex-shrink-0" />
              <div className="flex-1 min-w-0">
                <h3 className="text-lg font-semibold text-gray-900 truncate">
                  {`${professor.firstName} ${professor.lastName}`}
                </h3>
                <p className="text-gray-600 mb-2">
                  {formatProfessorTitle(professor.title)}
                </p>
                {professor.researchInterests &&
                  professor.researchInterests.length > 0 && (
                    <div className="flex flex-wrap gap-1">
                      {professor.researchInterests.map(
                        (interest: string, index: number) => {
                          if (index < 2) {
                            return (
                              <span
                                key={interest}
                                className="inline-flex items-center rounded-md bg-blue-50 px-2 py-0.5 text-xs font-medium text-blue-700"
                              >
                                {interest}
                              </span>
                            );
                          }
                          return null;
                        }
                      )}
                      {professor.researchInterests.length > 2 && (
                        <span className="text-xs text-gray-500">
                          +{professor.researchInterests.length - 2}
                        </span>
                      )}
                    </div>
                  )}
              </div>
            </div>
          </Card>
        </Link>
      ))}
    </div>
  );
}
