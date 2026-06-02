import { useNavigate } from "react-router-dom";

export function useActionContext(formData?: any) {
  const navigate = useNavigate();

  const showToast = (message: string) => {
    console.log("TOAST:", message);
    // replace with Snackbar / toast library if needed
  };

  return {
    navigate,
    showToast,
    formData,
  };
}