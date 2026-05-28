import React from "react";

import TextField from "@mui/material/TextField";

import {
  connect,
  mapProps,
  useField,
  useFieldSchema
} from "@formily/react";

const MuiInput = ({
  value,
  onInput,
  label,
  required,
  width = "100%",
  style,
  ...props
}: any) => {
  const field = useField();
  const fieldSchema = useFieldSchema();
  
  const mapping = fieldSchema["x-mapping"];

  return (
    <TextField
      fullWidth
      value={value || ""}
      label={label}
      required={required}
      onChange={(e) => onInput(e.target.value)}
      sx={{
        width,
        mb: 2,
        ...style,
      }}
      {...props}
    />
  );
};

export const Input = connect(
  MuiInput,
  mapProps({
    value: "value",
    title: "label",
  }),
  
);