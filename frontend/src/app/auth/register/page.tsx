// src/app/auth/register/page.tsx
"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";
import { Eye, EyeOff, Plus, X } from "lucide-react";
import { ProfessorTitle, RegistrationRequest } from "@/types/auth";
import { Department, categorizeDepartments } from "@/types/department";
import { departmentService } from "@/lib/api/department";
import { authService } from "@/lib/api/auth";
import {
  validateRegistrationForm,
  ValidationError,
} from "@/lib/validation/registration";
import Toast from "@/components/ui/Toast";

export default function RegisterPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");
  const [showPassword, setShowPassword] = useState(false);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [form, setForm] = useState<RegistrationRequest>({
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
  const [newInterest, setNewInterest] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const data = await departmentService.getPublicDepartments();
        setDepartments(data);
      } catch (err) {
        console.error("Error fetching departments:", err);
        setError("Failed to load departments");
      }
    };

    fetchDepartments();
  }, []);

  useEffect(() => {
    // Show success message if redirected from registration
    const registered = searchParams.get("registered");
    if (registered === "true") {
      setToastMessage("Registration successful! Please login.");
      setToastType("success");
      setShowToast(true);
    }
  }, [searchParams]);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));

    // Clear error for the changed field
    if (fieldErrors[name]) {
      setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    }
    if (error) {
      setError("");
    }
  };

  const addResearchInterest = () => {
    if (
      newInterest.trim() &&
      !form.researchInterests?.includes(newInterest.trim())
    ) {
      if ((form.researchInterests?.length || 0) >= 10) {
        setToastMessage("Maximum 10 research interests allowed");
        setToastType("error");
        setShowToast(true);
        return;
      }

      if (newInterest.length > 50) {
        setToastMessage("Research interest must be less than 50 characters");
        setToastType("error");
        setShowToast(true);
        return;
      }

      setForm({
        ...form,
        researchInterests: [
          ...(form.researchInterests || []),
          newInterest.trim(),
        ],
      });
      setNewInterest("");
    }
  };

  const removeResearchInterest = (interest: string) => {
    setForm({
      ...form,
      researchInterests:
        form.researchInterests?.filter((i) => i !== interest) || [],
    });
  };

  const validateForm = () => {
    const errors = validateRegistrationForm(form);

    if (errors.length > 0) {
      const errorMap = errors.reduce(
        (acc, error) => ({
          ...acc,
          [error.field]: error.message,
        }),
        {}
      );

      setFieldErrors(errorMap);
      setError(errors[0].message); // Show first error in the general error message
      return false;
    }

    setFieldErrors({});
    setError("");
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate form
    const validationErrors = validateRegistrationForm(form);
    if (validationErrors.length > 0) {
      const errorMap = validationErrors.reduce(
        (acc, error) => ({
          ...acc,
          [error.field]: error.message,
        }),
        {}
      );

      setFieldErrors(errorMap);
      setError(validationErrors[0].message);
      return;
    }

    setIsLoading(true);
    setError("");
    setFieldErrors({});

    try {
      await authService.register(form);
      router.push("/auth/login?registered=true");
    } catch (err: any) {
      console.error("Registration error:", err);

      if (err.response?.data?.fieldErrors) {
        setFieldErrors(err.response.data.fieldErrors);
      }

      setToastMessage(
        err.response?.data?.message || "Registration failed. Please try again."
      );
      setToastType("error");
      setShowToast(true);
    } finally {
      setIsLoading(false);
    }
  };

  const getInputClassName = (fieldName: string) => `
    w-full px-4 py-3 rounded-xl 
    ${
      fieldErrors[fieldName]
        ? "bg-red-50 border-red-300"
        : "bg-gray-50 border-gray-200"
    }
    text-gray-900 border 
    focus:outline-none focus:ring-2 
    ${fieldErrors[fieldName] ? "focus:ring-red-500" : "focus:ring-indigo-500"}
    focus:border-transparent transition-all duration-200
  `;

  return (
    <main className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 py-8 px-4">
      {showToast && (
        <Toast
          message={toastMessage}
          type={toastType}
          onClose={() => setShowToast(false)}
        />
      )}
      <div className="max-w-4xl mx-auto">
        {/* Header Section */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Join Our Academic Community
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Begin your journey as a Professor at Academic Management Hub
          </p>
        </div>

        {/* Main Form Card */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8 md:p-10">
          <form onSubmit={handleSubmit} className="space-y-8">
            {error && (
              <div className="p-4 text-sm text-red-600 bg-red-50 border border-red-100 rounded-xl">
                {error}
              </div>
            )}

            {/* Form Sections */}
            <div className="space-y-10">
              {/* Personal Information Section */}
              <section>
                <h2 className="text-xl font-semibold text-gray-900 mb-6 pb-2 border-b">
                  Personal Information
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* First Name */}
                  <div>
                    <label
                      htmlFor="firstName"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      First Name <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      id="firstName"
                      name="firstName"
                      required
                      placeholder="Enter your first name"
                      className={getInputClassName("firstName")}
                      value={form.firstName}
                      onChange={handleInputChange}
                    />
                    {fieldErrors.firstName && (
                      <p className="mt-1 text-sm text-red-600">
                        {fieldErrors.firstName}
                      </p>
                    )}
                  </div>

                  {/* Last Name */}
                  <div>
                    <label
                      htmlFor="lastName"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Last Name <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      id="lastName"
                      name="lastName"
                      required
                      placeholder="Enter your last name"
                      className={getInputClassName("lastName")}
                      value={form.lastName}
                      onChange={handleInputChange}
                    />
                    {fieldErrors.lastName && (
                      <p className="mt-1 text-sm text-red-600">
                        {fieldErrors.lastName}
                      </p>
                    )}
                  </div>
                </div>
              </section>

              {/* Account Information Section */}
              <section>
                <h2 className="text-xl font-semibold text-gray-900 mb-6 pb-2 border-b">
                  Account Information
                </h2>
                <div className="space-y-6">
                  {/* Email */}
                  <div>
                    <label
                      htmlFor="email"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Email Address <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      required
                      placeholder="you@example.com"
                      className={getInputClassName("email")}
                      value={form.email}
                      onChange={handleInputChange}
                    />
                    {fieldErrors.email && (
                      <p className="mt-1 text-sm text-red-600">
                        {fieldErrors.email}
                      </p>
                    )}
                  </div>

                  {/* Password */}
                  <div>
                    <label
                      htmlFor="password"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Password <span className="text-red-500">*</span>
                    </label>
                    <div className="relative">
                      <input
                        type={showPassword ? "text" : "password"}
                        id="password"
                        name="password"
                        required
                        placeholder="Create a strong password"
                        className={getInputClassName("password")}
                        value={form.password}
                        onChange={handleInputChange}
                      />
                      <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 
                   focus:outline-none transition-colors duration-200"
                      >
                        {showPassword ? (
                          <EyeOff className="h-5 w-5" />
                        ) : (
                          <Eye className="h-5 w-5" />
                        )}
                      </button>
                    </div>
                    {fieldErrors.password && (
                      <p className="mt-1 text-sm text-red-600">
                        {fieldErrors.password}
                      </p>
                    )}
                    <p className="mt-2 text-sm text-gray-500">
                      Must be at least 8 characters with 1 uppercase, 1
                      lowercase, 1 number, and 1 special character
                    </p>
                  </div>
                </div>
              </section>

              {/* Professional Information Section */}
              <section>
                <h2 className="text-xl font-semibold text-gray-900 mb-6 pb-2 border-b">
                  Professional Information
                </h2>
                <div className="space-y-6">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Academic Title */}
                    <div>
                      <label
                        htmlFor="title"
                        className="block text-sm font-medium text-gray-700 mb-2"
                      >
                        Academic Title <span className="text-red-500">*</span>
                      </label>
                      <select
                        id="title"
                        name="title"
                        required
                        className={getInputClassName("title")}
                        value={form.title}
                        onChange={handleInputChange}
                      >
                        <option value="">Select Title</option>
                        {Object.entries(ProfessorTitle).map(([key, value]) => (
                          <option key={key} value={value}>
                            {value
                              .replace(/_/g, " ")
                              .replace(/\b\w/g, (l) => l.toUpperCase())}
                          </option>
                        ))}
                      </select>
                      {fieldErrors.title && (
                        <p className="mt-1 text-sm text-red-600">
                          {fieldErrors.title}
                        </p>
                      )}
                    </div>

                    {/* Department */}
                    <div>
                      <label
                        htmlFor="departmentId"
                        className="block text-sm font-medium text-gray-700 mb-2"
                      >
                        Department <span className="text-red-500">*</span>
                      </label>
                      <select
                        id="departmentId"
                        name="departmentId"
                        required
                        className={getInputClassName("departmentId")}
                        value={form.departmentId}
                        onChange={handleInputChange}
                      >
                        <option value="">Select Department</option>
                        {categorizeDepartments(departments).map((category) => (
                          <optgroup key={category.name} label={category.name}>
                            {category.departments.map((dept) => (
                              <option key={dept.id} value={dept.id}>
                                {dept.name}
                              </option>
                            ))}
                          </optgroup>
                        ))}
                      </select>
                      {fieldErrors.departmentId && (
                        <p className="mt-1 text-sm text-red-600">
                          {fieldErrors.departmentId}
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Office Location */}
                    <div>
                      <label
                        htmlFor="officeLocation"
                        className="block text-sm font-medium text-gray-700 mb-2"
                      >
                        Office Location
                      </label>
                      <input
                        type="text"
                        id="officeLocation"
                        name="officeLocation"
                        placeholder="Building and room number"
                        className={getInputClassName("officeLocation")}
                        value={form.officeLocation}
                        onChange={handleInputChange}
                      />
                      {fieldErrors.officeLocation && (
                        <p className="mt-1 text-sm text-red-600">
                          {fieldErrors.officeLocation}
                        </p>
                      )}
                    </div>

                    {/* Phone Number */}
                    <div>
                      <label
                        htmlFor="phone"
                        className="block text-sm font-medium text-gray-700 mb-2"
                      >
                        Phone Number
                      </label>
                      <input
                        type="tel"
                        id="phone"
                        name="phone"
                        placeholder="Your contact number"
                        className={getInputClassName("phone")}
                        value={form.phone}
                        onChange={handleInputChange}
                      />
                      {fieldErrors.phone && (
                        <p className="mt-1 text-sm text-red-600">
                          {fieldErrors.phone}
                        </p>
                      )}
                    </div>
                  </div>

                  {/* Research Interests */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Research Interests
                    </label>
                    <div className="space-y-4">
                      <div className="flex gap-2">
                        <input
                          type="text"
                          value={newInterest}
                          onChange={(e) => setNewInterest(e.target.value)}
                          placeholder="Add your research interests"
                          className={`flex-1 ${getInputClassName(
                            "newInterest"
                          )}`}
                          onKeyPress={(e) => {
                            if (e.key === "Enter") {
                              e.preventDefault();
                              addResearchInterest();
                            }
                          }}
                        />
                        <button
                          type="button"
                          onClick={addResearchInterest}
                          className="px-4 py-3 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 
                     focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 
                     transition-all duration-200"
                        >
                          <Plus className="h-5 w-5" />
                        </button>
                      </div>

                      <div className="flex flex-wrap gap-2">
                        {form.researchInterests?.map((interest) => (
                          <span
                            key={interest}
                            className="inline-flex items-center px-3 py-1.5 rounded-lg text-sm bg-indigo-50 
                text-indigo-700 border border-indigo-100"
                          >
                            {interest}
                            <button
                              type="button"
                              onClick={() => removeResearchInterest(interest)}
                              className="ml-2 text-indigo-500 hover:text-indigo-700 focus:outline-none"
                            >
                              <X className="h-4 w-4" />
                            </button>
                          </span>
                        )) || null}
                      </div>
                      {fieldErrors.researchInterests && (
                        <p className="mt-1 text-sm text-red-600">
                          {fieldErrors.researchInterests}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              </section>

              {/* Submit Button */}
              <div className="pt-6">
                <button
                  type="submit"
                  disabled={isLoading}
                  className={`w-full py-4 px-6 rounded-xl text-base font-medium text-white
      ${
        isLoading
          ? "bg-indigo-400 cursor-not-allowed"
          : "bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
      } transition-all duration-200 shadow-lg hover:shadow-xl`}
                >
                  {isLoading ? "Creating account..." : "Complete Registration"}
                </button>

                {/* Login Link */}
                <div className="text-center mt-6">
                  <div className="relative">
                    <div className="absolute inset-0 flex items-center">
                      <div className="w-full border-t border-gray-200"></div>
                    </div>
                    <div className="relative flex justify-center text-sm">
                      <span className="px-2 bg-white text-gray-500">or</span>
                    </div>
                  </div>

                  <p className="mt-4">
                    <span className="text-gray-600">
                      Already have an account?{" "}
                    </span>
                    <Link
                      href="/auth/login"
                      className="font-medium text-indigo-600 hover:text-indigo-500 transition-colors duration-200"
                    >
                      Sign in instead
                    </Link>
                  </p>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}
