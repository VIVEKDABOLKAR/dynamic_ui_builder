import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";



export function useActionContext(formData?: any) {
  const navigate = useNavigate();

  const showToast = (message: string, type: "success" | "error" = "success") => {
    if (type === "error") {
      toast.error(message);
    } else {
      toast.success(message);
    }
  };

  return {
    navigate,
    showToast,
    formData,
  };
}