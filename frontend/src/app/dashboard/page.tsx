// src/app/dashboard/page.tsx
"use client";

import { useState, useEffect } from "react";
import ProtectedRoute from "@/components/auth/ProtectedRoute";
import DashboardLayout from "@/components/layout/DashboardLayout";
import { Users, Building2, Calendar } from "lucide-react";
import Link from "next/link";
import { useAuth } from "@/lib/hooks/useAuth";
import { departmentService } from "@/lib/api/department";
import type { Department } from "@/types/department";

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

const getCurrentSemester = () => {
  const now = new Date();
  const month = now.getMonth(); // 0-11
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

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const deps = await departmentService.getAllDepartments();
        setDepartments(deps);
      } catch (error) {
        console.error("Error fetching departments:", error);
      }
    };

    fetchDepartments();
  }, []);

  return (
    <ProtectedRoute>
      <DashboardLayout>
        <div className="space-y-6">
          <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>

          {/* Statistics Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <StatCard
              title="Total Departments"
              value={departments.length}
              icon={<Building2 size={24} />}
              description="Active academic departments"
            />
            <StatCard
              title="Total Professors"
              value={departments.reduce(
                (acc, dept) => acc + (dept.professors?.length || 0),
                0
              )}
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

          {/* Actions and Recent Activity Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Quick Actions */}
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

            {/* Department Overview */}
            <div className="bg-white p-6 rounded-lg shadow-sm">
              <h2 className="text-lg font-semibold text-gray-800 mb-4">
                Recent Departments
              </h2>
              <div className="overflow-x-auto">
                <table className="min-w-full">
                  <thead>
                    <tr>
                      <th className="text-left text-sm font-medium text-gray-500 pb-3">
                        Name
                      </th>
                      <th className="text-left text-sm font-medium text-gray-500 pb-3">
                        Head Professor
                      </th>
                      <th className="text-left text-sm font-medium text-gray-500 pb-3">
                        Professors
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {departments.slice(0, 5).map((dept) => (
                      <tr key={dept.id} className="border-t">
                        <td className="py-3 text-sm text-gray-900">
                          {dept.name}
                        </td>
                        <td className="py-3 text-sm text-gray-600">
                          {dept.headProfessor
                            ? `${dept.headProfessor.firstName} ${dept.headProfessor.lastName}`
                            : "Not Assigned"}
                        </td>
                        <td className="py-3 text-sm text-gray-600">
                          {dept.professors?.length || 0}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  );
}
