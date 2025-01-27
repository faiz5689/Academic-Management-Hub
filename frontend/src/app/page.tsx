'use client';
// src/app/page.tsx
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/hooks/useAuth';

export default function Home() {
  const router = useRouter();
  const { user, isLoading } = useAuth();

  useEffect(() => {
    if (!isLoading) {
      router.push(user ? '/dashboard' : '/auth/login');
    }
  }, [user, isLoading, router]);

  return null;
}