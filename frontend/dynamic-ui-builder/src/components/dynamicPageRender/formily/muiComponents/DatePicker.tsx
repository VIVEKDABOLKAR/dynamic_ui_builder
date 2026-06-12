import React from "react";
import { TextField } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { useComponentProps } from "../utils";

export default function Datepicker(incomingProps: any) {

    const {
        value,
        onChange,
        label,
        disabled
    } = useComponentProps(incomingProps);

    return (
        <DatePicker
            label={label}
            onChange={onChange}
            disabled={disabled}
            slotProps={{
                textField: {
                    fullWidth: true,
                    size: "small",
                },
            }}
        />
    );
}