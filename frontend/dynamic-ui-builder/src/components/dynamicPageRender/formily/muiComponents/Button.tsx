import React from 'react'
import Button from '@mui/material/Button'
import Box from '@mui/material/Box'
import axios from 'axios'
import { useForm } from '@formily/react'

export const CustomButton = ({ text, style, variant = "contained", action = [] }: any) => {
  const form = useForm();

  const resolveUrl = (url: string) => {
    if (!url) return "";
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }
    if (url.startsWith("/api")) {
      return `http://localhost:8080${url}`;
    }
    return url;
  };

  const handleClick = async () => {
    const clickActions = (action || []).filter((a: any) => a?.event === "onClick");

    for (const act of clickActions) {
      if (act?.type === "SUBMIT_FORM") {
        const values = form?.values || {};
        const api = act?.api || {};
        const method = (api.method || "POST").toUpperCase();

        try {
          await axios({
            method,
            url: resolveUrl(api.url),
            headers: api.headers || {},
            data: values,
          });
        } catch (error) {
          console.error("Button action SUBMIT_FORM failed", error);
        }
      }
    }
  };

  return (
    <Box sx={{ display: "flex", justifyContent: "flex-start", mt: 1, mb: 2 }}>
      <Button
        onClick={handleClick}
        variant={variant}
        sx={{
          textTransform: "none",
          px: 2,
          py: 1,
          borderRadius: 2,
          ...style,
        }}
      >
        {text}
      </Button>
    </Box>
  )
}