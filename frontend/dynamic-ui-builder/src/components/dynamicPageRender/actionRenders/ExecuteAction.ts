import axios from "axios";
import { ActionRegistry } from "../types/JsonSchema";

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

  const fetchValue = () => {

  }

  const

export default async function ExecuteAction(
  ref: string,
  cond: string,
  actRegistry: ActionRegistry,
) {

  const action = actRegistry[ref];

  if (!action) {
    console.warn(`Action ${ref} not found`);
    return;
  }

  switch (action.type) {

    case "SUBMIT_FORM": {

      const response = await axios({
        method: action.api?.method || "POST",
        url: resolveUrl(action.api?.url),
        data: fetchValue();
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

      break;
    }

    case "SHOW_TOAST": {

      console.log(action.toast?.message);

      break;
    }

    default:
      console.warn("Unsupported action type");
  }
}