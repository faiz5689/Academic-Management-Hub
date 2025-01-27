// src/app/auth/register/page.tsx
'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Eye, EyeOff, Plus, X } from 'lucide-react';
import { ProfessorTitle, RegistrationRequest } from '@/types/auth';
import { Department, categorizeDepartments } from '@/types/department';
import { departmentService } from '@/lib/api/department';

export default function RegisterPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [form, setForm] = useState<RegistrationRequest>({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    departmentId: '',
    title: '' as ProfessorTitle,
    officeLocation: '',
    phone: '',
    researchInterests: []
  });
  const [newInterest, setNewInterest] = useState('');

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const data = await departmentService.getPublicDepartments();
        setDepartments(data);
      } catch (err) {
        console.error('Error fetching departments:', err);
        setError('Failed to load departments');
      }
    };

    fetchDepartments();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const addResearchInterest = () => {
    if (newInterest.trim() && !form.researchInterests?.includes(newInterest.trim())) {
      setForm({
        ...form,
        researchInterests: [...(form.researchInterests || []), newInterest.trim()]
      });
      setNewInterest('');
    }
  };

  const removeResearchInterest = (interest: string) => {
    setForm({
      ...form,
      researchInterests: form.researchInterests?.filter(i => i !== interest) || []
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      // TODO: Implement registration API call
      console.log('Registration form:', form);
      router.push('/auth/login');
    } catch (err) {
      setError('Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 py-8 px-4">
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
                      className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                               focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                               transition-all duration-200"
                      value={form.firstName}
                      onChange={handleInputChange}
                    />
                  </div>

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
                      className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                               focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                               transition-all duration-200"
                      value={form.lastName}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
              </section>

              {/* Account Information Section */}
              <section>
                <h2 className="text-xl font-semibold text-gray-900 mb-6 pb-2 border-b">
                  Account Information
                </h2>
                <div className="space-y-6">
                  <div>
                    <label
                      htmlFor="email"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Email Address  <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      required
                      placeholder="you@example.com"
                      className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                               focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                               transition-all duration-200"
                      value={form.email}
                      onChange={handleInputChange}
                    />
                  </div>

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
                        className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                                 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                                 transition-all duration-200"
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
                        className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                                 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                                 transition-all duration-200"
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
                    </div>

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
                        className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                                 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                                 transition-all duration-200"
                        value={form.departmentId}
                        onChange={handleInputChange}
                      >
                        <option value="">Select Department</option>
                        {categorizeDepartments(departments).map((category) => (
                          <optgroup
                            key={category.name}
                            label={category.name}
                            className="font-medium"
                          >
                            {category.departments.map((dept) => (
                              <option key={dept.id} value={dept.id}>
                                {dept.name}
                              </option>
                            ))}
                          </optgroup>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
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
                        className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                                 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                                 transition-all duration-200"
                        value={form.officeLocation}
                        onChange={handleInputChange}
                      />
                    </div>

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
                        className="w-full px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                                 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                                 transition-all duration-200"
                        value={form.phone}
                        onChange={handleInputChange}
                      />
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
                          className="flex-1 px-4 py-3 rounded-xl bg-gray-50 text-gray-900 border border-gray-200 
                                   focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent 
                                   transition-all duration-200"
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
                                     text-indigo-700 border border-indigo-100 transition-all duration-200"
                          >
                            {interest}
                            <button
                              type="button"
                              onClick={() => removeResearchInterest(interest)}
                              className="ml-2 text-indigo-500 hover:text-indigo-700 focus:outline-none 
                                       transition-colors duration-200"
                            >
                              <X className="h-4 w-4" />
                            </button>
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              </section>
            </div>

            {/* Submit Section */}
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
                {isLoading
                  ? "Creating your account..."
                  : "Complete Registration"}
              </button>

              {/* Login Link */}
              <div className="text-center mt-6">
                <span className="text-gray-600">Already have an account?</span>{" "}
                <Link
                  href="/auth/login"
                  className="font-medium text-indigo-600 hover:text-indigo-500 transition-colors duration-200"
                >
                  Sign in instead
                </Link>
              </div>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}