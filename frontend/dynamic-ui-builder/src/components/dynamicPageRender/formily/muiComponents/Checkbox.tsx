import React from "react";
import { Box } from "@mui/material";

import {
  Checkbox as MuiCheckbox,
  FormControlLabel
} from "@mui/material";

import {
  connect,
  mapProps
} from "@formily/react";

const BaseCheckbox = ({
  label,
  checked,
  onChange,
  style
}: any) => {

  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        mb: 2,
        ...style,
      }}
    >
      <FormControlLabel
        control={
          <MuiCheckbox
            checked={!!checked}
            onChange={(e) => {
              onChange?.(e.target.checked);
            }}
          />
        }
        label={label}
      />
    </Box>
  );
};

export const Checkbox = connect(
  BaseCheckbox,

  mapProps(
    {
      value: "checked"
    },

    (props, field) => {

      return {

        ...props,

        label: field.title
      };
    }
  )
);