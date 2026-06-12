import { useForm } from "@formily/react";
import { useContext, useMemo } from "react";
import { useActionContext } from "../context/useActionContext";
import { PageSchemaContext } from "../context/PageSchemaContext";
import { resolveComponentActions } from "../actionRenders/ActionDispacther";

export const useResolvedActions  = (action: []) => {
  const form = useForm();
  const pageSchema = useContext(PageSchemaContext);
  const ctx = useActionContext(form?.values);

   return useMemo(() => {
    if (!action?.length) {
      return {};
    }

    return resolveComponentActions(
      action,
      pageSchema?.["x-actions"],
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