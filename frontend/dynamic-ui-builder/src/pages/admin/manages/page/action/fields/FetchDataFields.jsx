import { ApiFields } from "./ApiFields";
import { Field, inputClassName } from "./constant";

// add param field

export const FetchDataFields = ({ formData, onChange }) => (
  <>
    <ApiFields formData={formData} onChange={onChange} />

    <Field label="Response Path">
      <input
        name="responsePath"
        value={formData.responsePath}
        onChange={onChange}
        placeholder="data.items"
        className={inputClassName}
      />
    </Field>

    <Field label="Target Field">
      <input
        name="targetField"
        value={formData.targetField}
        onChange={onChange}
        placeholder="users"
        className={inputClassName}
      />
    </Field>
  </>
);