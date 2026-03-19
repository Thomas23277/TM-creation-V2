export const BASE_URL = "http://localhost:8080/api";
/* ============================================================
   🔁 FUNCIÓN BASE PARA TODAS LAS REQUESTS
============================================================ */
async function request(path, method, body, isFormData = false) {
    const cleanPath = path.startsWith("/") ? path : `/${path}`;
    const url = `${BASE_URL}${cleanPath}`;
    const headers = {
        Accept: "application/json",
    };
    const isActuallyFormData = body instanceof FormData || isFormData;
    let finalBody = undefined;
    // JSON
    if (!isActuallyFormData && body && typeof body === "object") {
        headers["Content-Type"] = "application/json";
        finalBody = JSON.stringify(body);
    }
    // FormData
    if (isActuallyFormData) {
        finalBody = body;
    }
    const options = {
        method,
        headers,
        credentials: "include", // 🔥 FUNDAMENTAL PARA JSESSIONID
        body: method !== "GET" ? finalBody : undefined,
    };
    let response;
    try {
        response = await fetch(url, options);
    }
    catch (error) {
        console.error(`❌ Error de red al llamar ${url}:`, error);
        throw new Error("No se pudo conectar con el servidor.");
    }
    // 🔥 Manejo especial para 401 (no autenticado)
    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }
    let data = null;
    try {
        const text = await response.text();
        data = text ? JSON.parse(text) : null;
    }
    catch (_a) {
        data = null;
    }
    if (!response.ok) {
        const msg = (data === null || data === void 0 ? void 0 : data.message) ||
            (data === null || data === void 0 ? void 0 : data.error) ||
            response.statusText ||
            `Error ${response.status}`;
        console.error(`❌ API error ${response.status} (${url}):`, msg);
        throw new Error(msg);
    }
    return data;
}
/* ============================================================
   📡 MÉTODOS HTTP
============================================================ */
export function apiGet(path) {
    return request(path, "GET");
}
export function apiPost(path, body, isFormData = false) {
    return request(path, "POST", body, isFormData);
}
export function apiPut(path, body, isFormData = false) {
    return request(path, "PUT", body, isFormData);
}
export function apiDelete(path) {
    return request(path, "DELETE");
}
/* ============================================================
   🚪 LOGOUT PROFESIONAL
============================================================ */
export function apiLogout() {
    return request("/auth/logout", "POST");
}
