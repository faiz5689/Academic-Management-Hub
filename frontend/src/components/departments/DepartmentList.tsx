// src/components/departments/DepartmentList.tsx
import { Department, categorizeDepartments } from "@/types/department";
import { DepartmentCard } from "./DepartmentCard";

interface DepartmentListProps {
  departments: Department[];
}

export function DepartmentList({ departments }: DepartmentListProps) {
  const categories = categorizeDepartments(departments);

  if (departments.length === 0) {
    return (
      <div className="text-center py-12 bg-gray-50 rounded-lg">
        <p className="text-gray-600">No departments found.</p>
      </div>
    );
  }

  return (
    <div className="space-y-10">
      {categories.map((category) => (
        <div key={category.name} className="space-y-4">
          <h2 className="text-2xl font-bold text-gray-800 border-b pb-2">
            {category.name}
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {category.departments.map((department) => (
              <DepartmentCard key={department.id} department={department} />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
