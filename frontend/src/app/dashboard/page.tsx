// src/app/dashboard/page.tsx
'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <div className="p-4">
        <h1 className="text-2xl font-bold">Dashboard</h1>
      </div>
    </ProtectedRoute>
  );
}