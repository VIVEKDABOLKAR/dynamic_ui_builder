import React from 'react'
import Button from '@mui/material/Button'

export const CustomButton = ({ text }: any) => {
  console.log("button component callled")
  return (
    <Button variant="contained">
      {text}
    </Button>
  )
}