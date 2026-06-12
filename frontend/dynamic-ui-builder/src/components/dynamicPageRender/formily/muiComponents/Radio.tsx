import React, { useEffect, useMemo, useState } from "react";

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

import { resolveApiLookup } from "../../../dataMappingEngine/rule/api.resolver";
import { useComponentProps } from "../utils";

const BaseRadio = (incomingProps: any) => {

  const {
    label,
    options = [],
    value,
    onChange,
    style,
    lookup,
  } = useComponentProps(incomingProps);

  const [runtimeOptions, setRuntimeOptions] = useState(options);

  useEffect(() => {
    let mounted = true;

    const loadOptions = async () => {
      if (!lookup) {
        if (mounted) setRuntimeOptions(options || []);
        return;
      }

      try {
        const response = await resolveApiLookup(lookup);

        if (!mounted) return;

        const labelKey = lookup.labelKey || "displayValue";
        const valueKey = lookup.valueKey || "lookupValue";

        const mapped = Array.isArray(response)
          ? response.map((item: any) => ({
              label: item?.[labelKey] ?? item?.label ?? String(item),
              value: item?.[valueKey] ?? item?.value ?? item?.id ?? item,
            }))
          : [];

        setRuntimeOptions(mapped);
      } catch (error) {
        console.error(error);

        if (mounted) {
          setRuntimeOptions(options || []);
        }
      }
    };

    loadOptions();

    return () => {
      mounted = false;
    };
  }, [lookup, options]);

  const menuItems = useMemo(
    () => runtimeOptions || [],
    [runtimeOptions]
  );

  return (
    <FormControl
      sx={{
        mb: 2,
        ...style,
      }}
    >
      <FormLabel>{label}</FormLabel>

      <RadioGroup
        value={value || ""}
        onChange={(e) => {
          onChange?.(e.target.value);
        }}
      >
        {menuItems.map((item: any) => (
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