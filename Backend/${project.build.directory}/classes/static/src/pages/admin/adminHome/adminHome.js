import { getCurrentUser, logout, checkSession } from "../../../utils/auth";
import { saveCategory as saveCategoryFn, renderCategories, } from "../categories/categories";
import { loadProducts as loadProductsData, saveProduct as saveProductFn, } from "../products/products";
import { loadOrders as loadOrdersData } from "../orders/orders";
async function initAdmin() {
    var _a;
    const sessionUser = await checkSession();
    const localUser = getCurrentUser();
    const user = (sessionUser !== null && sessionUser !== void 0 ? sessionUser : localUser);
    if (!user) {
        alert("Acceso denegado.");
        window.location.href = "/src/pages/auth/login/login.html";
        return;
    }
    if (((_a = user.rol) === null || _a === void 0 ? void 0 : _a.toUpperCase()) !== "ADMIN") {
        alert("Solo administradores.");
        window.location.href = "/src/pages/store/home/home.html";
        return;
    }
    setupUI(user);
}
/* ===============================
   UI ELEMENTS
=============================== */
const navBtns = Array.from(document.querySelectorAll(".nav-btn"));
const views = Array.from(document.querySelectorAll(".view"));
const pageTitle = document.getElementById("page-title");
const logoutBtn = document.getElementById("logoutBtn");
const metricProducts = document.getElementById("metric-products");
const metricCategories = document.getElementById("metric-categories");
const metricOrders = document.getElementById("metric-orders");
const cardProducts = document.getElementById("card-products");
const cardCategories = document.getElementById("card-categories");
const cardOrders = document.getElementById("card-orders");
/* ===============================
   SETUP UI
=============================== */
function setupUI(user) {
    var _a, _b;
    const nameEl = document.getElementById("admin-name");
    const emailEl = document.getElementById("admin-email");
    if (nameEl)
        nameEl.textContent = (_a = user.nombre) !== null && _a !== void 0 ? _a : "Admin";
    if (emailEl)
        emailEl.textContent = (_b = user.email) !== null && _b !== void 0 ? _b : "";
    showView("dashboard");
    loadMetrics();
}
/* ===============================
   LOGOUT
=============================== */
logoutBtn.onclick = async () => {
    await logout();
    window.location.href = "/src/pages/auth/login/login.html";
};
/* ===============================
   NAVIGATION
=============================== */
navBtns.forEach((b) => b.addEventListener("click", async () => {
    navBtns.forEach((nb) => nb.classList.remove("active"));
    b.classList.add("active");
    const view = b.getAttribute("data-view");
    showView(view);
    await loadViewData(view);
}));
function showView(name) {
    var _a;
    views.forEach((v) => v.classList.remove("active"));
    (_a = document.getElementById("view-" + name)) === null || _a === void 0 ? void 0 : _a.classList.add("active");
    pageTitle.textContent = name.toUpperCase();
}
/* ===============================
   METRICS
=============================== */
async function loadMetrics() {
    try {
        const [prods, cats, orders] = await Promise.all([
            fetch("/api/productos", { credentials: "include" }).then((r) => r.json()),
            fetch("/api/categoria", { credentials: "include" }).then((r) => r.json()),
            fetch("/api/pedidos", { credentials: "include" }).then((r) => r.json()),
        ]);
        metricProducts.textContent = String(prods.length);
        metricCategories.textContent = String(cats.length);
        metricOrders.textContent = String(orders.length);
        cardProducts.textContent = String(prods.length);
        cardCategories.textContent = String(cats.length);
        cardOrders.textContent = String(orders.length);
    }
    catch (err) {
        console.error("Error métricas:", err);
    }
}
/* ===============================
   VIEW DATA LOADER
=============================== */
async function loadViewData(view) {
    switch (view) {
        case "categories":
            await renderCategories();
            attachCategoryForm();
            break;
        case "products":
            await loadProductsData();
            attachProductForm();
            break;
        case "orders":
            await loadOrdersData();
            break;
    }
}
/* ===============================
   CATEGORY FORM
=============================== */
function attachCategoryForm() {
    const form = document.getElementById("cat-form");
    if (!form)
        return;
    form.onsubmit = async (e) => {
        var _a, _b, _c;
        e.preventDefault();
        const idRaw = document.getElementById("cat-id").value;
        const nombre = document.getElementById("cat-nombre").value.trim();
        const imagen = (_c = (_b = (_a = document.getElementById("cat-imagen")) === null || _a === void 0 ? void 0 : _a.files) === null || _b === void 0 ? void 0 : _b[0]) !== null && _c !== void 0 ? _c : null;
        if (!nombre) {
            alert("El nombre es obligatorio.");
            return;
        }
        await saveCategoryFn({
            id: idRaw ? Number(idRaw) : undefined,
            nombre,
            imagen,
        });
        await renderCategories();
        await loadMetrics();
        form.reset();
    };
    const clearBtn = document.getElementById("cat-clear");
    if (clearBtn) {
        clearBtn.onclick = () => {
            form.reset();
            document.getElementById("cat-id").value = "";
        };
    }
}
/* ===============================
   PRODUCT FORM
=============================== */
function attachProductForm() {
    const form = document.getElementById("prod-form");
    if (!form)
        return;
    form.onsubmit = async (e) => {
        e.preventDefault();
        await saveProductFn(e);
        await loadProductsData();
        await loadMetrics();
    };
    const clearBtn = document.getElementById("prod-clear");
    if (clearBtn) {
        clearBtn.addEventListener("click", () => {
            form.reset();
            document.getElementById("prod-id").value = "";
            const preview = document.getElementById("prod-preview");
            if (preview)
                preview.src = "/src/assets/food/default.jpeg";
            const file = document.getElementById("prod-file");
            if (file)
                file.value = "";
        });
    }
}
/* ===============================
   INITIALIZE
=============================== */
initAdmin();
