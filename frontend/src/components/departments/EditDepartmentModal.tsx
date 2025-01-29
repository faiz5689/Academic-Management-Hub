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
import { Textarea } from "@/components/ui/textarea";
import { Department } from "@/types/department";
import { departmentService } from "@/lib/api/department";
import { Loader2, Edit } from "lucide-react";
import Toast from "@/components/ui/Toast";

interface EditDepartmentModalProps {
  department: Department;
  trigger?: React.ReactNode;
  onDepartmentUpdated: (updatedDepartment: Department) => void;
}

export function EditDepartmentModal({
  department,
  trigger,
  onDepartmentUpdated,
}: EditDepartmentModalProps) {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<"success" | "error">("success");
  const [formData, setFormData] = useState({
    name: department.name || "",
    description: department.description || "",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const updatedDepartment = await departmentService.updateDepartment(
        department.id,
        {
          name: formData.name,
          description: formData.description,
        }
      );
      onDepartmentUpdated(updatedDepartment);
      setToastMessage("Department updated successfully");
      setToastType("success");
      setOpen(false);
    } catch (error) {
      console.error("Error updating department:", error);
      setToastMessage("Failed to update department");
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
              <span>Edit Department</span>
            </Button>
          )}
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px] bg-white border shadow-lg">
          <DialogHeader>
            <DialogTitle>Edit Department</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium">Name</label>
              <Input
                value={formData.name}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, name: e.target.value }))
                }
                placeholder="Department name"
                required
                className="w-full"
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Description</label>
              <Textarea
                value={formData.description}
                onChange={(e) =>
                  setFormData((prev) => ({
                    ...prev,
                    description: e.target.value,
                  }))
                }
                placeholder="Department description"
                required
                className="w-full min-h-[100px]"
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
