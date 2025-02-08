// src/app/auth/login/page.tsx
"use client";

import { Suspense } from "react";
import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/lib/hooks/useAuth";
import Link from "next/link";
import { Eye, EyeOff } from "lucide-react";
import Toast from "@/components/ui/Toast";

function LoginContent() {
  const searchParams = useSearchParams();
  // Your existing component logic...
}

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login } = useAuth();

  useEffect(() => {
    const registered = searchParams.get("registered");
    if (registered === "true") {
      setShowToast(true);
      // Optional: Remove the query parameter
      router.replace("/auth/login");
    }
  }, [searchParams, router]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    try {
      await login(email, password);
      router.push("/dashboard");
    } catch (err) {
      setError("Invalid email or password");
    } finally {
      setIsLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <main className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-b from-gray-50 to-gray-100 px-4">
      {showToast && (
        <Toast
          message="Registration successful! Please log in with your credentials."
          type="success"
          onClose={() => setShowToast(false)}
        />
      )}
      <div className="w-full max-w-md">
        {/* Logo & Title Section */}
        <div className="text-center mb-10">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Welcome Back
          </h1>
          <p className="text-gray-600">Sign in to Academic Management Hub</p>
        </div>

        {/* Form Card */}
        <div className="bg-white rounded-xl shadow-lg p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {error && (
              <div className="p-3 text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg">
                {error}
              </div>
            )}

            {/* Email Field */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Email Address
              </label>
              <input
                id="email"
                type="email"
                required
                className="mt-1 block w-full px-4 py-3 rounded-lg bg-white text-gray-900 text-base transition-colors duration-200 ease-in-out border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            {/* Password Field */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Password
              </label>
              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  required
                  className="mt-1 block w-full px-4 py-3 rounded-lg bg-white text-gray-900 text-base transition-colors duration-200 ease-in-out border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 focus:outline-none"
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5" />
                  ) : (
                    <Eye className="h-5 w-5" />
                  )}
                </button>
              </div>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className={`w-full py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white 
                ${
                  isLoading
                    ? "bg-indigo-400 cursor-not-allowed"
                    : "bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                } transition-colors duration-200`}
            >
              {isLoading ? "Signing in..." : "Sign in"}
            </button>

            {/* Register Link */}
            <div className="text-center text-sm">
              <span className="text-gray-600">Don't have an account?</span>{" "}
              <Link
                href="/auth/register"
                className="font-medium text-indigo-600 hover:text-indigo-500 transition-colors duration-200"
              >
                Sign up
              </Link>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}
