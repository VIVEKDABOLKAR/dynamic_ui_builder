import React from "react";

import {
  Radio as MuiRadio,
  RadioGroup,
  FormControlLabel,
  FormControl,
  FormLabel
} from "@mui/material";

import {
  connect,
  mapProps
} from "@formily/react";

const BaseRadio = ({
  label,
  options = [],
  value,
  onChange,
  style
}: any) => {

  return (

    <FormControl
      sx={{
        mb: 2,
        ...style,
      }}
    >

      <FormLabel>
        {label}
      </FormLabel>

      <RadioGroup
        value={value || ""}
        onChange={(e) => {
          onChange?.(e.target.value);
        }}
      >

        {options.map((item: any) => (

          <FormControlLabel
            key={item.value}
            value={item.value}
            control={<MuiRadio />}
            label={item.label}
          />

        ))}

      </RadioGroup>

    </FormControl>
  );
};

export const Radio = connect(
  BaseRadio,

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