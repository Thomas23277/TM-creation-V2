import "./styles.css";
import { getCurrentUser, checkSession } from "./utils/auth";
(async function checkRootRedirect() {
    // 🔥 Validar sesión real contra backend
    const sessionUser = await checkSession();
    const localUser = getCurrentUser();
    const user = (sessionUser !== null && sessionUser !== void 0 ? sessionUser : localUser);
    const currentPath = window.location.pathname;
    const loginPath = "/src/pages/auth/login/login.html";
    const registerPath = "/src/pages/auth/register/register.html";
    const adminPath = "/src/pages/admin/adminHome/adminHome.html";
    const storePath = "/src/pages/store/home/home.html";
    console.log("--- Redireccionamiento de Ruta ---");
    console.log(`👤 Usuario detectado: ${user ? user.rol : "ninguno"}`);
    console.log(`📄 Ruta actual: ${currentPath}`);
    console.log("---------------------------------");
    // ======================
    // 🔥 MANEJO DE RAÍZ "/"
    // ======================
    if (currentPath === "/" || currentPath.endsWith("index.html")) {
        if (!user) {
            console.log("🔹 En raíz sin usuario — redirigiendo a login");
            window.location.href = loginPath;
            return;
        }
        if (user.rol === "ADMIN") {
            console.log("👑 En raíz — redirigiendo a Admin Home");
            window.location.href = adminPath;
        }
        else {
            console.log("🛒 En raíz — redirigiendo a Store Home");
            window.location.href = storePath;
        }
        return;
    }
    // ======================
    // 🚫 No autenticado
    // ======================
    if (!user) {
        if (!currentPath.includes("/auth/login/") &&
            !currentPath.includes("/auth/register/")) {
            console.log("⚠️ No autenticado. Redirigiendo a login");
            window.location.href = loginPath;
        }
        else {
            console.log("✅ Página pública (login/register).");
        }
        return;
    }
    // ======================
    // 👑 Admin
    // ======================
    if (user.rol === "ADMIN") {
        if (!currentPath.includes("/admin/")) {
            console.log("👑 Admin detectado. Redirigiendo a Admin Home.");
            window.location.href = adminPath;
        }
        else {
            console.log("✅ Admin en página válida.");
        }
    }
    // ======================
    // 🛒 Usuario normal
    // ======================
    else {
        if (!currentPath.includes("/store/")) {
            console.log("🛒 Usuario regular. Redirigiendo a Store Home.");
            window.location.href = storePath;
        }
        else {
            console.log("✅ Usuario regular en página válida.");
        }
    }
})();
// ======================
// ⚙️ Carga dinámica de scripts según la página actual (Vite)
// ======================
const path = window.location.pathname;
const modules = import.meta.glob([
    "./pages/auth/login/login.js",
    "./pages/auth/register/register.js",
    "./pages/store/home/home.js",
    "./pages/store/productDetail/productDetail.js",
    "./pages/store/cart/cart.js",
    "./pages/admin/adminHome/adminHome.js",
    "./pages/admin/categories/categories.js",
    "./pages/admin/products/products.js",
    "./pages/admin/orders/orders.js",
]);
for (const filePath in modules) {
    const normalizedPath = filePath.replace("./pages", "/src/pages").replace(/\.[^/.]+$/, "");
    const currentPath = path.replace(/\.[^/.]+$/, "");
    if (currentPath.includes(normalizedPath)) {
        console.log(`✅ Cargando módulo dinámico: ${filePath}`);
        modules[filePath]();
        break;
    }
}
console.log("⚙️ Scripts dinámicos cargados según la página actual");
