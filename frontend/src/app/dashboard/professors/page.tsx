// src/app/dashboard/professors/page.tsx
"use client";

import { useEffect, useState } from "react";
import { Professor } from "@/types/professor";
import { professorService } from "@/lib/api/professor";
import { Button } from "@/components/ui/button";
import { Plus, Loader2 } from "lucide-react";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import DashboardLayout from "@/components/layout/DashboardLayout";
import { ProfessorCard } from "@/components/professors/ProfessorCard";
import { CreateProfessorModal } from "@/components/professors/CreateProfessorModal";

export default function ProfessorsPage() {
  const [professors, setProfessors] = useState<Professor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  useEffect(() => {
    const fetchProfessors = async () => {
      try {
        const data = await professorService.getAllProfessors();
        setProfessors(data);
      } catch (err) {
        setError("Failed to load professors");
        console.error("Error loading professors:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfessors();
  }, []);

  const handleProfessorCreate = (newProfessor: Professor) => {
    setProfessors((prevProfessors) => [...prevProfessors, newProfessor]);
  };

  if (loading) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center min-h-[400px]">
          <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
        </div>
      </DashboardLayout>
    );
  }

  if (error) {
    return (
      <DashboardLayout>
        <div className="text-center py-12 bg-red-50 rounded-lg">
          <p className="text-red-600 font-semibold text-lg mb-4">{error}</p>
          <Button
            variant="outline"
            className="hover:bg-red-50"
            onClick={() => window.location.reload()}
          >
            Try Again
          </Button>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout>
      <div className="space-y-8">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Professors</h1>
            <p className="text-gray-600 mt-1">
              View and manage faculty members
            </p>
          </div>
          {user?.role === UserRole.ADMIN && (
            <CreateProfessorModal
              trigger={
                <Button className="flex items-center gap-2 shrink-0">
                  <Plus className="h-4 w-4" />
                  <span>Add Professor</span>
                </Button>
              }
              onProfessorCreated={handleProfessorCreate}
            />
          )}
        </div>

        {professors.length === 0 ? (
          <div className="text-center py-12 bg-gray-50 rounded-lg">
            <p className="text-gray-600">No professors found.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {professors.map((professor) => (
              <ProfessorCard
                key={professor.id}
                professor={professor}
                onProfessorUpdate={(updatedProfessor) => {
                  setProfessors((prevProfessors) =>
                    prevProfessors.map((p) =>
                      p.id === updatedProfessor.id ? updatedProfessor : p
                    )
                  );
                }}
              />
            ))}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
