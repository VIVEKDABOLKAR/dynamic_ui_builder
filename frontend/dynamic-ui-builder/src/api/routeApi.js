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

export const resolveNavigation = async (path) => {
    const response = (await apiClient.get("/api/ui/routes/navigation"));
    console.log(response.data);
    return response.data;
}

export const getPathUsingUIRoute = async (page_id) => {
    const response = await apiClient.get(`/api/ui/routes/path?pageid=${page_id}`)
    console.log(response);
    return response.data;
}