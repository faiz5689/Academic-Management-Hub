// src/components/navigation/Breadcrumbs.tsx
"use client";

import { usePathname } from "next/navigation";
import Link from "next/link";
import { ChevronRight } from "lucide-react";

const Breadcrumbs = () => {
  const pathname = usePathname();

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

      // Format the segment text (capitalize and replace hyphens with spaces)
      const text = segment
        .replace(/-/g, " ")
        .replace(/\b\w/g, (char) => char.toUpperCase());

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
