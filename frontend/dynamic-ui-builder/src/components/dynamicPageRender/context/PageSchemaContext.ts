import { createContext, ReactNode, useContext } from 'react'
import { FormilyPageSchema } from '../types/JsonSchemaFormily';

export const PageSchemaContext = createContext<FormilyPageSchema | undefined>(undefined);

export function usePageSchema() {
  const context = useContext(PageSchemaContext);
    
  if (!context) {
    throw new Error("pageSchema must be used within a PageSchema Provider");
  }

  return context;
}

