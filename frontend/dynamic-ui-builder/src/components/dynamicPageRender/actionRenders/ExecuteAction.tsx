import apiClient from "../../../api/apiClient";
import { ActionRegistry, DynamicPageSchema } from "../types/JsonSchema";
import { buildEntityPayload } from "../../dataMappingEngine/utils/buildEntityPayload";
import { useContext } from "react";
import { PageSchemaContext } from "../context/PageSchemaContext";
import { FormilyPageSchema } from "../types/JsonSchemaFormily";

export interface ActionContext {
    navigate?: (path: string) => void;
    showToast?: (message: string, type?: string) => void;
    formData?: any;
}

export default async function ExecuteAction(
    ref: string,
    cond: string,
    pageSchema: FormilyPageSchema,
    ctx: ActionContext
) {
    const actRegistry = pageSchema?.["x-actions"];
    if (!actRegistry) {
        console.warn(`Action Registry not found`);
        return;
    }

    const action = actRegistry[ref];

    if (!action) {
        console.warn(`Action ${ref} not found`);
        return;
    }

    switch (action.type) {

        case "SUBMIT_FORM": {
            try {
                const response = await apiClient({
                    method: action.api?.method || "POST",
                    url: action.api?.url,
                    data: ctx.formData,
                });

                console.log("Success", response.data);
                ctx.showToast?.("Saved successfully");
            } catch (err) {
                console.error("Submit failed", err);
                ctx.showToast?.("Failed to save. Please try again.", "error");
            }
            break;
        }

        case "FETCH_DATA": {
            const response = await apiClient({
                method: action.api?.method || "GET",
                url: action.api?.url
            });

            console.log(response.data);
            break;
        }

        case "NAVIGATE": {
            ctx.navigate?.(action.navigate?.path || "/");
            break;
        }

        case "SHOW_TOAST": {
            ctx.showToast?.(action.toast?.message || "");
            break;
        }

        default:
            console.warn("Unsupported action type");
    }
}