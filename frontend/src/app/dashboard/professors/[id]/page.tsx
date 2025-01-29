// src/app/dashboard/professors/[id]/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import DashboardLayout from "@/components/layout/DashboardLayout";
import { Professor } from "@/types/professor";
import { Department } from "@/types/department";
import { professorService } from "@/lib/api/professor";
import { departmentService } from "@/lib/api/department";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Loader2,
  Mail,
  Phone,
  MapPin,
  Building2,
  GraduationCap,
  Edit,
} from "lucide-react";
import { formatProfessorTitle, getProfessorEmail } from "@/lib/utils/professor";
import Toast from "@/components/ui/Toast";
import { EditProfessorModal } from "@/components/professors/EditProfessorModal";

export default function ProfessorDetailsPage() {
  const params = useParams();
  const { user } = useAuth();
  const [professor, setProfessor] = useState<Professor | null>(null);
  const [department, setDepartment] = useState<Department | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");

  useEffect(() => {
    const fetchProfessorAndDepartment = async () => {
      try {
        setLoading(true);
        const profData = await professorService.getProfessorById(
          params.id as string
        );
        setProfessor(profData);

        // Fetch department details if professor has a department
        if (profData.departmentId) {
          try {
            const deptData = await departmentService.getDepartmentById(
              profData.departmentId
            );
            setDepartment(deptData);
          } catch (err) {
            console.error("Error loading department:", err);
            setToastMessage("Failed to load department details");
            setToastType("error");
            setShowToast(true);
          }
        }
      } catch (err) {
        setError("Failed to load professor details");
        console.error("Error loading professor:", err);
      } finally {
        setLoading(false);
      }
    };

    if (params.id) {
      fetchProfessorAndDepartment();
    }
  }, [params.id]);

  if (loading) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center min-h-[400px]">
          <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
        </div>
      </DashboardLayout>
    );
  }

  if (error || !professor) {
    return (
      <DashboardLayout>
        <div className="text-center py-12 bg-red-50 rounded-lg">
          <p className="text-red-600 font-semibold text-lg mb-4">
            {error || "Professor not found"}
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
              {`${professor.firstName} ${professor.lastName}`}
            </h1>
            <p className="text-gray-600 mt-1">
              {formatProfessorTitle(professor.title)}
            </p>
          </div>
          {(user?.role === UserRole.ADMIN || user?.id === professor.userId) && (
            <div className="flex items-center gap-3 shrink-0">
              <EditProfessorModal
                professor={professor}
                onProfessorUpdated={(updatedProfessor) => {
                  setProfessor(updatedProfessor);
                  setToastMessage("Professor updated successfully");
                  setToastType("success");
                  setShowToast(true);
                }}
              />
            </div>
          )}
        </div>

        {/* Professor Info Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Contact Information Card */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Contact Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center space-x-2">
                <Mail className="h-5 w-5 text-blue-500" />
                <span className="text-gray-600">
                  {getProfessorEmail(professor)}
                </span>
              </div>
              {professor.phone && (
                <div className="flex items-center space-x-2">
                  <Phone className="h-5 w-5 text-blue-500" />
                  <span className="text-gray-600">{professor.phone}</span>
                </div>
              )}
              {professor.officeLocation && (
                <div className="flex items-center space-x-2">
                  <MapPin className="h-5 w-5 text-blue-500" />
                  <span className="text-gray-600">
                    {professor.officeLocation}
                  </span>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Department Information Card */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Department Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center space-x-2">
                <Building2 className="h-5 w-5 text-blue-500" />
                <span className="text-gray-600">
                  {department ? department.name : "No department assigned"}
                </span>
              </div>
              {department?.description && (
                <p className="text-sm text-gray-500">
                  {department.description}
                </p>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Research Interests Section */}
        {professor.researchInterests &&
          professor.researchInterests.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Research Interests</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex flex-wrap gap-2">
                  {professor.researchInterests.map((interest) => (
                    <span
                      key={interest}
                      className="inline-flex items-center rounded-full bg-blue-50 px-3 py-1 text-sm font-medium text-blue-700"
                    >
                      {interest}
                    </span>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}
      </div>

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
