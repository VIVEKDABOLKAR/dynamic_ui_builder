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
  let style;
  if(props.style){
    style = convertToImportantClassName(JSON.parse(props.style ?? "{}"));
  }

  return {
    ...props,
    ...handlers,
    style
  };
};

/**
 * Need this because MUI give there classname highest proiorti
 * so user classname override 
 * fix - mark classname as imortant so mui cannot override
 * @param className 
 * @returns 
 */
export function convertToImportantClassName(input?: { className?: string }): string {
  if (!input?.className) return "";

  return input.className
    .split(" ")
    .filter(Boolean)
    .map((cls) => {
      if (cls.startsWith("!")) return cls;
      return `!${cls}`;
    })
    .join(" ");
}