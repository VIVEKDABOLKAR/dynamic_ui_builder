
import { ActionRegistry, ApiConfig, ActionConfig } from "./JsonSchema";

export type FormilyFieldType =
    | "object"
    | "string"
    | "number"
    | "boolean"
    | "array"
    | "void"
    | "date";

export interface FormilyEnumOption {
    label: string;
    value: string | number | boolean;
}

export interface FormilyValidator {
  required?: boolean;
  min?: number;
  max?: number;
  minLength?: number;
  maxLength?: number;
  pattern?: string;
  format?: string;
  message?: string;
}

export interface FormilyReaction {
  dependencies?: string[];

  fulfill?: {
    state?: Record<string, any>;
    schema?: Record<string, any>;
  };

  otherwise?: {
    state?: Record<string, any>;
    schema?: Record<string, any>;
  };

  target?: string;

  when?: string;
}

export type ResolvedAction = Function;

export type ResolvedActionSchema =
    Record<string, ResolvedAction>;

export interface FormilyFieldSchema {
    type?: FormilyFieldType;

    title?: string;
    description?: string;

    properties?: Record<string, FormilyFieldSchema>;
    items?: FormilyFieldSchema;

    enum?: FormilyEnumOption[];

    default?: any;

    // UI Rendering
    "x-component"?: string;
    "x-component-props"?: Record<string, any> 
    "x-mapping"?: Record<string, any>;
    "x-lookup"?: Record<string, any>;

    // Wrapper
    "x-decorator"?: string;
    "x-decorator-props"?: Record<string, any>;

    // Formily Features
    "x-validator"?: FormilyValidator[];
    "x-reactions"?: FormilyReaction | FormilyReaction[];

    "x-visible"?: boolean;
    "x-hidden"?: boolean;
    "x-disabled"?: boolean;
    "x-read-pretty"?: boolean;

    "x-index"?: number;

    [key: string]: any;
}

export interface FormilyPageSchema {
  type: "object";
  properties: Record<string, FormilyFieldSchema>;
  "x-actions"?: ActionRegistry;
}