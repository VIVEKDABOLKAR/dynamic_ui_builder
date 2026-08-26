import apiClient from "../../../api/apiClient";
import { FormilyPageSchema } from "../types/JsonSchemaFormily";

export default async function ExecuteAction(
    ref: string,
    cond: string,
    pageSchema: FormilyPageSchema,
    ctx: ActionContext,
    visited: Set<string> = new Set()
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

    // FIX: condition was captured/stored end-to-end but never evaluated —
    // every action fired unconditionally. Skip execution when it evaluates false.
    if (!evaluateCondition(cond, ctx)) {
        return;
    }

    // FIX: guard against a CHAIN action referencing itself (directly or via a
    // cycle of chains), which would otherwise recurse forever.
    if (visited.has(ref)) {
        console.warn(`Circular action chain detected at "${ref}", skipping`);
        return;
    }
    visited.add(ref);

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
            console.log("field "+field, "  ", "value "+value)
            ctx.setFieldValue?.(field, value);
            break;
        }

        case "RESET_FORM": {
            await ctx.resetForm?.();
            break;
        }

        //typeScript issues ignore till fully understanding emit-event
        case "EMIT_EVENT": {
            const eventConfig = action.event || action.emit || {};
            const eventName = eventConfig.name || action.eventName;
            const payload = resolveValue(parseJsonValue(eventConfig.payload), ctx);
            ctx.emitEvent?.(eventName, payload);
            break;
        }``

        // FIX: CHAIN was fully configurable from the admin UI (type selector,
        // ChainFields, saved as { chain: [...] }) but had no runtime handler —
        // it silently hit "Unsupported action type". Run each referenced
        // action in order, sharing the same visited set to prevent cycles.
        case "CHAIN": {
            const chainRefs = Array.isArray(action.chain) ? action.chain : [];
            for (const chainRef of chainRefs) {
                await ExecuteAction(chainRef, "true", pageSchema, ctx, visited);
            }
            break;
        }

        default:
            console.warn(`Unsupported action type: ${action.type}`);
    }
}


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

const resolveValue = (value: any, ctx: ActionContext, responseData?: any): any => {
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

// FIX: previously unused — `cond` reached this file but nothing ever called
// this. Supports the same "true" / "false" / "$form.field" style already
// used elsewhere in this file, plus simple JS-like comparisons
// (e.g. `$form.status == 'active' && $form.qty > 0`).
const evaluateCondition = (cond: string | undefined, ctx: ActionContext): boolean => {
    const expr = (cond ?? "true").trim();

    if (expr === "" || expr === "true") return true;
    if (expr === "false") return false;

    const substituted = expr.replace(/\$form\.([A-Za-z0-9_.-]+)/g, (_match, path) => {
        const value = ctx.getFormValue?.(path) ?? getByPath(ctx.formData, path);
        return JSON.stringify(value ?? null);
    });

    try {
        // eslint-disable-next-line no-new-func
        return Boolean(new Function(`"use strict"; return (${substituted});`)());
    } catch (err) {
        console.warn(`Failed to evaluate condition "${cond}", defaulting to true`, err);
        return true;
    }
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

