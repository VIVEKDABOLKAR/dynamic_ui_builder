import { Field, inputClassName, textareaClassName } from "./constant";

export const EmitEventFields = ({ formData, onChange }) => (
  <>
    <Field label="Event Name">
      <input
        name="eventName"
        value={formData.eventName}
        onChange={onChange}
        placeholder="user:selected"
        className={inputClassName}
      />
    </Field>

    <Field label="Payload JSON">
      <textarea
        name="eventPayload"
        value={formData.eventPayload}
        onChange={onChange}
        placeholder={'{ "id": "$form.userId" }'}
        className={textareaClassName}
      />
    </Field>
  </>
);
