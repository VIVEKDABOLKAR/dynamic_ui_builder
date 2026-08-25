import { Field, inputClassName } from "./constant";

export const ApiFields = ({ formData, onChange }) => (
  <>
    <Field label="API URL">
      <input
        name="url"
        value={formData.url}
        onChange={onChange}
        placeholder="/api/users"
        className={inputClassName}
      />
    </Field>

    <Field label="Method">
      <select
        name="method"
        value={formData.method}
        onChange={onChange}
        className={inputClassName}
      >
        <option>GET</option>
        <option>POST</option>
        <option>PUT</option>
        <option>DELETE</option>
      </select>
    </Field>

    <Field label="Params">
      <input
        name="params"
        value={formData.params}
        onChange={onChange}
        placeholder=""
        className={inputClassName}
      />
    </Field>
  </>
);
