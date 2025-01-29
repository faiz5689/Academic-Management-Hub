// src/components/professors/CreateProfessorModal.tsx
"use client";

import { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { RegistrationRequest, ProfessorTitle } from "@/types/auth";
import { Professor } from "@/types/professor";
import { authService } from "@/lib/api/auth";
import { Plus, Loader2, X, Eye, EyeOff } from "lucide-react";
import Toast from "@/components/ui/Toast";
import { departmentService } from "@/lib/api/department";
import type { Department } from "@/types/department";

interface CreateProfessorModalProps {
  trigger: React.ReactNode;
  onProfessorCreated: (professor: Professor) => void;
}

export function CreateProfessorModal({
  trigger,
  onProfessorCreated,
}: CreateProfessorModalProps) {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");
  const [departments, setDepartments] = useState<Department[]>([]);
  const [showPassword, setShowPassword] = useState(false);
  const [newInterest, setNewInterest] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const [formData, setFormData] = useState<RegistrationRequest>({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    departmentId: "",
    title: "" as ProfessorTitle,
    officeLocation: "",
    phone: "",
    researchInterests: [],
  });

  useEffect(() => {
    const fetchDepartments = async () => {
      if (open) {
        try {
          const data = await departmentService.getAllDepartments();
          setDepartments(data);
        } catch (err) {
          console.error("Error fetching departments:", err);
          setToastMessage("Failed to load departments");
          setToastType("error");
          setShowToast(true);
        }
      }
    };

    fetchDepartments();
  }, [open]);

  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.email) errors.email = "Email is required";
    if (!formData.password) errors.password = "Password is required";
    if (!formData.firstName) errors.firstName = "First name is required";
    if (!formData.lastName) errors.lastName = "Last name is required";
    if (!formData.title) errors.title = "Title is required";
    if (!formData.departmentId) errors.departmentId = "Department is required";

    // Password validation
    if (formData.password) {
      if (formData.password.length < 8) {
        errors.password = "Password must be at least 8 characters";
      } else if (!/[A-Z]/.test(formData.password)) {
        errors.password = "Password must include an uppercase letter";
      } else if (!/[a-z]/.test(formData.password)) {
        errors.password = "Password must include a lowercase letter";
      } else if (!/[0-9]/.test(formData.password)) {
        errors.password = "Password must include a number";
      } else if (!/[!@#$%^&*]/.test(formData.password)) {
        errors.password =
          "Password must include a special character (!@#$%^&*)";
      }
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const addResearchInterest = () => {
    if (
      newInterest.trim() &&
      !formData.researchInterests?.includes(newInterest.trim())
    ) {
      if ((formData.researchInterests?.length || 0) >= 10) {
        setToastMessage("Maximum 10 research interests allowed");
        setToastType("error");
        setShowToast(true);
        return;
      }

      setFormData({
        ...formData,
        researchInterests: [
          ...(formData.researchInterests || []),
          newInterest.trim(),
        ],
      });
      setNewInterest("");
    }
  };

  const removeResearchInterest = (interest: string) => {
    setFormData({
      ...formData,
      researchInterests:
        formData.researchInterests?.filter((i) => i !== interest) || [],
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    setLoading(true);

    try {
      await authService.register(formData);
      setToastMessage("Professor created successfully");
      setToastType("success");
      setOpen(false);

      // Reset form
      setFormData({
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        departmentId: "",
        title: "" as ProfessorTitle,
        officeLocation: "",
        phone: "",
        researchInterests: [],
      });

      // Refresh the professors list
      onProfessorCreated({
        id: "", // This will be replaced when the list refreshes
        userId: "",
        departmentId: formData.departmentId,
        title: formData.title,
        firstName: formData.firstName,
        lastName: formData.lastName,
        officeLocation: formData.officeLocation,
        phone: formData.phone,
        researchInterests: formData.researchInterests,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      });
    } catch (error: any) {
      console.error("Error creating professor:", error);
      setToastMessage(error.message || "Failed to create professor");
      setToastType("error");
    } finally {
      setLoading(false);
      setShowToast(true);
    }
  };

  return (
    <>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogTrigger asChild>{trigger}</DialogTrigger>
        <DialogContent className="sm:max-w-[600px] bg-white">
          <DialogHeader>
            <DialogTitle>Create New Professor</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4 mt-4">
            <div className="grid grid-cols-2 gap-4">
              {/* Title */}
              <div className="space-y-2">
                <label className="text-sm font-medium">
                  Title <span className="text-red-500">*</span>
                </label>
                <Select
                  value={formData.title}
                  onValueChange={(value: ProfessorTitle) =>
                    setFormData((prev) => ({ ...prev, title: value }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select title" />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(ProfessorTitle).map((title) => (
                      <SelectItem key={title} value={title}>
                        {title
                          .replace(/_/g, " ")
                          .toLowerCase()
                          .replace(/\b\w/g, (c) => c.toUpperCase())}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {fieldErrors.title && (
                  <p className="text-sm text-red-600">{fieldErrors.title}</p>
                )}
              </div>

              {/* Department */}
              <div className="space-y-2">
                <label className="text-sm font-medium">
                  Department <span className="text-red-500">*</span>
                </label>
                <Select
                  value={formData.departmentId}
                  onValueChange={(value) =>
                    setFormData((prev) => ({ ...prev, departmentId: value }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select department" />
                  </SelectTrigger>
                  <SelectContent>
                    {departments.map((dept) => (
                      <SelectItem key={dept.id} value={dept.id}>
                        {dept.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {fieldErrors.departmentId && (
                  <p className="text-sm text-red-600">
                    {fieldErrors.departmentId}
                  </p>
                )}
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              {/* First Name */}
              <div className="space-y-2">
                <label className="text-sm font-medium">
                  First Name <span className="text-red-500">*</span>
                </label>
                <Input
                  value={formData.firstName}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      firstName: e.target.value,
                    }))
                  }
                  placeholder="Enter first name"
                />
                {fieldErrors.firstName && (
                  <p className="text-sm text-red-600">
                    {fieldErrors.firstName}
                  </p>
                )}
              </div>

              {/* Last Name */}
              <div className="space-y-2">
                <label className="text-sm font-medium">
                  Last Name <span className="text-red-500">*</span>
                </label>
                <Input
                  value={formData.lastName}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      lastName: e.target.value,
                    }))
                  }
                  placeholder="Enter last name"
                />
                {fieldErrors.lastName && (
                  <p className="text-sm text-red-600">{fieldErrors.lastName}</p>
                )}
              </div>
            </div>

            {/* Email */}
            <div className="space-y-2">
              <label className="text-sm font-medium">
                Email <span className="text-red-500">*</span>
              </label>
              <Input
                type="email"
                value={formData.email}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, email: e.target.value }))
                }
                placeholder="Enter email address"
              />
              {fieldErrors.email && (
                <p className="text-sm text-red-600">{fieldErrors.email}</p>
              )}
            </div>

            {/* Password */}
            <div className="space-y-2">
              <label className="text-sm font-medium">
                Password <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <Input
                  type={showPassword ? "text" : "password"}
                  value={formData.password}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      password: e.target.value,
                    }))
                  }
                  placeholder="Enter password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </button>
              </div>
              {fieldErrors.password && (
                <p className="text-sm text-red-600">{fieldErrors.password}</p>
              )}
              <p className="text-xs text-gray-500">
                Must be at least 8 characters with 1 uppercase, 1 lowercase, 1
                number, and 1 special character
              </p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              {/* Office Location */}
              <div className="space-y-2">
                <label className="text-sm font-medium">Office Location</label>
                <Input
                  value={formData.officeLocation}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      officeLocation: e.target.value,
                    }))
                  }
                  placeholder="Enter office location"
                />
              </div>

              {/* Phone */}
              <div className="space-y-2">
                <label className="text-sm font-medium">Phone</label>
                <Input
                  value={formData.phone}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, phone: e.target.value }))
                  }
                  placeholder="Enter phone number"
                />
              </div>
            </div>

            {/* Research Interests */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Research Interests</label>
              <div className="flex gap-2">
                <Input
                  value={newInterest}
                  onChange={(e) => setNewInterest(e.target.value)}
                  placeholder="Add a research interest"
                  onKeyPress={(e) => {
                    if (e.key === "Enter") {
                      e.preventDefault();
                      addResearchInterest();
                    }
                  }}
                />
                <Button
                  type="button"
                  onClick={addResearchInterest}
                  className="shrink-0"
                >
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
              <div className="flex flex-wrap gap-2 mt-2">
                {formData.researchInterests?.map((interest) => (
                  <span
                    key={interest}
                    className="inline-flex items-center px-2.5 py-1.5 rounded-full text-sm bg-blue-50 text-blue-700"
                  >
                    {interest}
                    <button
                      type="button"
                      onClick={() => removeResearchInterest(interest)}
                      className="ml-1.5 hover:text-blue-900"
                    >
                      <X className="h-3 w-3" />
                    </button>
                  </span>
                ))}
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
                disabled={loading}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={loading}>
                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Create Professor
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {showToast && (
        <Toast
          message={toastMessage}
          type={toastType}
          onClose={() => setShowToast(false)}
        />
      )}
    </>
  );
}
