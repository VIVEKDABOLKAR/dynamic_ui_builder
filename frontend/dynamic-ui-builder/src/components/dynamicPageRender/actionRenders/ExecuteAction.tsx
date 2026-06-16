import axios from "axios";
import { ActionRegistry, DynamicPageSchema } from "../types/JsonSchema";
import { buildEntityPayload } from "../../dataMappingEngine/utils/buildEntityPayload";
import { useContext } from "react";
import { PageSchemaContext } from "../context/PageSchemaContext";
import { FormilyPageSchema } from "../types/JsonSchemaFormily";

const resolveUrl = (url: string | undefined) => {
    if (!url) return "";
    if (url.startsWith("http://") || url.startsWith("https://")) {
        return url;
    }
    if (url.startsWith("/api")) {
        return `http://localhost:8080${url}`;
    }
    return url;
};

export interface ActionContext {
    navigate?: (path: string) => void;
    showToast?: (message: string) => void;
    formData?: any;
}

export default async function ExecuteAction(
    ref: string,
    cond: string,
    pageSchema: FormilyPageSchema,
    ctx: ActionContext
) {
    const actRegistry = pageSchema?.["x-actions"];
    if(!actRegistry) {
        console.warn(`Action Registry not found`);
        return;
    }

    const action = actRegistry[ref];

    if (!action) {
        console.warn(`Action ${ref} not found`);
        return;
    }
    console.log(action)

    switch (action.type) {

        case "SUBMIT_FORM": {
            console.log(buildEntityPayload(ctx.formData, pageSchema));
            console.log(ctx.formData);

            const response = await axios({
                method: action.api?.method || "POST",
                url: resolveUrl(action.api?.url),
                data: ctx.formData,
            });

            console.log("Success", response.data);

            break;
        }

        case "FETCH_DATA": {

            const response = await axios({
                method: action.api?.method || "GET",
                url: action.api?.url
            });

            console.log(response.data);

            break;
        }

        case "NAVIGATE": {
            console.log("Navigate to", action.navigate?.path);
            ctx.navigate?.(action.navigate?.path || "/");
            break;
        }

        case "SHOW_TOAST": {

            console.log(action.toast?.message);
            ctx.showToast?.(action.toast?.message || "");
            break;
        }

        default:
            console.warn("Unsupported action type");
    }
}

