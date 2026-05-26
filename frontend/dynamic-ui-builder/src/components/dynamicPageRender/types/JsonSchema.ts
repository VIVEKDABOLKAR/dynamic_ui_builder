// ============================================
// ENUMS
// ============================================

export type PageStatus = "ACTIVE" | "DRAFT" | "INACTIVE" | "DELETED";

export type ComponentType =
  | "heading"
  | "input"
  | "textarea"
  | "select"
  | "checkbox"
  | "button"
  | "divider"
  | "table"
  | "custom";

export type MappingType =
  | "STATIC"
  | "API"
  | "FIELD"
  | "FORMULA"
  | "LOOKUP";


// ============================================
// ROOT PAGE JSON SCHEMA
// ============================================

export interface DynamicPageSchema {
  page: PageMeta;
  components: ComponentSchema[];
}


// ============================================
// PAGE SCHEMA
// ============================================

export interface PageMeta {
  id: number;
  pageCode: string;      // unique page key
  pageName: string;      // display name
  route: string;         // /home, /job/create
  status: PageStatus;
}


// ============================================
// COMPONENT SCHEMA
// ============================================

export interface ComponentSchema {
  id: number;
  name: string;                    // unique field/component key
  type: ComponentType;
  sequence: number;               // render order
  properties: ComponentProperties;
  mapping?: MappingSchema;
  lookup?: LookupSchema;
}


// ============================================
// COMPONENT PROPERTIES
// shared UI + behavior config
// ============================================

export interface ComponentProperties {
  label?: string;
  placeholder?: string;
  text?: string;

  width?: number | string;
  height?: number | string;

  visible?: boolean;
  disabled?: boolean;
  required?: boolean;

  defaultValue?: any;

  options?: SelectOption[];

  style?: Record<string, any>;

  [key: string]: any; // extensible
}


// ============================================
// SELECT / DROPDOWN OPTIONS
// ============================================

export interface SelectOption {
  label: string;
  value: string | number | boolean;
}


// ============================================
// MAPPING SCHEMA
// Controls data binding / transformations
// ============================================

export interface MappingSchema {
  type: MappingType;

  source?: string;
  target?: string;

  expression?: string;

  defaultValue?: any;

  dependencies?: string[];
}


// Examples:
//
// STATIC   -> fixed value
// FIELD    -> map another field
// API      -> map backend response
// FORMULA  -> computed field
// LOOKUP   -> external lookup


// ============================================
// LOOKUP SCHEMA
// For dynamic dropdown / autocomplete / table
// ============================================

export interface LookupSchema {
  apiUrl?: string;
  method?: "GET" | "POST";

  labelKey?: string;
  valueKey?: string;

  searchKey?: string;

  params?: Record<string, any>;

  dependsOn?: string[];

  lazyLoad?: boolean;
}


// ============================================
// OPTIONAL: TYPE-SAFE SPECIALIZED COMPONENTS
// ============================================

export interface InputComponent extends ComponentSchema {
  type: "input";
  properties: ComponentProperties & {
    placeholder?: string;
    required?: boolean;
  };
}

export interface ButtonComponent extends ComponentSchema {
  type: "button";
  properties: ComponentProperties & {
    text: string;
  };
}

export interface SelectComponent extends ComponentSchema {
  type: "select";
  properties: ComponentProperties & {
    options?: SelectOption[];
  };
}