import { ACTION_FIELDS_REGISTERY } from "./constants";

export const ActionFields = ({ actionType, formData, onChange }) => {
  const FieldsComponent = ACTION_FIELDS_REGISTERY[actionType];

  if (!FieldsComponent) {
    return null;
  }

  return (
    <FieldsComponent
      formData={formData}
      onChange={onChange}
    />
  );
};
