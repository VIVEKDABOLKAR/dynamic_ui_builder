import React from 'react'
import Button from '@mui/material/Button'
import Box from '@mui/material/Box'

export const CustomButton = ({ text, style, variant = "contained" }: any) => {
  console.log("button component callled")
  return (
    <Box sx={{ display: "flex", justifyContent: "flex-start", mt: 1, mb: 2 }}>
      <Button
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