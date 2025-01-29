// src/app/dashboard/departments/[id]/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import DashboardLayout from "@/components/layout/DashboardLayout";
import { Department } from "@/types/department";
import { Professor } from "@/types/professor";
import { departmentService } from "@/lib/api/department";
import { professorService } from "@/lib/api/professor";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Users, Edit, UserPlus, Loader2 } from "lucide-react";
import { ProfessorsList } from "@/components/professors/ProfessorsList";
import HeadProfessorDialog from "@/components/departments/HeadProfessorDialog";
import Toast from "@/components/ui/Toast";

interface ExtendedDepartment extends Department {
  professorIds?: string[];
}

export default function DepartmentDetailsPage() {
  const params = useParams();
  const { user } = useAuth();
  const [department, setDepartment] = useState<ExtendedDepartment | null>(null);
  const [headProfessor, setHeadProfessor] = useState<Professor | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadingHead, setLoadingHead] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isHeadProfessorDialogOpen, setIsHeadProfessorDialogOpen] =
    useState(false);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");

  // Fetch department details and head professor
  useEffect(() => {
    const fetchDepartmentAndHead = async () => {
      try {
        setLoading(true);
        const data = await departmentService.getDepartmentDetails(
          params.id as string
        );
        setDepartment(data);

        // If there's a head professor, fetch their details
        if (data.headProfessorId) {
          setLoadingHead(true);
          try {
            const headProfData = await professorService.getProfessorById(
              data.headProfessorId
            );
            setHeadProfessor(headProfData);
          } catch (err) {
            console.error("Error loading head professor:", err);
            setToastMessage("Failed to load head professor details");
            setToastType("error");
            setShowToast(true);
          } finally {
            setLoadingHead(false);
          }
        } else {
          setLoadingHead(false);
          setHeadProfessor(null);
        }
      } catch (err) {
        setError("Failed to load department details");
        console.error("Error loading department:", err);
      } finally {
        setLoading(false);
      }
    };

    if (params.id) {
      fetchDepartmentAndHead();
    }
  }, [params.id]);

  const handleAssignHeadProfessor = async (professorId: string) => {
    try {
      setLoadingHead(true); // Set loading state at the start

      // Assign head professor
      const updatedDepartment = await departmentService.assignHeadProfessor(
        department!.id,
        professorId
      );
      setDepartment(updatedDepartment);

      // Immediately fetch new head professor details
      if (professorId) {
        const headProfData = await professorService.getProfessorById(
          professorId
        );
        setHeadProfessor(headProfData);
      } else {
        setHeadProfessor(null);
      }

      setToastMessage("Head professor assigned successfully");
      setToastType("success");
      setShowToast(true);
    } catch (error) {
      console.error("Error assigning head professor:", error);
      setToastMessage("Failed to assign head professor");
      setToastType("error");
      setShowToast(true);
    } finally {
      setLoadingHead(false); // Make sure to clear loading state
    }
  };

  if (loading) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center min-h-[400px]">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
      </DashboardLayout>
    );
  }

  if (error || !department) {
    return (
      <DashboardLayout>
        <div className="text-center py-12 bg-red-50 rounded-lg">
          <p className="text-red-600 font-semibold text-lg mb-4">
            {error || "Department not found"}
          </p>
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
      <div className="space-y-6">
        {/* Header with actions */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              {department.name}
            </h1>
            <p className="text-gray-600 mt-1">{department.description}</p>
          </div>
          {user?.role === UserRole.ADMIN && (
            <div className="flex items-center gap-3 shrink-0">
              <Button variant="outline" className="flex items-center gap-2">
                <Edit className="h-4 w-4" />
                <span>Edit Department</span>
              </Button>
              <Button
                className="flex items-center gap-2"
                onClick={() => setIsHeadProfessorDialogOpen(true)}
              >
                <UserPlus className="h-4 w-4" />
                <span>Assign Head</span>
              </Button>
            </div>
          )}
        </div>

        {/* Department Info Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Head Professor Card */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Head Professor</CardTitle>
            </CardHeader>
            <CardContent>
              {loadingHead ? (
                <div className="flex items-center space-x-2">
                  <Loader2 className="h-4 w-4 animate-spin" />
                  <span>Loading head professor details...</span>
                </div>
              ) : headProfessor ? (
                <div className="space-y-2">
                  <p className="font-medium">
                    {`${headProfessor.firstName} ${headProfessor.lastName}`}
                  </p>
                  {headProfessor.title && (
                    <p className="text-sm text-gray-500">
                      {headProfessor.title}
                    </p>
                  )}
                </div>
              ) : (
                <p className="text-gray-600">No head professor assigned</p>
              )}
            </CardContent>
          </Card>

          {/* Department Stats Card */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Department Statistics</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center space-x-2">
                <Users className="h-5 w-5 text-blue-500" />
                <span className="text-gray-600">
                  {department.professorCount} Professors
                </span>
              </div>
              <div className="text-sm text-gray-500">
                Created: {new Date(department.createdAt).toLocaleDateString()}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Professors List Section */}
        <div className="bg-white rounded-lg shadow-sm">
          <div className="p-6 border-b">
            <h2 className="text-xl font-semibold text-gray-800">
              Department Professors
            </h2>
          </div>
          <div className="p-6">
            {department.professorIds && department.professorIds.length > 0 ? (
              <ProfessorsList professorIds={department.professorIds} />
            ) : (
              <p className="text-gray-600">
                No professors assigned to this department
              </p>
            )}
          </div>
        </div>
      </div>

      {/* Head Professor Assignment Dialog */}
      {department && (
        <HeadProfessorDialog
          isOpen={isHeadProfessorDialogOpen}
          onClose={() => setIsHeadProfessorDialogOpen(false)}
          departmentId={department.id}
          professorIds={department.professorIds || []}
          onAssign={handleAssignHeadProfessor}
        />
      )}

      {/* Toast Notifications */}
      {showToast && (
        <Toast
          message={toastMessage}
          type={toastType}
          onClose={() => setShowToast(false)}
        />
      )}
    </DashboardLayout>
  );
}
