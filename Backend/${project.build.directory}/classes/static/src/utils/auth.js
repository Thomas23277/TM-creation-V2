const API_URL = "http://localhost:8080";
/* ============================================================
🧩 Función auxiliar: parsear error
============================================================ */
async function parseErrorResponse(res) {
    try {
        const text = await res.text();
        if (!text)
            return `Error (${res.status})`;
        try {
            const data = JSON.parse(text);
            return (data === null || data === void 0 ? void 0 : data.message) || (data === null || data === void 0 ? void 0 : data.error) || text;
        }
        catch (_a) {
            return text;
        }
    }
    catch (_b) {
        return `Error (${res.status})`;
    }
}
/* ============================================================
🧩 Normalizar usuario (tolerante a backend)
============================================================ */
function normalizeUser(usuario) {
    var _a, _b, _c, _d, _e, _f, _g;
    if (!usuario)
        return null;
    const rol = (_d = (_c = (_b = (_a = usuario.rol) !== null && _a !== void 0 ? _a : usuario.role) !== null && _b !== void 0 ? _b : usuario.Rol) !== null && _c !== void 0 ? _c : usuario.Role) !== null && _d !== void 0 ? _d : null;
    return {
        id: (_e = usuario.id) !== null && _e !== void 0 ? _e : null,
        nombre: (_f = usuario.nombre) !== null && _f !== void 0 ? _f : "",
        email: (_g = usuario.email) !== null && _g !== void 0 ? _g : "",
        rol
    };
}
/* ============================================================
🌐 Helper fetch con configuración base
============================================================ */
async function fetchWithSession(url, options = {}) {
    return fetch(url, Object.assign({ credentials: "include", mode: "cors", headers: Object.assign({ "Content-Type": "application/json", "Accept": "application/json" }, (options.headers || {})) }, options));
}
/* ============================================================
🔐 LOGIN TRADICIONAL (SESION REAL + VALIDACIÓN)
============================================================ */
export async function login(email, contrasenia) {
    const res = await fetchWithSession(`${API_URL}/api/auth/login`, {
        method: "POST",
        body: JSON.stringify({ email, contrasenia })
    });
    if (!res.ok) {
        const err = await parseErrorResponse(res);
        throw new Error(err);
    }
    // 🔎 Validar sesión real después del login
    const sessionUser = await checkSession();
    if (!sessionUser) {
        throw new Error("No se pudo validar la sesión.");
    }
    return sessionUser;
}
/* ============================================================
🔵 LOGIN CON GOOGLE
============================================================ */
export function loginWithGoogle() {
    window.location.href = `${API_URL}/oauth2/authorization/google`;
}
/* ============================================================
📥 REGISTER
============================================================ */
export async function register(nombre, apellido, email, contrasenia) {
    const res = await fetchWithSession(`${API_URL}/api/auth/register`, {
        method: "POST",
        body: JSON.stringify({
            nombre,
            apellido,
            email,
            contrasenia
        })
    });
    if (!res.ok) {
        const err = await parseErrorResponse(res);
        throw new Error(err);
    }
    return await res.json();
}
/* ============================================================
👤 VALIDAR SESIÓN REAL DESDE BACKEND
============================================================ */
export async function checkSession() {
    try {
        const res = await fetchWithSession(`${API_URL}/api/auth/me`, {
            method: "GET"
        });
        if (res.status === 401)
            return null;
        if (!res.ok)
            return null;
        const data = await res.json();
        return normalizeUser(data);
    }
    catch (_a) {
        return null;
    }
}
/* ============================================================
👤 Obtener usuario ACTUAL desde backend
============================================================ */
export async function getUser() {
    return await checkSession();
}
export const getCurrentUser = getUser;
/* ============================================================
🚪 LOGOUT REAL (SESION)
============================================================ */
export async function logout() {
    try {
        await fetchWithSession(`${API_URL}/api/auth/logout`, {
            method: "POST"
        });
    }
    catch (_a) { }
    window.location.href = "/src/pages/auth/login/login.html";
}
