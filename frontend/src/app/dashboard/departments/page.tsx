// src/app/dashboard/departments/page.tsx
"use client";

import { useEffect, useState } from "react";
import { Department } from "@/types/department";
import { departmentService } from "@/lib/api/department";
import { DepartmentList } from "@/components/departments/DepartmentList";
import { Button } from "@/components/ui/button";
import { Plus, Loader2 } from "lucide-react";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function DepartmentsPage() {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const data = await departmentService.getAllDepartments();
        setDepartments(data);
      } catch (err) {
        setError("Failed to load departments");
        console.error("Error loading departments:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchDepartments();
  }, []);

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
            <h1 className="text-2xl font-bold text-gray-900">Departments</h1>
            <p className="text-gray-600 mt-1">
              Manage and view all academic departments
            </p>
          </div>
          {user?.role === UserRole.ADMIN && (
            <Button className="flex items-center gap-2 shrink-0">
              <Plus className="h-4 w-4" />
              <span>Add Department</span>
            </Button>
          )}
        </div>

        <DepartmentList departments={departments} />
      </div>
    </DashboardLayout>
  );
}
