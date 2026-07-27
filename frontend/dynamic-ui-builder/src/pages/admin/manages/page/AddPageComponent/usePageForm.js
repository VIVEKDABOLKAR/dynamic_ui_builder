import { useCallback, useEffect, useState } from "react";
import {
  createPage,
  getPageByCode,
  updatePage,
} from "../../../../../api/adminPageApi";

const defaultForm = {
  pageName: "",
  pageCode: "",
  description: "",
  version: "1.0",
  status: "DRAFT",

  route: {
    routeCode: "",
    path: "",
    showInMenu: true,
    parentMenu: "",
    menuOrder: 1,
    breadcrumb: true,
    icon: "",
    active: true,
  },

  moduleCode: "",
  categoryCode: "",
  layoutCode: "",



  requireAuthentication: true,
  permissionCode: "",

};

export default function usePageForm(pageCode) {
  const isEdit = Boolean(pageCode);

  const [formData, setFormData] = useState(defaultForm);
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState({});

  /**
   * Load Page
   */
  const loadPage = useCallback(async () => {
    if (!pageCode) {
      return;
    }

    try {
      setLoading(true);

      const page = await getPageByCode(pageCode);

      setFormData({
        pageName: page.pageName || "",
        pageCode: page.pageCode || "",
        description: page.description || "",
        version: page.version || "1.0",
        status: page.status || "DRAFT",

        route: {
          routeCode: page.route?.routeCode || "",
          path: page.route?.path || "",
          showInMenu: page.route?.showInMenu ?? true,
          parentMenu: page.route?.parentMenu || "",
          menuOrder: page.route?.menuOrder || 1,
          breadcrumb: page.route?.breadcrumb ?? true,
          icon: page.route?.icon || "",
          active: page.route?.active ?? true,
        },


        moduleCode: page.moduleCode || "",
        categoryCode: page.categoryCode || "",
        layoutCode: page.layoutCode || "",

        requireAuthentication: page.requireAuthentication ?? true,
        permissionCode: page.permissionCode || "",
      });
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load page.");
    } finally {
      setLoading(false);
    }
  }, [pageCode]);

  useEffect(() => {
    loadPage();
  }, [loadPage]);

  /**
   * Change Handler
   * Also clears the error for a field as soon as the user edits it,
   * so error state doesn't go stale while switching between tabs.
   */
  const handleChange = useCallback((event) => {
    const { name, value, checked, type } = event.target;
    let fieldValue;

    if (type === "checkbox") {
      fieldValue = checked;
    }
    else if (type === "number") {
      fieldValue = Number(value);
    }
    else {
      fieldValue = value;
    }


    setFormData((current) => {
      const updated = { ...current }; //init setFormData
      const keys = name.split(".");

      let target = updated;

      while (keys.length > 1) {
        const key = keys.shift();
        target = target[key] = { ...target[key] };
      }

      target[keys[0]] = fieldValue;

      return updated;
    }, []);

    setErrors((current) => {
      if (!current[name]) return current;
      const next = { ...current };
      delete next[name];
      return next;
    });


  }, []);

  /**
   * Reset
   */
  const reset = () => {
    setFormData(defaultForm);
    setErrors({});
    setMessage("");
  };

  /**
   * Validation
   */
  const validate = () => {
    const validation = {};

    if (!formData.pageName) validation.pageName = "Page Name is required.";

    if (!formData.pageCode) validation.pageCode = "Page Code is required.";

    if (!formData.route) validation.route = "Route is required.";

    if (!formData.moduleCode) validation.moduleCode = "Module is required.";

    setErrors(validation);

    return Object.keys(validation).length === 0;
  };

  /**
   * Save
   * TODO: wire this up to createPage/updatePage once the backend contract
   * for this form is finalized. For now we log the exact payload that
   * would be sent so the flow can be tested end-to-end from the UI.
   */
  const save = async () => {
    if (!validate()) {
      setMessage("Please fix the highlighted fields before saving.");
      return false;
    }

    try {
      setSaving(true);
      setMessage("");

      console.log(
        isEdit ? `Payload for updatePage(${pageCode}):` : "Payload for createPage:",
        formData
      );

      if (isEdit) {
        await updatePage(pageCode, formData);
      } else {
        await createPage(formData);
      }

      setMessage(isEdit ? "Page updated successfully." : "Page created successfully.");

      return true;
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to save page.");
      return false;
    } finally {
      setSaving(false);
    }
  };

  return {
    isEdit,
    loading,
    saving,
    message,
    errors,
    formData,
    setFormData,
    handleChange,
    loadPage,
    save,
    validate,
    reset,
    setMessage,
  };
}