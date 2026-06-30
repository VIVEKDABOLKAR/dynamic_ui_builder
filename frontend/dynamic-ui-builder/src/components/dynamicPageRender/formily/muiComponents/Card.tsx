import React from "react";

import MuiCard from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { useComponentProps } from "../utils";

interface CardProps {
  title?: string;
  description?: string;
  width?: string;
  prop?: string;
  children?: React.ReactNode;
}

export const Card = (incomingProps: any) => {
  const {
    title,
    description,
    width = "100%",
    style,
    children,
  } = useComponentProps(incomingProps);


  console.log("Style Of Card :- ", style)//here style is !bg-red-500
  return (
    <MuiCard
    className={style}
      sx={{
        width,
        marginBottom: 3,
      }}
    >
      <CardContent>
        {title && (
          <Typography
            variant="h5"
            gutterBottom
          >
            {title}
          </Typography>
        )}

        {description && (
          <Typography
            variant="body2"
            sx={{ mb: 2 }}
          >
            {description}
          </Typography>
        )}

        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          {children}
        </Box>
      </CardContent>
    </MuiCard>
  );
};