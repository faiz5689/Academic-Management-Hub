// src/lib/validation/registration.ts

export interface ValidationError {
  field: string;
  message: string;
}

const PHONE_REGEX = /^\+?[\d\s-()]{10,}$/;
const MAX_RESEARCH_INTEREST_LENGTH = 50;
const MAX_RESEARCH_INTERESTS = 10;

export const validateRegistrationForm = (form: any): ValidationError[] => {
  const errors: ValidationError[] = [];

  // Email validation
  if (!form.email) {
    errors.push({ field: 'email', message: 'Email is required' });
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.push({ field: 'email', message: 'Please enter a valid email address' });
  }

  // Password validation
  if (!form.password) {
    errors.push({ field: 'password', message: 'Password is required' });
  } else if (!form.password.match(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/)) {
    errors.push({
      field: 'password',
      message: 'Password must be at least 8 characters and include numbers, uppercase, lowercase, and special characters'
    });
  }

  // Name validation
  if (!form.firstName?.trim()) {
    errors.push({ field: 'firstName', message: 'First name is required' });
  }
  if (!form.lastName?.trim()) {
    errors.push({ field: 'lastName', message: 'Last name is required' });
  }

  // Department validation
  if (!form.departmentId) {
    errors.push({ field: 'departmentId', message: 'Please select a department' });
  }

  // Title validation
  if (!form.title) {
    errors.push({ field: 'title', message: 'Please select a title' });
  }

  // Phone validation (optional but must be valid if provided)
  if (form.phone && !PHONE_REGEX.test(form.phone)) {
    errors.push({
      field: 'phone',
      message: 'Please enter a valid phone number (minimum 10 digits)'
    });
  }

  // Research interests validation
  if (form.researchInterests?.length > MAX_RESEARCH_INTERESTS) {
    errors.push({
      field: 'researchInterests',
      message: `Maximum ${MAX_RESEARCH_INTERESTS} research interests allowed`
    });
  }

  form.researchInterests?.forEach((interest: string, index: number) => {
    if (interest.length > MAX_RESEARCH_INTEREST_LENGTH) {
      errors.push({
        field: 'researchInterests',
        message: `Research interest "${interest}" exceeds maximum length of ${MAX_RESEARCH_INTEREST_LENGTH} characters`
      });
    }
  });

  return errors;
};