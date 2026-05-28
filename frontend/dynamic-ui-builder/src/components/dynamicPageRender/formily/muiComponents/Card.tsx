import React from "react";

import MuiCard from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

interface CardProps {
  title?: string;
  description?: string;
  width?: string;
  style?: React.CSSProperties;
  children?: React.ReactNode;
}

export const Card = ({
  title,
  description,
  width = "100%",
  style = {},
  children,
}: CardProps) => {
  return (
    <MuiCard
      sx={{
        width,
        marginBottom: 3,
        ...style,
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