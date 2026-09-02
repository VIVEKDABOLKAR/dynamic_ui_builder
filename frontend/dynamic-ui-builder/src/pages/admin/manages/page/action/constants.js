import { ApiFields } from "./fields/ApiFields";
import { ChainFields } from "./fields/ChainFields";
import { EmitEventFields } from "./fields/EmitEventFields";
import { FetchDataFields } from "./fields/FetchDataFields";
import { NavigateFields } from "./fields/NavigateFields";
import { ResetFormFields } from "./fields/ResetFormFields";
import { SetFieldValueFields } from "./fields/SetFieldValueFields";
import { ToastFields } from "./fields/ToastFields";

export const AVAILABLE_ACTIONS = [
  { type: "SUBMIT_FORM", label: "Submit Form", icon: "📤" },
  { type: "FETCH_DATA", label: "Fetch Data", icon: "📥" },
  { type: "NAVIGATE", label: "Navigate", icon: "🧭" },
  { type: "SHOW_TOAST", label: "Show Toast", icon: "🔔" },
  { type: "SET_FIELD_VALUE", label: "Set Field Value", icon: "✏️" },
  { type: "RESET_FORM", label: "Reset Form", icon: "🔄" },
  { type: "EMIT_EVENT", label: "Emit Event", icon: "EVT" },
  { type: "CHAIN", label: "Chain Actions", icon: "⛓️" },
];

export const ACTION_FIELDS_REGISTERY = {
  NAVIGATE: NavigateFields,
  FETCH_DATA: FetchDataFields,
  SUBMIT_FORM: ApiFields,
  SHOW_TOAST: ToastFields,
  SET_FIELD_VALUE: SetFieldValueFields,
  RESET_FORM: ResetFormFields,
  EMIT_EVENT: EmitEventFields,
  CHAIN: ChainFields,
};

export const DEFAULT_FORM_DATA  = (pageCode) => ({
  uiPagecode: pageCode,
  actionName: "",
  actionType: "SUBMIT_FORM",

  path: "",
  navigateParams: "",
  navigateReplace: false,

  url: "",
  method: "GET",
  params: "",
  responsePath: "",
  targetField: "",

  toastMessage: "",
  toastType: "success",

  fieldName: "",
  fieldValue: "",

  eventName: "",
  eventPayload: "",

  chainActions: ""
});