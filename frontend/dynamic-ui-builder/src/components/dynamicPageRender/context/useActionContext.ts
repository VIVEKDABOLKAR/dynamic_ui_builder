import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

type ToastType = "success" | "error" | "warning" | "info";

export function useActionContext(formData?: any, form?: any) {
  const navigate = useNavigate();

  const showToast = (message: string, type: ToastType = "success") => {
    if (type === "error") {
      toast.error(message);
    } else if (type === "warning" || type === "info") {
      toast(message);
    } else {
      toast.success(message);
    }
  };

  const setFieldValue = (field: string, value: any) => {
    if (!field) return;

    if (typeof form?.setValuesIn === "function") {
      form.setValuesIn(field, value);
      return;
    }

    form?.setValues?.({ [field]: value });
  };

  const resetForm = async () => {
    if (typeof form?.reset === "function") {
      await form.reset("*");
      return;
    }

    form?.setValues?.({});
  };

  const getFormValue = (field: string) => {
    if (!field) return undefined;

    if (typeof form?.getValuesIn === "function") {
      return form.getValuesIn(field);
    }

    return formData?.[field];
  };

  const emitEvent = (name: string, payload?: any) => {
    if (!name) return;
    window.dispatchEvent(new CustomEvent(name, { detail: payload }));
  };

  return {
    navigate,
    showToast,
    setFieldValue,
    resetForm,
    getFormValue,
    emitEvent,
    formData,
    form,
  };
}
