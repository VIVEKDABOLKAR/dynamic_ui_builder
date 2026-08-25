import { Field, inputClassName } from "./constant";

export const SetFieldValueFields = ({ formData, onChange }) => (
  <>
    <Field label="Field Name">
      <input
        name="fieldName"
        value={formData.fieldName}
        onChange={onChange}
        className={inputClassName}
      />
    </Field>

    <Field label="Field Value">
      <input
        name="fieldValue"
        value={formData.fieldValue}
        onChange={onChange}
        className={inputClassName}
      />
    </Field>
  </>
);