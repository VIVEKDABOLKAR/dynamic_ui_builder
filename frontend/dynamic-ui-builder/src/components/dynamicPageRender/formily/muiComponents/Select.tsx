import React from "react";

import {
  FormControl,
  InputLabel,
  MenuItem,
  Select as MuiSelect
} from "@mui/material";

import {
  connect,
  mapProps
} from "@formily/react";

const BaseSelect = ({
  label,
  options = [],
  value,
  onChange,
  placeholder,
  style
}: any) => {

  return (
    <FormControl
      fullWidth
      sx={style}
    >
      <InputLabel>
        {label}
      </InputLabel>

      <MuiSelect
        value={value || ""}
        label={label}
        onChange={onChange}
      >

        {options.map((item: any) => (

          <MenuItem
            key={item.value}
            value={item.value}
          >
            {item.label}
          </MenuItem>

        ))}

      </MuiSelect>

    </FormControl>
  );
};

export const Select = connect(
  BaseSelect,

  mapProps(
    {
      dataSource: "options"
    },

    (props, field) => {

      return {

        ...props,

        label: field.title
      };
    }
  )
);