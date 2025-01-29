// src/components/professors/ProfessorCard.tsx
"use client";

import { useEffect, useState } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Professor } from "@/types/professor";
import { departmentService } from "@/lib/api/department";
import Link from "next/link";
import {
  MapPin,
  Phone,
  GraduationCap,
  Pencil,
  Building2,
  Mail,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import { formatProfessorTitle, getProfessorEmail } from "@/lib/utils/professor";

interface ProfessorCardProps {
  professor: Professor;
  onProfessorUpdate?: (updatedProfessor: Professor) => void;
}

export function ProfessorCard({
  professor,
  onProfessorUpdate,
}: ProfessorCardProps) {
  const { user } = useAuth();
  const [departmentName, setDepartmentName] = useState<string>("");
  const [loadingDepartment, setLoadingDepartment] = useState(false);

  useEffect(() => {
    const fetchDepartmentName = async () => {
      if (professor.departmentId) {
        setLoadingDepartment(true);
        try {
          const department = await departmentService.getDepartmentById(
            professor.departmentId
          );
          setDepartmentName(department.name);
        } catch (error) {
          console.error("Error fetching department:", error);
        } finally {
          setLoadingDepartment(false);
        }
      }
    };

    fetchDepartmentName();
  }, [professor.departmentId]);

  return (
    <div className="relative">
      <Link href={`/dashboard/professors/${professor.id}`} className="block">
        <Card className="transform transition-all duration-200 hover:scale-[1.02] hover:shadow-lg bg-white border-gray-200">
          <CardHeader className="pb-2">
            <div className="flex justify-between items-start">
              <div>
                <CardTitle className="text-xl font-bold text-gray-900">
                  {`${professor.firstName} ${professor.lastName}`}
                </CardTitle>
                <div className="space-y-1">
                  <p className="text-sm text-gray-600">
                    {formatProfessorTitle(professor.title)}
                  </p>
                  {professor.departmentId && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Building2 className="h-3 w-3 mr-1 text-blue-500" />
                      {loadingDepartment ? (
                        <span className="text-gray-400">
                          Loading department...
                        </span>
                      ) : (
                        <span>{departmentName}</span>
                      )}
                    </div>
                  )}
                </div>
              </div>
              {(user?.role === UserRole.ADMIN ||
                user?.id === professor.userId) && (
                <Button
                  size="sm"
                  variant="ghost"
                  className="h-8 w-8 p-0"
                  onClick={(e) => {
                    e.preventDefault();
                    // EditProfessorModal will handle this
                  }}
                >
                  <Pencil className="h-4 w-4" />
                </Button>
              )}
            </div>
          </CardHeader>
          <CardContent>
            {professor.officeLocation && (
              <div className="flex items-center text-sm text-gray-600 mb-2">
                <MapPin className="h-4 w-4 mr-2 text-blue-500" />
                <span>{professor.officeLocation}</span>
              </div>
            )}
            <div className="flex items-center text-sm text-gray-600 mb-2">
              <Mail className="h-4 w-4 mr-2 text-blue-500" />
              <span>{getProfessorEmail(professor)}</span>
            </div>
            {professor.phone && (
              <div className="flex items-center text-sm text-gray-600 mb-2">
                <Phone className="h-4 w-4 mr-2 text-blue-500" />
                <span>{professor.phone}</span>
              </div>
            )}
            {professor.researchInterests &&
              professor.researchInterests.length > 0 && (
                <div className="mt-3">
                  <div className="flex items-center text-sm text-gray-600 mb-2">
                    <GraduationCap className="h-4 w-4 mr-2 text-blue-500" />
                    <span>Research Interests</span>
                  </div>
                  <div className="flex flex-wrap gap-1">
                    {professor.researchInterests.slice(0, 3).map((interest) => (
                      <span
                        key={interest}
                        className="inline-flex items-center rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-medium text-blue-700"
                      >
                        {interest}
                      </span>
                    ))}
                    {professor.researchInterests.length > 3 && (
                      <span className="text-xs text-gray-500">
                        +{professor.researchInterests.length - 3} more
                      </span>
                    )}
                  </div>
                </div>
              )}
          </CardContent>
        </Card>
      </Link>
    </div>
  );
}
