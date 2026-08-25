import { Field, inputClassName } from "./constant";

export const ChainFields = ({ formData, onChange }) => (
  <Field label="Action Names (comma-separated)">
    <input
      name="chainActions"
      value={formData.chainActions}
      onChange={onChange}
      placeholder="saveUser, showSuccess, navigateHome"
      className={inputClassName}
    />
  </Field>
);
