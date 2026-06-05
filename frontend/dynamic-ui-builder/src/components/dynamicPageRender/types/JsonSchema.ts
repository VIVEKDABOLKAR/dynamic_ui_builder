// ============================================
// ENUMS
// ============================================

export type PageStatus = "ACTIVE" | "DRAFT" | "INACTIVE" | "DELETED";

export type ComponentType =
  | "heading"
  | "layout"
  | "input"
  | "textarea"
  | "select"
  | "checkbox"
  | "button"
  | "card"
  | "divider"
  | "table"
  | "custom"
  | "radio"
  | "datepicker";

export type MappingType =
  | "STATIC"
  | "API"
  | "FIELD"
  | "FORMULA"
  | "LOOKUP"
  | "DATABASE"
  | "ENTITY";


  // create genralize apiConfig , to use everywhere we need apidefination

// ============================================
// API CONFIG
// Generalized API definition used across actions, lookups and mappings
// ============================================
export interface ApiConfig {
  url: string; // full URL or registry key
  method?: "GET" | "POST" | "PUT" | "DELETE";
  headers?: Record<string, string>;
  params?: Record<string, any>;
  body?: any;
  responsePath?: string; // optional path inside response to extract
  lazyLoad?: boolean;
  // label/value mapping for lookups
  labelKey?: string;
  valueKey?: string;
}

// ============================================
// ROOT PAGE JSON SCHEMA
// ============================================

export interface DynamicPageSchema {
  page: PageMeta;
  components: ComponentSchema[];
  actions?: ActionRegistry;
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
  children?: ComponentSchema[];
  mapping?: MappingSchema;
  lookup?: LookupSchema;
  action?: ActionSchema[];
}


// ============================================
// ACTION PROPERTIES
// components specfice action array for functionlity
// ============================================
export interface ActionSchema {
 event: ActionEvent;
 ref: string;
 condition: string;
}

export type ActionEvent=
  | "onClick"
  | "onLoad"
  | "onChange"
  | "onHover"
  | "onBlur"
  | "onFocus"
  | "onNavigation"
  | string; 

export interface ActionRegistry {
  [actionName: string]: ActionConfig;
}

export interface ActionConfig {
  type: ActionType;
  api?: ApiConfig;
  navigate?: NavigateConfig;
  setField?: SetFieldConfig; //can use to toggel field as well
    toast?: ToastConfig;
  chain?: string[];          // list of other actionNames to run in sequence
  onSuccess?: string;        // actionName to run on success
  onError?: string;          // actionName to run on error
}

export type ActionType =
  | "SUBMIT_FORM"
  | "FETCH_DATA"
  | "NAVIGATE"
  | "SET_FIELD_VALUE"
  | "SHOW_TOAST"
  | "TOGGLE_VISIBLE"
  | "RESET_FORM"
  | "CONFIRM_DIALOG"
  | "CHAIN";

  export interface NavigateConfig {
  path: string;              // e.g. "/ui/employee-list"
  params?: Record<string, string>; // field refs: { id: "$form.employeeId" }
}

export interface SetFieldConfig {
  field: string;             // target field name
  value: any;                // static value or "$response.fieldName"
}

export interface ToastConfig {
  message: string;
  severity: "success" | "error" | "info" | "warning";
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

  formate?: string,

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

  source: string;
  target?: string;
  method?: "GET" | "POST" | "PUT" | "DELETE";
  responsePath?: string;

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
  apiUrl: string;
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