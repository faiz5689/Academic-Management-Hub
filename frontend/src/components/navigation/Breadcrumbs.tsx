// src/components/navigation/Breadcrumbs.tsx
"use client";

import { usePathname } from "next/navigation";
import Link from "next/link";
import { ChevronRight } from "lucide-react";
import { useEffect, useState } from "react";
import { departmentService } from "@/lib/api/department";
import { professorService } from "@/lib/api/professor";
import type { Department } from "@/types/department";

const Breadcrumbs = () => {
  const pathname = usePathname();
  const [departmentName, setDepartmentName] = useState<string | null>(null);
  const [professorName, setProfessorName] = useState<string | null>(null);

  useEffect(() => {
    const fetchNames = async () => {
      // Check if we're on a department details page
      const departmentMatch = pathname.match(
        /\/dashboard\/departments\/([\w-]+)/
      );
      if (departmentMatch) {
        try {
          const department = await departmentService.getDepartmentById(
            departmentMatch[1]
          );
          setDepartmentName(department.name);
        } catch (error) {
          console.error("Error fetching department:", error);
        }
      }

      // Check if we're on a professor details page
      const professorMatch = pathname.match(
        /\/dashboard\/professors\/([\w-]+)/
      );
      if (professorMatch) {
        try {
          const professor = await professorService.getProfessorById(
            professorMatch[1]
          );
          setProfessorName(`${professor.firstName} ${professor.lastName}`);
        } catch (error) {
          console.error("Error fetching professor:", error);
        }
      }
    };

    fetchNames();
  }, [pathname]);

  const generateBreadcrumbs = () => {
    // Remove trailing slash and split path into segments
    const segments = pathname
      .replace(/\/$/, "")
      .split("/")
      .filter((segment) => segment !== "");

    // Generate breadcrumb items
    return segments.map((segment, index) => {
      // Create the URL for this breadcrumb
      const url = `/${segments.slice(0, index + 1).join("/")}`;

      // Check if this segment is a department ID or professor ID
      const isDepartmentId =
        segments[index - 1] === "departments" && segment.match(/[\w-]{36}/);
      const isProfessorId =
        segments[index - 1] === "professors" && segment.match(/[\w-]{36}/);

      // Format the segment text
      let text = segment;
      if (isDepartmentId && departmentName) {
        text = departmentName;
      } else if (isProfessorId && professorName) {
        text = professorName;
      } else {
        text = segment
          .replace(/-/g, " ")
          .replace(/\b\w/g, (char) => char.toUpperCase());
      }

      return {
        text,
        url,
        isLast: index === segments.length - 1,
      };
    });
  };

  const breadcrumbs = generateBreadcrumbs();

  if (breadcrumbs.length <= 1) return null;

  return (
    <nav aria-label="Breadcrumb" className="mb-4">
      <ol className="flex items-center space-x-2 text-sm">
        <li>
          <Link
            href="/dashboard"
            className="text-gray-500 hover:text-gray-700 transition-colors duration-200"
          >
            Dashboard
          </Link>
        </li>
        {breadcrumbs.slice(1).map((breadcrumb, index) => (
          <li key={breadcrumb.url} className="flex items-center space-x-2">
            <ChevronRight size={16} className="text-gray-400" />
            {breadcrumb.isLast ? (
              <span className="font-medium text-gray-800">
                {breadcrumb.text}
              </span>
            ) : (
              <Link
                href={breadcrumb.url}
                className="text-gray-500 hover:text-gray-700 transition-colors duration-200"
              >
                {breadcrumb.text}
              </Link>
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
};

export default Breadcrumbs;
