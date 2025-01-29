"use client";

import { useEffect, useState } from "react";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { Department } from "@/types/department";
import { Professor } from "@/types/professor";
import { professorService } from "@/lib/api/professor";
import Link from "next/link";
import { Users, Loader2, Pencil } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import { EditDepartmentModal } from "./EditDepartmentModal";

interface DepartmentCardProps {
  department: Department;
  onDepartmentUpdate?: (updatedDepartment: Department) => void;
}

export function DepartmentCard({
  department,
  onDepartmentUpdate,
}: DepartmentCardProps) {
  const [headProfessor, setHeadProfessor] = useState<Professor | null>(null);
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    const fetchHeadProfessor = async () => {
      if (department.headProfessorId) {
        setLoading(true);
        try {
          const professor = await professorService.getProfessorById(
            department.headProfessorId
          );
          setHeadProfessor(professor);
        } catch (error) {
          console.error("Error fetching head professor:", error);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchHeadProfessor();
  }, [department.headProfessorId]);

  const renderHeadProfessorStatus = () => {
    if (loading) {
      return (
        <div className="flex items-center gap-2">
          <Loader2 className="h-3 w-3 animate-spin" />
          <span>Loading head professor...</span>
        </div>
      );
    }

    if (headProfessor) {
      return (
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
            Head: {headProfessor.firstName} {headProfessor.lastName}
          </span>
        </div>
      );
    }

    return (
      <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
        No head assigned
      </span>
    );
  };

  const handleDepartmentUpdate = (updatedDepartment: Department) => {
    if (onDepartmentUpdate) {
      onDepartmentUpdate(updatedDepartment);
    }
  };

  return (
    <div className="relative">
      <Link href={`/dashboard/departments/${department.id}`} className="block">
        <Card className="transform transition-all duration-200 hover:scale-[1.02] hover:shadow-lg bg-white border-gray-200">
          <CardHeader className="pb-3">
            <CardTitle className="text-xl font-bold text-gray-900">
              {department.name}
            </CardTitle>
            <CardDescription className="flex items-center text-sm text-gray-500">
              {renderHeadProfessorStatus()}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex items-center text-sm text-gray-600 mb-3">
              <Users className="h-4 w-4 mr-2 text-blue-500" />
              <span>{department.professorCount} Professors</span>
            </div>
            <p className="text-sm text-gray-600 line-clamp-2 mt-2 bg-gray-50 p-3 rounded-md">
              {department.description}
            </p>
          </CardContent>
        </Card>
      </Link>

      {user?.role === UserRole.ADMIN && (
        <div
          className="absolute top-3 right-3"
          onClick={(e) => e.preventDefault()}
        >
          <EditDepartmentModal
            department={department}
            onDepartmentUpdated={handleDepartmentUpdate}
            trigger={
              <Button size="sm" variant="ghost" className="h-8 w-8 p-0">
                <Pencil className="h-4 w-4" />
              </Button>
            }
          />
        </div>
      )}
    </div>
  );
}
