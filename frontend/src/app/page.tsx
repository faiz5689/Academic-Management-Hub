import MainLayout from '@/components/layout/MainLayout';

export default function Home() {
  return (
    <MainLayout>
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900">
              Academic Management Hub
            </h1>
            <p className="mt-3 text-lg text-gray-500">
              A comprehensive platform for managing academic activities
            </p>
            <div className="mt-8 space-y-4">
              <div className="rounded-md shadow">
                <a
                  href="/login"
                  className="w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 md:py-4 md:text-lg md:px-10"
                >
                  Get Started
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}