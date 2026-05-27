import React from "react";
import Typography from "@mui/material/Typography";

interface HeadingProps {
  text: string;
  variant?: any;
  align?: "left" | "center" | "right";
}

export const Heading = ({
  text,
  variant = "h4",
  align = "left",
}: HeadingProps) => {
  return (
    <Typography
      variant={variant}
      align={align}
      gutterBottom
    >
      {text}
    </Typography>
  );
};