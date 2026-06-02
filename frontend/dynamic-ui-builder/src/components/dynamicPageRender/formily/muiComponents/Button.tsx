import React from 'react'
import Button from '@mui/material/Button'
import Box from '@mui/material/Box'
import axios from 'axios'
import { useForm } from '@formily/react'
import { useContext } from 'react'
import { PageSchemaContext } from '../../context/PageSchemaContext'
import { buildEntityPayload } from '../../../dataMappingEngine/utils/buildEntityPayload'
import { useNavigate } from 'react-router'
import { resolveComponentActions } from '../../actionRenders/ActionDispacther'
import { useActionContext } from '../../context/useActionContext'

export const CustomButton = ({ text, style, variant = "contained", action = [] }: any) => {
  const form = useForm();
  const pageSchema = useContext(PageSchemaContext);
  const ctx = useActionContext(form?.values);

  const handler = resolveComponentActions(action, pageSchema?.["x-actions"],
    ctx
  )
  console.log(handler);

  return (
    <Box sx={{ display: "flex", justifyContent: "flex-start", mt: 1, mb: 2 }}>
      <Button
        {...handler} // event handler
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