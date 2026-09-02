import { Field, inputClassName } from "./constant";

export const ToastFields = ({ formData, onChange }) => (
  <>
    <Field label="Message">
      <input
        name="toastMessage"
        value={formData.toastMessage}
        onChange={onChange}
        className={inputClassName}
      />
    </Field>

    <Field label="Type">
      <select
        name="toastType"
        value={formData.toastType}
        onChange={onChange}
        className={inputClassName}
      >
        <option value="success">Success</option>
        <option value="error">Error</option>
        <option value="warning">Warning</option>
        <option value="info">Info</option>
      </select>
    </Field>
  </>
);