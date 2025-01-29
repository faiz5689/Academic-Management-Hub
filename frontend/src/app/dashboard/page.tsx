// src/app/dashboard/page.tsx
"use client";

import { useState, useEffect } from "react";
import { Loader2, Users, Building2, Calendar, Search } from "lucide-react";
import Link from "next/link";
import { useAuth } from "@/lib/hooks/useAuth";
import { departmentService } from "@/lib/api/department";
import { professorService } from "@/lib/api/professor";
import type { Department } from "@/types/department";
import type { Professor } from "@/types/professor";
import ProtectedRoute from "@/components/auth/ProtectedRoute";
import DashboardLayout from "@/components/layout/DashboardLayout";
import { Input } from "@/components/ui/input";

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  description: string;
}

const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  icon,
  description,
}) => (
  <div className="bg-white p-6 rounded-lg shadow-sm">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm font-medium text-gray-600">{title}</p>
        <p className="text-2xl font-semibold text-gray-900 mt-2">{value}</p>
      </div>
      <div className="text-blue-500">{icon}</div>
    </div>
    <p className="text-sm text-gray-600 mt-2">{description}</p>
  </div>
);

const QuickActionCard: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => (
  <div className="bg-white p-6 rounded-lg shadow-sm">
    <h2 className="text-lg font-semibold text-gray-800 mb-4">Quick Actions</h2>
    <div className="space-y-3">{children}</div>
  </div>
);

const ActionLink: React.FC<{ href: string; children: React.ReactNode }> = ({
  href,
  children,
}) => (
  <Link
    href={href}
    className="flex items-center text-sm text-gray-600 hover:text-blue-600 transition-colors duration-200"
  >
    {children}
  </Link>
);

interface DepartmentRowProps {
  department: Department;
}

const DepartmentRow: React.FC<DepartmentRowProps> = ({ department }) => {
  const [headProfessor, setHeadProfessor] = useState<Professor | null>(null);
  const [loading, setLoading] = useState(false);

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

  const renderHeadProfessor = () => {
    if (loading) {
      return (
        <div className="flex items-center gap-2">
          <Loader2 className="h-3 w-3 animate-spin" />
          <span>Loading...</span>
        </div>
      );
    }

    if (headProfessor) {
      return (
        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
          {headProfessor.firstName} {headProfessor.lastName}
        </span>
      );
    }

    return (
      <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
        Not Assigned
      </span>
    );
  };

  return (
    <tr className="hover:bg-gray-50">
      <td className="px-6 py-4 text-sm">
        <Link
          href={`/dashboard/departments/${department.id}`}
          className="text-gray-900 hover:text-blue-600 font-medium"
        >
          {department.name}
        </Link>
      </td>
      <td className="px-6 py-4 text-sm text-gray-600">
        {renderHeadProfessor()}
      </td>
      <td className="px-6 py-4 text-sm text-gray-600">
        {department.professorCount || 0}
      </td>
    </tr>
  );
};

const getCurrentSemester = () => {
  const now = new Date();
  const month = now.getMonth();
  const year = now.getFullYear();

  if (month >= 0 && month <= 4) {
    return `Spring ${year}`;
  } else if (month >= 5 && month <= 6) {
    return `Summer ${year}`;
  } else {
    return `Fall ${year}`;
  }
};

export default function DashboardPage() {
  const { user } = useAuth();
  const [departments, setDepartments] = useState<Department[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        setLoading(true);
        const deps = await departmentService.getAllDepartments();
        setDepartments(deps);
      } catch (error) {
        console.error("Error fetching departments:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDepartments();
  }, []);

  const filteredDepartments = departments.filter(
    (dept) =>
      dept.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      dept.description?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const totalProfessors = departments.reduce(
    (total, dept) => total + (dept.professorCount || 0),
    0
  );

  return (
    <ProtectedRoute>
      <DashboardLayout>
        <div className="space-y-6">
          <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>

          {/* Statistics Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <StatCard
              title="Total Departments"
              value={departments.length}
              icon={<Building2 size={24} />}
              description="Active academic departments"
            />
            <StatCard
              title="Total Professors"
              value={totalProfessors}
              icon={<Users size={24} />}
              description="Faculty members"
            />
            <StatCard
              title="Current Semester"
              value={getCurrentSemester()}
              icon={<Calendar size={24} />}
              description="Academic period"
            />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Quick Actions */}
            <div className="lg:col-span-1">
              <QuickActionCard>
                <ActionLink href="/dashboard/profile">
                  → View/Edit Profile
                </ActionLink>
                <ActionLink href="/dashboard/departments">
                  → Browse Departments
                </ActionLink>
                <ActionLink href="/dashboard/professors">
                  → Find Professors
                </ActionLink>
                {user?.role === "ADMIN" && (
                  <ActionLink href="/dashboard/admin">
                    → Admin Settings
                  </ActionLink>
                )}
              </QuickActionCard>
            </div>

            {/* Department Overview */}
            <div className="lg:col-span-2">
              <div className="bg-white rounded-lg shadow-sm">
                <div className="p-6 border-b">
                  <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <h2 className="text-lg font-semibold text-gray-800">
                      All Departments
                    </h2>
                    <div className="relative w-full sm:w-64">
                      <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                      <Input
                        type="text"
                        placeholder="Search departments..."
                        value={searchQuery}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                          setSearchQuery(e.target.value)
                        }
                        className="pl-10"
                      />
                    </div>
                  </div>
                </div>
                <div className="overflow-x-auto">
                  <div className="max-h-[400px] overflow-y-auto">
                    <table className="min-w-full">
                      <thead className="bg-gray-50 sticky top-0">
                        <tr>
                          <th className="text-left text-sm font-medium text-gray-500 px-6 py-3">
                            Department Name
                          </th>
                          <th className="text-left text-sm font-medium text-gray-500 px-6 py-3">
                            Head Professor
                          </th>
                          <th className="text-left text-sm font-medium text-gray-500 px-6 py-3">
                            Faculty Count
                          </th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-200">
                        {loading ? (
                          <tr>
                            <td colSpan={3} className="px-6 py-4 text-center">
                              <div className="flex justify-center items-center">
                                <Loader2 className="h-6 w-6 animate-spin text-blue-500" />
                              </div>
                            </td>
                          </tr>
                        ) : filteredDepartments.length === 0 ? (
                          <tr>
                            <td
                              colSpan={3}
                              className="px-6 py-4 text-center text-gray-500"
                            >
                              No departments found
                            </td>
                          </tr>
                        ) : (
                          filteredDepartments.map((dept) => (
                            <DepartmentRow key={dept.id} department={dept} />
                          ))
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  );
}
