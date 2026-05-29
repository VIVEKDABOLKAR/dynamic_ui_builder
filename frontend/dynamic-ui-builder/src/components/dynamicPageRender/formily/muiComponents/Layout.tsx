import React from "react";
import Box from "@mui/material/Box";

interface LayoutProps {
  direction?: "row" | "column";
  gap?: number | string;
  justifyContent?: React.CSSProperties["justifyContent"];
  alignItems?: React.CSSProperties["alignItems"];
  wrap?: React.CSSProperties["flexWrap"];
  width?: string | number;
  style?: Record<string, any>;
  children?: React.ReactNode;
}

export const Layout = ({
  direction = "column",
  gap = 2,
  justifyContent,
  alignItems,
  wrap,
  width = "100%",
  style = {},
  children,
}: LayoutProps) => {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: direction,
        gap,
        justifyContent,
        alignItems,
        flexWrap: wrap,
        width,
        ...style,
      }}
    >
      {children}
    </Box>
  );
};
