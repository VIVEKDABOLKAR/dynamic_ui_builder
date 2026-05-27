import React from "react";
import Typography from "@mui/material/Typography";

interface HeadingProps {
  text: string;
  variant?: any;
  align?: "left" | "center" | "right";
  style?: Record<string, any>;
}

export const Heading = ({
  text,
  variant = "h4",
  align = "left",
  style,
}: HeadingProps) => {
  return (
    <Typography
      variant={variant}
      align={align}
      gutterBottom
      sx={{
        mb: 2,
        fontWeight: 600,
        ...style,
      }}
    >
      {text}
    </Typography>
  );
};