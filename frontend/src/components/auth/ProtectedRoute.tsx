// src/components/auth/ProtectedRoute.tsx
"use client";

import { useAuth } from "@/lib/hooks/useAuth";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { UserRole } from "@/types/auth";

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: UserRole[];
}

export default function ProtectedRoute({
  children,
  allowedRoles,
}: ProtectedRouteProps) {
  const { user, isLoading } = useAuth();
  const router = useRouter();
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    // Don't do anything while loading
    if (isLoading) return;

    // Check if user is authenticated
    if (!user) {
      router.push("/auth/login");
      return;
    }

    // Check role restrictions if specified
    if (allowedRoles && !allowedRoles.includes(user.role)) {
      router.push("/dashboard");
      return;
    }

    setIsAuthorized(true);
  }, [user, isLoading, router, allowedRoles]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  // Only render children if user is authorized
  return isAuthorized ? <>{children}</> : null;
}
