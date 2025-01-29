"use client";

import { useState } from "react";
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
import { Professor, ProfessorTitle } from "@/types/professor";
import { professorService } from "@/lib/api/professor";
import { Edit, Loader2 } from "lucide-react";
import Toast from "@/components/ui/Toast";

interface EditProfessorModalProps {
  professor: Professor;
  trigger?: React.ReactNode;
  onProfessorUpdated: (updatedProfessor: Professor) => void;
}

export function EditProfessorModal({
  professor,
  trigger,
  onProfessorUpdated,
}: EditProfessorModalProps) {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");
  const [formData, setFormData] = useState<{
    title: ProfessorTitle;
    firstName: string;
    lastName: string;
    officeLocation: string;
    phone: string;
    researchInterests: string;
  }>({
    title: professor.title,
    firstName: professor.firstName,
    lastName: professor.lastName,
    officeLocation: professor.officeLocation || "",
    phone: professor.phone || "",
    researchInterests: professor.researchInterests?.join(", ") || "",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Convert research interests string back to array
      const researchInterests = formData.researchInterests
        ? formData.researchInterests
            .split(",")
            .map((interest) => interest.trim())
        : [];

      const updatedProfessor = await professorService.updateProfessor(
        professor.id,
        {
          title: formData.title,
          firstName: formData.firstName,
          lastName: formData.lastName,
          officeLocation: formData.officeLocation,
          phone: formData.phone,
          researchInterests,
        }
      );
      onProfessorUpdated(updatedProfessor);
      setToastMessage("Professor updated successfully");
      setToastType("success");
      setOpen(false);
    } catch (error) {
      console.error("Error updating professor:", error);
      setToastMessage("Failed to update professor");
      setToastType("error");
    } finally {
      setLoading(false);
      setShowToast(true);
    }
  };

  return (
    <>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogTrigger asChild>
          {trigger || (
            <Button variant="outline" className="flex items-center gap-2">
              <Edit className="h-4 w-4" />
              <span>Edit Professor</span>
            </Button>
          )}
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px] bg-white border shadow-lg">
          <DialogHeader>
            <DialogTitle>Edit Professor</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium">Title</label>
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
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">First Name</label>
              <Input
                value={formData.firstName}
                onChange={(e) =>
                  setFormData((prev) => ({
                    ...prev,
                    firstName: e.target.value,
                  }))
                }
                placeholder="First name"
                required
                className="w-full"
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Last Name</label>
              <Input
                value={formData.lastName}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, lastName: e.target.value }))
                }
                placeholder="Last name"
                required
                className="w-full"
              />
            </div>
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
                placeholder="Office location"
                className="w-full"
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Phone</label>
              <Input
                value={formData.phone}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, phone: e.target.value }))
                }
                placeholder="Phone number"
                className="w-full"
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">
                Research Interests (comma-separated)
              </label>
              <Input
                value={formData.researchInterests}
                onChange={(e) =>
                  setFormData((prev) => ({
                    ...prev,
                    researchInterests: e.target.value,
                  }))
                }
                placeholder="AI, Machine Learning, Data Science"
                className="w-full"
              />
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
                Save Changes
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
