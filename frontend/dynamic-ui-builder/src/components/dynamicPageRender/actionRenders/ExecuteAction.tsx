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
                    params: resolveRecord(action.api?.params, ctx),
                    data: ctx.formData,
                });

                console.log("Success", response.data);
                ctx.showToast?.("Saved successfully", "success");
            } catch (err) {
                console.error("Submit failed", err);
                ctx.showToast?.("Failed to save. Please try again.", "error");
            }
            break;
        }

        case "FETCH_DATA": {
            try {
                const response = await apiClient({
                    method: action.api?.method || "GET",
                    url: action.api?.url,
                    params: resolveRecord(action.api?.params, ctx),
                    data: resolveValue(action.api?.body, ctx),
                });

                const responseData = action.api?.responsePath
                    ? getByPath(response.data, action.api.responsePath)
                    : response.data;

                const targetField = getSetFieldName(action);
                if (targetField) {
                    const fieldValue = action.setField?.value
                        ? resolveValue(action.setField.value, ctx, responseData)
                        : responseData;

                    ctx.setFieldValue?.(targetField, fieldValue);
                }

                console.log("Fetched data", responseData);
            } catch (err) {
                console.error("Fetch failed", err);
                ctx.showToast?.("Failed to fetch data. Please try again.", "error");
            }
            break;
        }

        case "NAVIGATE": {
            ctx.navigate?.(buildNavigatePath(action, ctx), { replace: action.navigate?.replace });
            break;
        }

        case "SHOW_TOAST": {
            ctx.showToast?.(
                resolveValue(action.toast?.message || "", ctx),
                action.toast?.severity || action.toast?.type || "success"
            );
            break;
        }

        case "SET_FIELD_VALUE": {
            const field = getSetFieldName(action);
            const value = resolveValue(action.setField?.value, ctx);
            ctx.setFieldValue?.(field, value);
            break;
        }

        case "RESET_FORM": {
            await ctx.resetForm?.();
            break;
        }

        case "EMIT_EVENT": {
            const eventConfig = action.event || action.emit || {};
            const eventName = eventConfig.name || action.eventName;
            const payload = resolveValue(parseJsonValue(eventConfig.payload), ctx);
            ctx.emitEvent?.(eventName, payload);
            break;
        }

        default:
            console.warn(`Unsupported action type: ${action.type}`);
    }
}

import apiClient from "../../../api/apiClient";
import { FormilyPageSchema } from "../types/JsonSchemaFormily";

export interface ActionContext {
    navigate?: (path: string, options?: { replace?: boolean }) => void;
    showToast?: (message: string, type?: string) => void;
    setFieldValue?: (field: string, value: any) => void;
    resetForm?: () => Promise<void> | void;
    getFormValue?: (field: string) => any;
    emitEvent?: (name: string, payload?: any) => void;
    formData?: any;
    form?: any;
}

const getByPath = (source: any, path?: string) => {
    if (!path) return source;

    return path.split(".").reduce((current, key) => {
        if (current == null) return undefined;
        return current[key];
    }, source);
};

const parseJsonValue = (value: any) => {
    if (typeof value !== "string") return value;

    try {
        return JSON.parse(value);
    } catch {
        return value;
    }
};

const resolveValue = (value: any, ctx: ActionContext, responseData?: any) => {
    if (Array.isArray(value)) {
        return value.map((item) => resolveValue(item, ctx, responseData));
    }

    if (value && typeof value === "object") {
        return Object.entries(value).reduce<Record<string, any>>((result, [key, entryValue]) => {
            result[key] = resolveValue(entryValue, ctx, responseData);
            return result;
        }, {});
    }

    if (typeof value !== "string") return value;

    if (value === "$form") return ctx.formData;
    if (value === "$response") return responseData;

    if (value.startsWith("$form.")) {
        const field = value.slice("$form.".length);
        return ctx.getFormValue?.(field) ?? getByPath(ctx.formData, field);
    }

    if (value.startsWith("$response.")) {
        return getByPath(responseData, value.slice("$response.".length));
    }

    return value.replace(/\$(form|response)\.([A-Za-z0-9_.-]+)/g, (_match, source, path) => {
        const resolved = source === "form"
            ? ctx.getFormValue?.(path) ?? getByPath(ctx.formData, path)
            : getByPath(responseData, path);

        return resolved == null ? "" : String(resolved);
    });
};

const resolveRecord = (
    record: Record<string, any> | undefined,
    ctx: ActionContext,
    responseData?: any
) => {
    if (!record) return undefined;

    return Object.entries(record).reduce<Record<string, any>>((result, [key, value]) => {
        result[key] = resolveValue(value, ctx, responseData);
        return result;
    }, {});
};

const getSetFieldName = (action: any) =>
    action.setField?.field || action.setField?.fieldName || action.targetField;

const buildNavigatePath = (action: any, ctx: ActionContext) => {
    const navigateConfig = action.navigate || {};
    const path = resolveValue(navigateConfig.path || "/", ctx);
    const params = resolveRecord(navigateConfig.params, ctx);

    if (!params || Object.keys(params).length === 0) {
        return path;
    }

    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value != null && value !== "") {
            query.append(key, String(value));
        }
    });

    const queryString = query.toString();
    if (!queryString) return path;

    return `${path}${path.includes("?") ? "&" : "?"}${queryString}`;
};

