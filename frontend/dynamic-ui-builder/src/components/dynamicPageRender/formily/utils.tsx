import { useForm } from "@formily/react";
import { useContext, useMemo } from "react";
import { useActionContext } from "../context/useActionContext";
import { usePageSchema } from "../context/PageSchemaContext";
import { resolveComponentActions } from "../actionRenders/ActionDispacther";
import { FormilyPageSchema } from "../types/JsonSchemaFormily";

export const useResolvedActions  = (action: []) => {
  const form = useForm();
  const pageSchema = usePageSchema();
  const ctx = useActionContext(form?.values);

   return useMemo(() => {
    if (!action?.length) {
      return {};
    }

    return resolveComponentActions(
      action,
      pageSchema,
      ctx
    );
  }, [action, pageSchema, ctx]);
};

export const useComponentProps = (props: any) => {
  const handlers = useResolvedActions(props.action);

  return {
    ...props,
    ...handlers,
  };
};