"use client";

import { useEffect, useState } from "react";
import DashboardLayout from "@/components/layout/DashboardLayout";
import { Professor } from "@/types/professor";
import { Department } from "@/types/department";
import { professorService } from "@/lib/api/professor";
import { departmentService } from "@/lib/api/department";
import { useAuth } from "@/lib/hooks/useAuth";
import { UserRole } from "@/types/auth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Loader2,
  Mail,
  Phone,
  MapPin,
  Building2,
  ShieldCheck,
  Eye,
  EyeOff,
} from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { formatProfessorTitle } from "@/lib/utils/professor";
import Toast from "@/components/ui/Toast";
import { EditProfessorModal } from "@/components/professors/EditProfessorModal";
import type { ChangePasswordRequest } from "@/types/auth";
import { authService } from "@/lib/api/auth";

export default function ProfilePage() {
  const { user } = useAuth();
  const [professor, setProfessor] = useState<Professor | null>(null);
  const [department, setDepartment] = useState<Department | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");
  const [showPasswordDialog, setShowPasswordDialog] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false,
  });

  useEffect(() => {
    const fetchProfessorAndDepartment = async () => {
      if (user?.role !== UserRole.PROFESSOR) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const allProfessors = await professorService.getAllProfessors();
        const userProf = allProfessors.find((p) => p.userId === user.id);

        if (!userProf) {
          throw new Error("Professor profile not found");
        }

        const profData = await professorService.getProfessorById(
          `${userProf.id}/details`
        );
        setProfessor(profData);

        if (profData.departmentId) {
          try {
            const deptData = await departmentService.getDepartmentById(
              profData.departmentId
            );
            setDepartment(deptData);
          } catch (err) {
            console.error("Error loading department:", err);
            setToastMessage("Failed to load department details");
            setToastType("error");
            setShowToast(true);
          }
        }
      } catch (err) {
        setError("Failed to load profile details");
        console.error("Error loading profile:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfessorAndDepartment();
  }, [user]);

  const handleChangePassword = async () => {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setToastMessage("Passwords do not match");
      setToastType("error");
      setShowToast(true);
      return;
    }

    try {
      const request: ChangePasswordRequest = {
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      };
      await authService.changePassword(request);
      setShowPasswordDialog(false);
      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setShowPasswords({
        current: false,
        new: false,
        confirm: false,
      });
      setToastMessage("Password changed successfully");
      setToastType("success");
      setShowToast(true);
    } catch (error) {
      console.error("Error changing password:", error);
      setToastMessage("Failed to change password");
      setToastType("error");
      setShowToast(true);
    }
  };

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
      <div className="space-y-6">
        {/* Header with actions */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              {professor
                ? `${professor.firstName} ${professor.lastName}`
                : user?.email}
            </h1>
            {professor && (
              <p className="text-gray-600 mt-1">
                {formatProfessorTitle(professor.title)}
              </p>
            )}
          </div>
          <div className="flex items-center gap-3 shrink-0">
            {professor && (
              <EditProfessorModal
                professor={professor}
                onProfessorUpdated={(updatedProfessor) => {
                  setProfessor(updatedProfessor);
                  setToastMessage("Profile updated successfully");
                  setToastType("success");
                  setShowToast(true);
                }}
              />
            )}
            <Button
              variant="outline"
              onClick={() => setShowPasswordDialog(true)}
            >
              Change Password
            </Button>
          </div>
        </div>

        {/* Basic Info Card */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Account Information</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center space-x-2">
              <Mail className="h-5 w-5 text-blue-500" />
              <span className="text-gray-600">{user?.email}</span>
            </div>
            <div className="flex items-center space-x-2">
              <ShieldCheck className="h-5 w-5 text-blue-500" />
              <span className="text-gray-600">{user?.role}</span>
            </div>
          </CardContent>
        </Card>

        {professor && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Contact Information Card */}
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Contact Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {professor.phone && (
                  <div className="flex items-center space-x-2">
                    <Phone className="h-5 w-5 text-blue-500" />
                    <span className="text-gray-600">{professor.phone}</span>
                  </div>
                )}
                {professor.officeLocation && (
                  <div className="flex items-center space-x-2">
                    <MapPin className="h-5 w-5 text-blue-500" />
                    <span className="text-gray-600">
                      {professor.officeLocation}
                    </span>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Department Information Card */}
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">
                  Department Information
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center space-x-2">
                  <Building2 className="h-5 w-5 text-blue-500" />
                  <span className="text-gray-600">
                    {department ? department.name : "No department assigned"}
                  </span>
                </div>
                {department?.description && (
                  <p className="text-sm text-gray-500">
                    {department.description}
                  </p>
                )}
              </CardContent>
            </Card>
          </div>
        )}

        {/* Research Interests Section */}
        {professor?.researchInterests &&
          professor.researchInterests.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Research Interests</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex flex-wrap gap-2">
                  {professor.researchInterests.map((interest) => (
                    <span
                      key={interest}
                      className="inline-flex items-center rounded-full bg-blue-50 px-3 py-1 text-sm font-medium text-blue-700"
                    >
                      {interest}
                    </span>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

        {/* Password Change Dialog */}
        <Dialog open={showPasswordDialog} onOpenChange={setShowPasswordDialog}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Change Password</DialogTitle>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div>
                <label className="text-sm font-medium text-gray-500">
                  Current Password
                </label>
                <div className="relative">
                  <Input
                    type={showPasswords.current ? "text" : "password"}
                    value={passwordData.currentPassword}
                    onChange={(e) =>
                      setPasswordData({
                        ...passwordData,
                        currentPassword: e.target.value,
                      })
                    }
                  />
                  <button
                    type="button"
                    onClick={() =>
                      setShowPasswords({
                        ...showPasswords,
                        current: !showPasswords.current,
                      })
                    }
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showPasswords.current ? (
                      <EyeOff className="h-4 w-4" />
                    ) : (
                      <Eye className="h-4 w-4" />
                    )}
                  </button>
                </div>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">
                  New Password
                </label>
                <div className="relative">
                  <Input
                    type={showPasswords.new ? "text" : "password"}
                    value={passwordData.newPassword}
                    onChange={(e) =>
                      setPasswordData({
                        ...passwordData,
                        newPassword: e.target.value,
                      })
                    }
                  />
                  <button
                    type="button"
                    onClick={() =>
                      setShowPasswords({
                        ...showPasswords,
                        new: !showPasswords.new,
                      })
                    }
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showPasswords.new ? (
                      <EyeOff className="h-4 w-4" />
                    ) : (
                      <Eye className="h-4 w-4" />
                    )}
                  </button>
                </div>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">
                  Confirm New Password
                </label>
                <div className="relative">
                  <Input
                    type={showPasswords.confirm ? "text" : "password"}
                    value={passwordData.confirmPassword}
                    onChange={(e) =>
                      setPasswordData({
                        ...passwordData,
                        confirmPassword: e.target.value,
                      })
                    }
                  />
                  <button
                    type="button"
                    onClick={() =>
                      setShowPasswords({
                        ...showPasswords,
                        confirm: !showPasswords.confirm,
                      })
                    }
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showPasswords.confirm ? (
                      <EyeOff className="h-4 w-4" />
                    ) : (
                      <Eye className="h-4 w-4" />
                    )}
                  </button>
                </div>
              </div>
            </div>
            <DialogFooter>
              <Button
                variant="outline"
                onClick={() => setShowPasswordDialog(false)}
              >
                Cancel
              </Button>
              <Button onClick={handleChangePassword}>Change Password</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* Toast Notifications */}
        {showToast && (
          <Toast
            message={toastMessage}
            type={toastType}
            onClose={() => setShowToast(false)}
          />
        )}
      </div>
    </DashboardLayout>
  );
}
