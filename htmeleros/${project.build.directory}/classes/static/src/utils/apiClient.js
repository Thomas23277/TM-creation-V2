const API_URL = "http://localhost:8080"; // 🔧 Asegura que todos los fetch usen la misma base URL
export { API_URL };
/**
 * Wrapper para realizar peticiones HTTP con manejo de errores.
 * @param path Ruta del endpoint (por ejemplo: "/api/producto")
 * @param options Opciones fetch (method, headers, body, etc.)
 * @returns Respuesta JSON del servidor
 */
export async function apiFetch(path, options = {}) {
    try {
        const response = await fetch(`${API_URL}${path}`, {
            method: options.method || "GET",
            headers: Object.assign({ "Content-Type": "application/json" }, (options.headers || {})),
            body: options.body,
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        // Intenta parsear la respuesta como JSON
        const data = await response.json().catch(() => null);
        return data;
    }
    catch (error) {
        console.error("❌ Error en apiFetch:", error);
        throw error;
    }
}
