import FormControl from '@mui/material/FormControl'
import FormHelperText from '@mui/material/FormHelperText'
import InputLabel from '@mui/material/InputLabel'

export const FormItem = (props: any) => {
  return (
    <FormControl fullWidth margin="normal">
      {props.label && (
        <InputLabel shrink>
          {props.label}
        </InputLabel>
      )}

      {props.children}

      {props.helperText && (
        <FormHelperText>
          {props.helperText}
        </FormHelperText>
      )}
    </FormControl>
  )
}