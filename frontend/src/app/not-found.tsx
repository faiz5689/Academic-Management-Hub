// src/app/not-found.tsx
"use client";

import Link from "next/link";
import { Home } from "lucide-react";
import { useAuth } from "@/lib/hooks/useAuth";

export default function NotFound() {
  const { user } = useAuth();
  const linkHref = user ? "/dashboard" : "/auth/login";

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8 text-center">
        <div>
          <h1 className="text-9xl font-bold text-blue-600">404</h1>
          <h2 className="mt-6 text-3xl font-bold text-gray-900">
            Page Not Found
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Sorry, we couldn't find the page you're looking for.
          </p>
        </div>
        <div className="mt-8">
          <Link
            href={linkHref}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            <Home className="h-5 w-5 mr-2" />
            {user ? "Back to Dashboard" : "Go to Login"}
          </Link>
        </div>
      </div>
    </div>
  );
}
