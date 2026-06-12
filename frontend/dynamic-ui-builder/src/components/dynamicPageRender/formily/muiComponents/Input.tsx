import React from "react";

import TextField from "@mui/material/TextField";

import {
  connect,
  mapProps,
  useField,
  useFieldSchema
} from "@formily/react";
import { useComponentProps } from "../utils";

const MuiInput = (incomingProps: any) => {

  const {
    value,
    onInput,
    label,
    required,
    width = "100%",
    style,
    ...props
  } = useComponentProps(incomingProps);

  const field = useField();
  const fieldSchema = useFieldSchema();

  return (
    <TextField
      fullWidth
      value={value || ""}
      label={label}
      required={required}
      onChange={(e) => onInput?.(e.target.value)}
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