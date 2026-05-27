//this is component registery, formily use it
import { createSchemaField } from '@formily/react'
import { Input } from './muiComponents/Input'
import { CustomButton } from './muiComponents/Button'
import { Heading } from './muiComponents/Heading'
import { Textarea } from './muiComponents/Textarea'
import { Card } from './muiComponents/Card'
import { Select } from './muiComponents/Select'
import { Checkbox } from './muiComponents/Checkbox'

export const SchemaField = createSchemaField({
  components: {
    Input,
    CustomButton,
    Heading,
    Textarea,
    Card,
    Select,
    Checkbox,
  },    
  
})