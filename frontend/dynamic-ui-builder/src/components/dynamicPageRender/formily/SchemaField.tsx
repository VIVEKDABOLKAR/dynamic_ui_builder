//this is component registery, formily use it
import { createSchemaField } from '@formily/react'
import { Input } from './muiComponents/Input'
import { CustomButton } from './muiComponents/Button'

export const SchemaField = createSchemaField({
  components: {
    Input,
    CustomButton,
  },    
  
})