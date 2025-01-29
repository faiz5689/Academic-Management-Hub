// src/components/departments/HeadProfessorDialog.tsx
import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Professor } from "@/types/professor";
import { professorService } from "@/lib/api/professor";
import { UserPlus, Loader2, Search } from "lucide-react";
import Toast from "@/components/ui/Toast";

interface HeadProfessorDialogProps {
  isOpen: boolean;
  onClose: () => void;
  departmentId: string;
  professorIds: string[];
  onAssign: (professorId: string) => Promise<void>;
}

const HeadProfessorDialog: React.FC<HeadProfessorDialogProps> = ({
  isOpen,
  onClose,
  departmentId,
  professorIds,
  onAssign,
}) => {
  const [professors, setProfessors] = useState<Professor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedProfessor, setSelectedProfessor] = useState<string | null>(
    null
  );
  const [assigning, setAssigning] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");

  useEffect(() => {
    const fetchProfessors = async () => {
      try {
        setLoading(true);
        const data = await professorService.getProfessorsByIds(professorIds);
        setProfessors(data);
      } catch (err) {
        setError("Failed to load professors");
        console.error("Error loading professors:", err);
      } finally {
        setLoading(false);
      }
    };

    if (isOpen && professorIds.length > 0) {
      fetchProfessors();
      setSelectedProfessor(null); // Reset selection when dialog opens
      setSearchQuery(""); // Reset search when dialog opens
    }
  }, [isOpen, professorIds]);

  const filteredProfessors = professors.filter((professor) =>
    `${professor.firstName} ${professor.lastName}`
      .toLowerCase()
      .includes(searchQuery.toLowerCase())
  );

  const handleAssign = async () => {
    if (!selectedProfessor) return;

    try {
      setAssigning(true);
      await onAssign(selectedProfessor);
      setToastMessage("Head professor assigned successfully");
      setToastType("success");
      setShowToast(true);
      onClose();
    } catch (err) {
      setToastMessage("Failed to assign head professor");
      setToastType("error");
      setShowToast(true);
    } finally {
      setAssigning(false);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-40" onClick={onClose} />
      <div className="fixed inset-x-0 top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%] w-full max-w-lg bg-white rounded-lg shadow-lg z-50 p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-semibold">Assign Head Professor</h2>
          <Button
            variant="ghost"
            size="sm"
            onClick={onClose}
            className="hover:bg-gray-100 rounded-full h-8 w-8 p-0"
          >
            ×
          </Button>
        </div>

        {/* Search Input */}
        <div className="relative mb-4">
          <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search professors..."
            className="w-full pl-10 pr-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-blue-500" />
          </div>
        ) : error ? (
          <div className="text-center py-8">
            <p className="text-red-500">{error}</p>
            <Button
              variant="outline"
              className="mt-4"
              onClick={() => {
                setError(null);
                setProfessors([]);
                const fetchProfessors = async () => {
                  try {
                    setLoading(true);
                    const data = await professorService.getProfessorsByIds(
                      professorIds
                    );
                    setProfessors(data);
                  } catch (err) {
                    setError("Failed to load professors");
                    console.error("Error loading professors:", err);
                  } finally {
                    setLoading(false);
                  }
                };
                fetchProfessors();
              }}
            >
              Retry
            </Button>
          </div>
        ) : (
          <div className="max-h-64 overflow-y-auto">
            {filteredProfessors.map((professor) => (
              <div
                key={professor.id}
                className={`p-4 mb-2 rounded-md cursor-pointer transition-colors ${
                  selectedProfessor === professor.id
                    ? "bg-blue-50 border-blue-200"
                    : "hover:bg-gray-50 border-transparent"
                } border`}
                onClick={() => setSelectedProfessor(professor.id)}
              >
                <div className="font-medium">
                  {professor.firstName} {professor.lastName}
                </div>
                {professor.title && (
                  <div className="text-sm text-gray-500">{professor.title}</div>
                )}
              </div>
            ))}

            {filteredProfessors.length === 0 && (
              <div className="text-center py-8 text-gray-500">
                {searchQuery
                  ? "No professors found matching your search"
                  : "No professors available"}
              </div>
            )}
          </div>
        )}

        <div className="flex justify-end space-x-4 mt-6 pt-4 border-t">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button
            onClick={handleAssign}
            disabled={!selectedProfessor || assigning}
          >
            {assigning ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Assigning...
              </>
            ) : (
              <>
                <UserPlus className="mr-2 h-4 w-4" />
                Assign Head
              </>
            )}
          </Button>
        </div>
      </div>

      {showToast && (
        <Toast
          message={toastMessage}
          type={toastType}
          onClose={() => setShowToast(false)}
        />
      )}
    </>
  );
};

export default HeadProfessorDialog;
