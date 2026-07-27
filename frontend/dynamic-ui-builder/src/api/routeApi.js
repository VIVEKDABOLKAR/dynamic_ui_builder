import apiClient from "./apiClient";


export const resolveRoute = async (path) => {
    const response = (await apiClient.get("/api/ui/routes/resolve", {
        params: {
            path
        }
    }));
    console.log(response.data);
    return response.data;
}