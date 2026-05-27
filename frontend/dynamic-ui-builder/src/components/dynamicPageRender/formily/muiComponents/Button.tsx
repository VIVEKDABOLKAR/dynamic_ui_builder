import React from 'react'
import Button from '@mui/material/Button'

export const CustomButton = ({ text }: any) => {
  return (
    <Button variant="contained">
      {text}
    </Button>
  )
}