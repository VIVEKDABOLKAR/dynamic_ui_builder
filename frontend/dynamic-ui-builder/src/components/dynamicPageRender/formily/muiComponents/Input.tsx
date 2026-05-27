import React from 'react'
import TextField from '@mui/material/TextField'
import { connect, mapProps } from '@formily/react'

const MuiInput = (props: any) => {
  return <TextField fullWidth {...props} />
}

export const Input = connect(
  MuiInput,
  mapProps({
    value: 'value',
    title: 'label',
  })
)