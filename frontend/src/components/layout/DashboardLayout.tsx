// src/components/layout/DashboardLayout.tsx
"use client";

import { useState } from "react";
import { useAuth } from "@/lib/hooks/useAuth";
import { Menu, X, ChevronDown, LogOut, User, Settings } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";

interface DashboardLayoutProps {
  children: React.ReactNode;
}

const DashboardLayout = ({ children }: DashboardLayoutProps) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = async () => {
    try {
      await logout();
      router.push("/auth/login");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  const closeProfileDropdown = () => {
    setProfileDropdownOpen(false);
  };

  // Close dropdown when clicking outside
  const handleClickOutside = (event: React.MouseEvent<HTMLDivElement>) => {
    const target = event.target as HTMLElement;
    if (!target.closest(".profile-dropdown")) {
      closeProfileDropdown();
    }
  };

  return (
    <div className="min-h-screen bg-gray-50" onClick={handleClickOutside}>
      {/* Header */}
      <header className="bg-white shadow-sm fixed w-full z-10">
        <div className="flex items-center justify-between px-4 py-3">
          <div className="flex items-center">
            <button
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded-md"
            >
              {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
            <h1 className="ml-4 text-xl font-semibold text-gray-800">
              Academic Management Hub
            </h1>
          </div>

          {/* Profile Dropdown */}
          <div className="relative profile-dropdown">
            <button
              onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
              className="flex items-center space-x-2 text-gray-700 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded-md px-2 py-1"
            >
              <span className="text-sm font-medium">{user?.email}</span>
              <ChevronDown size={20} />
            </button>

            {profileDropdownOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 border border-gray-200">
                <Link
                  href="/dashboard/profile"
                  className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  onClick={closeProfileDropdown}
                >
                  <User size={16} className="mr-2" />
                  Profile
                </Link>
                <Link
                  href="/dashboard/settings"
                  className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  onClick={closeProfileDropdown}
                >
                  <Settings size={16} className="mr-2" />
                  Settings
                </Link>
                <button
                  onClick={handleLogout}
                  className="flex items-center w-full px-4 py-2 text-sm text-red-700 hover:bg-gray-100"
                >
                  <LogOut size={16} className="mr-2" />
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Sidebar */}
      <aside
        className={`fixed left-0 top-[57px] h-[calc(100vh-57px)] w-64 bg-white shadow-md transform transition-transform duration-200 ease-in-out ${
          sidebarOpen ? "translate-x-0" : "-translate-x-full"
        } overflow-y-auto`}
      >
        <nav className="mt-4 space-y-1">
          <Link
            href="/dashboard"
            className="flex items-center px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors duration-200"
          >
            Dashboard
          </Link>
          <Link
            href="/dashboard/departments"
            className="flex items-center px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors duration-200"
          >
            Departments
          </Link>
          <Link
            href="/dashboard/professors"
            className="flex items-center px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors duration-200"
          >
            Professors
          </Link>
          {user?.role === "ADMIN" && (
            <Link
              href="/dashboard/admin"
              className="flex items-center px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors duration-200"
            >
              Admin Panel
            </Link>
          )}
        </nav>
      </aside>

      {/* Main Content */}
      <main
        className={`pt-[57px] min-h-screen transition-all duration-200 ${
          sidebarOpen ? "ml-64" : "ml-0"
        }`}
      >
        <div className="p-6">{children}</div>
      </main>
    </div>
  );
};

export default DashboardLayout;
