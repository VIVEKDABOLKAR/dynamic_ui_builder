//this is component registery, formily use it
import { createSchemaField } from '@formily/react'
import { Input } from './muiComponents/Input'
import { CustomButton } from './muiComponents/Button'
import { Heading } from './muiComponents/Heading'
import { Textarea } from './muiComponents/Textarea'
import { Select } from './muiComponents/Select'
import { Checkbox } from './muiComponents/Checkbox'
import { Radio } from './muiComponents/Radio'
import { Card } from './muiComponents/card'
import { Layout } from './muiComponents/Layout'
import { DataTable } from './agGridComponents/DataTable'
import Datepicker from './muiComponents/DatePicker'

export const SchemaField = createSchemaField({
  components: {
    Input,
    CustomButton,
    Heading,
    Textarea,
    Layout,
    Card,
    Select,
    Checkbox,
    Radio,
    DataTable,
    Datepicker,
  },    
  
})  