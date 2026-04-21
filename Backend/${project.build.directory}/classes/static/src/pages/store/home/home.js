// ======================= API GET =======================
import { apiGet, apiPost } from "../../../utils/api";
async function verificarSesion() {
    try {
        return await apiGet("/auth/me");
    }
    catch (_a) {
        window.location.href = "/src/pages/auth/login/login.html";
        return null;
    }
}
// ======================= VARIABLES DE ELEMENTOS =======================
const productListEl = document.getElementById("product-list");
const productCountEl = document.getElementById("product-count");
const menuBtn = document.getElementById("menu-btn");
const sidebar = document.getElementById("sidebar");
const closeSidebar = document.getElementById("close-sidebar");
const cartSidebarBtn = document.getElementById("cart-sidebar");
const ordersSidebarBtn = document.getElementById("orders-sidebar");
const logoutBtn = document.getElementById("logout-sidebar");
const searchBig = document.getElementById("search-input-big");
const sortBtn = document.getElementById("sort-btn");
const sortMenu = document.getElementById("sort-menu");
const cartBadgeSide = document.getElementById("cart-badge-side");
const categoryContainer = document.querySelector(".category-cards");
// ======================= SALUDO PERSONALIZADO =======================
// ======================= SALUDO PERSONALIZADO =======================
const bannerGreeting = document.getElementById("intro-greeting");
// ======================= IMÁGENES =======================
const imagenesProductos = {
    "Gomitas de oso": "/src/assets/food/GOMITASDEOSO.jpeg",
    Manzana: "/src/assets/food/MANZANA.jpeg",
    "Coca Cola 2l": "/src/assets/food/COCACOLA2L.jpeg",
    "Doritos 129g": "/src/assets/food/DORITO.jpeg",
    Lechuga: "/src/assets/food/LECHUGA.jpeg",
    "Agua mineral": "/src/assets/food/AGUAMINERAL.jpeg",
    "Coca Cola 1l": "/src/assets/food/COCACOLA1L.jpeg",
    "Fanta 2l": "/src/assets/food/FANTA2L.jpeg",
    "Priti 2l": "/src/assets/food/PRITTY2L.jpg",
    "Lata Quilmes 500ml": "/src/assets/food/LATAQUILMES.jpeg",
    "Quilmes Rubia 1l": "/src/assets/food/QUILMESRUBIA.jpeg",
    "Oreos paquete": "/src/assets/food/OREOSPAQUETE.jpeg",
    "Lays Clasicas 134g": "/src/assets/food/LAYSCLASICAS.jpeg",
    Pera: "/src/assets/food/PERA.jpeg",
    "Pringles Jamon Crudo": "/src/assets/food/PRINGLESJAMONCRUDO.jpeg",
    "Chicles de menta": "/src/assets/food/CHICLESDEMENTA.jpeg",
    Banana: "/src/assets/food/BANANA.jpeg",
    Tomate: "/src/assets/food/TOMATE.jpeg",
    Naranja: "/src/assets/food/NARANJA.jpeg",
};
const imagenesCategorias = {
    Bebidas: "/src/assets/categories/BEBIDAS.png",
    Snacks: "/src/assets/categories/SNACKS.png",
    "Frutas y Verduras": "/src/assets/categories/FRUTASYVERDURAS.png",
    Todo: "/src/assets/categories/TODO.png",
    default: "/src/assets/categories/default.jpeg",
};
function getProductImageUrl(product) {
    var _a, _b;
    if ((_a = product.urlImagen) === null || _a === void 0 ? void 0 : _a.startsWith("http"))
        return product.urlImagen;
    if ((_b = product.urlImagen) === null || _b === void 0 ? void 0 : _b.startsWith("/uploads/"))
        return `http://localhost:8080${product.urlImagen}`;
    if (product.urlImagen)
        return `http://localhost:8080/uploads/${product.urlImagen}`;
    return imagenesProductos[product.nombre] || "/src/assets/food/default.jpeg";
}
function getCategoryImageUrl(urlImagen, nombre) {
    if (!urlImagen)
        return imagenesCategorias[nombre] || imagenesCategorias.default;
    const clean = urlImagen.replace(/^\/+/, "");
    if (clean.startsWith("uploads/"))
        return `http://localhost:8080/${clean}`;
    if (clean.startsWith("categorias/"))
        return `http://localhost:8080/uploads/${clean}`;
    return `http://localhost:8080/uploads/categorias/${clean}`;
}
// ======================= CARRITO =======================
function loadCart() {
    try {
        return JSON.parse(localStorage.getItem("carrito") || "[]");
    }
    catch (_a) {
        return [];
    }
}
function updateCartBadgeSide() {
    if (!cartBadgeSide)
        return;
    const cart = loadCart();
    const total = cart.reduce((s, i) => s + (Number(i.cantidad) || 0), 0);
    cartBadgeSide.textContent = String(total);
    cartBadgeSide.style.display = total > 0 ? "inline-block" : "none";
}
window.addEventListener("storage", (e) => {
    if (e.key === "carrito")
        updateCartBadgeSide();
});
// ======================= SIDEBAR =======================
menuBtn === null || menuBtn === void 0 ? void 0 : menuBtn.addEventListener("click", () => {
    updateCartBadgeSide();
    sidebar === null || sidebar === void 0 ? void 0 : sidebar.classList.add("active");
});
closeSidebar === null || closeSidebar === void 0 ? void 0 : closeSidebar.addEventListener("click", () => {
    sidebar === null || sidebar === void 0 ? void 0 : sidebar.classList.remove("active");
});
cartSidebarBtn === null || cartSidebarBtn === void 0 ? void 0 : cartSidebarBtn.addEventListener("click", () => {
    window.location.href = "/src/pages/store/cart/cart.html";
});
ordersSidebarBtn === null || ordersSidebarBtn === void 0 ? void 0 : ordersSidebarBtn.addEventListener("click", () => {
    window.location.href = "/src/pages/client/orders/orders.html";
});
logoutBtn === null || logoutBtn === void 0 ? void 0 : logoutBtn.addEventListener("click", async () => {
    try {
        await apiPost("/auth/logout");
    }
    catch (error) {
        console.error("Error cerrando sesión", error);
    }
    localStorage.clear();
    window.location.href = "/src/pages/auth/login/login.html";
});
// ======================= DATOS =======================
let productos = [];
let productosFiltrados = [];
function normalize(str) {
    return str
        ? str.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        : "";
}
// ======================= CARGAR PRODUCTOS =======================
async function cargarProductos() {
    try {
        const data = await apiGet("/productos");
        productos = Array.isArray(data) ? data : [];
    }
    catch (err) {
        console.error("Error obteniendo productos:", err);
        productos = [];
    }
    productosFiltrados = [...productos];
    renderProductos(productosFiltrados);
}
// ======================= CARGAR CATEGORÍAS =======================
async function cargarCategoriasNuevas() {
    try {
        const categorias = await apiGet("/categoria");
        if (!Array.isArray(categorias) || !categoryContainer)
            return;
        categorias.forEach((cat) => {
            var _a, _b;
            const nombre = (_a = cat.nombre) !== null && _a !== void 0 ? _a : "";
            if (!nombre)
                return;
            const existe = Array.from(categoryContainer.querySelectorAll(".category-card")).some((el) => { var _a; return ((_a = el.getAttribute("data-cat")) === null || _a === void 0 ? void 0 : _a.toLowerCase()) === nombre.toLowerCase(); });
            if (existe)
                return;
            const imgUrl = getCategoryImageUrl((_b = cat.urlImagen) !== null && _b !== void 0 ? _b : null, nombre);
            const card = document.createElement("div");
            card.className = "category-card";
            card.setAttribute("data-cat", nombre);
            card.innerHTML = `
        <img src="${imgUrl}" alt="${nombre}" onerror="this.src='${imagenesCategorias.default}'" />
        <div class="category-card-name">${nombre}</div>
      `;
            categoryContainer.appendChild(card);
        });
    }
    catch (err) {
        console.error("Error cargando categorías:", err);
    }
}
// ======================= EVENTOS =======================
categoryContainer === null || categoryContainer === void 0 ? void 0 : categoryContainer.addEventListener("click", (ev) => {
    const target = ev.target;
    const card = target.closest(".category-card");
    if (!card)
        return;
    const categoria = card.getAttribute("data-cat") || "";
    filtrarProductos((searchBig === null || searchBig === void 0 ? void 0 : searchBig.value.toLowerCase()) || "", categoria);
});
searchBig === null || searchBig === void 0 ? void 0 : searchBig.addEventListener("input", () => {
    filtrarProductos(searchBig.value.toLowerCase(), "");
});
// ======================= ORDENAR =======================
// Mostrar / ocultar menú
sortBtn === null || sortBtn === void 0 ? void 0 : sortBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    if (!sortMenu)
        return;
    sortMenu.style.display =
        sortMenu.style.display === "block" ? "none" : "block";
});
// Cerrar menú al clickear afuera
document.addEventListener("click", () => {
    if (sortMenu)
        sortMenu.style.display = "none";
});
// Click en opciones de orden
sortMenu === null || sortMenu === void 0 ? void 0 : sortMenu.addEventListener("click", (e) => {
    const target = e.target;
    const tipo = target.getAttribute("data-sort");
    if (!tipo)
        return;
    switch (tipo) {
        case "name-asc":
            productosFiltrados.sort((a, b) => a.nombre.localeCompare(b.nombre));
            break;
        case "name-desc":
            productosFiltrados.sort((a, b) => b.nombre.localeCompare(a.nombre));
            break;
        case "price-asc":
            productosFiltrados.sort((a, b) => Number(a.precio) - Number(b.precio));
            break;
        case "price-desc":
            productosFiltrados.sort((a, b) => Number(b.precio) - Number(a.precio));
            break;
    }
    sortMenu.style.display = "none";
    renderProductos(productosFiltrados);
});
// ======================= FILTRADO Y RENDER =======================
function filtrarProductos(busqueda, categoria) {
    const buscarNorm = normalize(busqueda);
    const catNorm = normalize(categoria);
    productosFiltrados = productos.filter((p) => {
        var _a;
        const nom = normalize(p.nombre);
        const desc = normalize(p.descripcion || "");
        const catVal = typeof p.categoria === "string" ? p.categoria : ((_a = p.categoria) === null || _a === void 0 ? void 0 : _a.nombre) || "";
        const cat = normalize(catVal);
        return ((nom.includes(buscarNorm) || desc.includes(buscarNorm)) &&
            (categoria === "Todo" || !categoria || cat.includes(catNorm)));
    });
    renderProductos(productosFiltrados);
}
function renderProductos(lista) {
    if (!productListEl || !productCountEl)
        return;
    productListEl.innerHTML = "";
    productCountEl.textContent = `${lista.length} productos`;
    lista.forEach((p) => {
        var _a;
        const img = getProductImageUrl(p);
        const stockNum = Number(p.stock);
        const disponible = p.disponible === true || p.disponible === "true" || p.disponible === 1;
        const inStock = disponible && stockNum > 0;
        const card = document.createElement("div");
        card.className = "product-card";
        card.innerHTML = `
      <div class="img-container">
        <img src="${img}" alt="${p.nombre}" />
        <span class="badge ${inStock ? "available" : "unavailable"}">
          ${inStock ? "Disponible" : "No Disponible"}
        </span>
      </div>
      <h4>${p.nombre}</h4>
      <p>${p.descripcion || ""}</p>
      <p><b>$${p.precio}</b></p>
      <button class="add-cart-btn" ${inStock ? "" : "disabled"}>🛒 Agregar</button>
    `;
        (_a = card.querySelector(".add-cart-btn")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", (e) => {
            e.stopPropagation();
            const carrito = loadCart();
            const item = carrito.find((i) => i.id === p.id);
            if (item)
                item.cantidad = (item.cantidad || 0) + 1;
            else
                carrito.push(Object.assign(Object.assign({}, p), { cantidad: 1 }));
            localStorage.setItem("carrito", JSON.stringify(carrito));
            updateCartBadgeSide();
            alert(`${p.nombre} agregado al carrito 🛒`);
        });
        card.addEventListener("click", () => {
            window.location.href = `/src/pages/store/productDetail/productDetail.html?id=${p.id}`;
        });
        productListEl.appendChild(card);
    });
}
// ======================= INIT =======================
(async () => {
    const user = await verificarSesion();
    if (!user)
        return;
    if (bannerGreeting) {
        bannerGreeting.textContent = (user === null || user === void 0 ? void 0 : user.email)
            ? `Hola, ${user.nombre}`
            : "Bienvenido a TM creation";
    }
    await cargarProductos();
    await cargarCategoriasNuevas();
    updateCartBadgeSide();
})();
