import React from "react";

import TextField from "@mui/material/TextField";

import {
  connect,
  mapProps
} from "@formily/react";

const MuiTextarea = ({
  value,
  onInput,
  label,
  required,
  height = 10,
  width = "100%",
  ...props
}: any) => {
  return (
    <TextField
      multiline
      rows={4}
      value={value || ""}
      label={label}
      required={required}
      onChange={(e) => onInput(e.target.value)}
      sx={{
        width,
      }}
      {...props}
    />
  );
};

export const Textarea = connect(
  MuiTextarea,
  mapProps({
    value: "value",
  })
);