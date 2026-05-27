import React from "react";

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
  onChange
}: any) => {

  return (

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