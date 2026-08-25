import { Field, inputClassName, textareaClassName } from "./constant";

export const NavigateFields = ({ formData, onChange }) => (
  <>
    <Field label="Path">
      <input
        name="path"
        value={formData.path}
        onChange={onChange}
        placeholder="/ui/users"
        className={inputClassName}
      />
    </Field>

    <Field label="Query Params JSON">
      <textarea
        name="navigateParams"
        value={formData.navigateParams}
        onChange={onChange}
        placeholder={'{ "id": "$form.userId" }'}
        className={textareaClassName}
      />
    </Field>

    <label className="flex items-center gap-2 text-sm font-medium text-slate-700">
      <input
        type="checkbox"
        name="navigateReplace"
        checked={formData.navigateReplace}
        onChange={onChange}
      />
      Replace current history entry
    </label>
  </>
);
