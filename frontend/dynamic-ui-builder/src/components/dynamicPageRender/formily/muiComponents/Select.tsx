import React, { useEffect, useMemo, useState } from "react";

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

import { resolveApiLookup } from "../../../dataMappingEngine/rule/api.resolver";

const BaseSelect = ({
    label,
    options = [],
    value,
    onChange,
    placeholder,
    style,
    lookup
}: any) => {
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
                console.error("Failed to load select lookup options", error);
                if (mounted) setRuntimeOptions(options || []);
            }
        };

        loadOptions();

        return () => {
            mounted = false;
        };
    }, [lookup, options]);

    const menuItems = useMemo(() => runtimeOptions || [], [runtimeOptions]);

    return (
        <FormControl
            fullWidth
            sx={{
                mb: 2,
                ...style,
            }}
        >
            <InputLabel>
                {label}
            </InputLabel>

            <MuiSelect
                value={value || ""}
                label={label}
                onChange={ (event) => {
                    onChange?.(event.target.value);
                }}
            >

                {menuItems.map((item: any) => (

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
            dataSource: "options",
            value: "value",
        },
    

        (props, field) => {

            return {

                ...props,

                label: field.title
            };
        }
    )
);