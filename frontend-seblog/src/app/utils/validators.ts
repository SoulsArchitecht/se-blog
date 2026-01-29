import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumeric = /\d/.test(value);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    const errors: ValidationErrors = {};
    if (!hasUpperCase) errors['missingUpperCase'] = true;
    if (!hasLowerCase) errors['missingLowerCase'] = true;
    if (!hasNumeric) errors['missingNumber'] = true;
    if (!hasSpecial) errors['missingSpecial'] = true;

    return Object.keys(errors).length > 0 ? { passwordStrength: true } : null;
  };
}

export function confirmPasswordValidator(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirmPassword = group.get('confirmPassword')?.value;

  return password === confirmPassword ? null : { passwordMismatch: true };
}