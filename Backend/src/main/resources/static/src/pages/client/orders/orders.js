"use strict";
/* ============================
   ==== IMÁGENES PRODUCTOS ====
============================ */
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
/* ============================
        API FETCH
============================ */
async function apiFetch(path, opts = {}) {
    const API_BASE = "http://localhost:8080";
    const res = await fetch(`${API_BASE}${path}`, opts);
    if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${await res.text()}`);
    }
    return res.json();
}
/* ============================
        ESTADOS VISUALES
============================ */
const estadosVisuales = {
    PENDIENTE: "⏳ Pendiente",
    EN_PROCESO: "🔧 En proceso",
    EN_CAMINO: "🚚 En camino",
    ENTREGADO: "✅ Entregado",
};
/* ============================
        DOM READY
============================ */
document.addEventListener("DOMContentLoaded", async () => {
    const logoutBtn = document.getElementById("logout-btn");
    const mensajeEl = document.getElementById("mensaje");
    const ordersContainer = document.getElementById("orders-container");
    const usernameEl = document.getElementById("username-tag");
    // Validación inicial
    if (!mensajeEl || !ordersContainer || !usernameEl) {
        console.error("Elementos del DOM no encontrados");
        return;
    }
    // 🔒 A PARTIR DE ACÁ → NO NULL
    const safeMensajeEl = mensajeEl;
    const safeOrdersContainer = ordersContainer;
    const safeUsernameEl = usernameEl;
    /* ---------------- LOGOUT ---------------- */
    logoutBtn === null || logoutBtn === void 0 ? void 0 : logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("foodstore_user");
        localStorage.removeItem("user");
        localStorage.removeItem("usuario");
        localStorage.removeItem("currentUsuario");
        window.location.href = "../../auth/login/login.html";
    });
    /* ---------------- LEER USUARIO ---------------- */
    const rawUser = localStorage.getItem("foodstore_user") ||
        localStorage.getItem("user") ||
        localStorage.getItem("usuario") ||
        localStorage.getItem("currentUsuario");
    if (!rawUser) {
        safeMensajeEl.textContent =
            "⚠️ Debes iniciar sesión para ver tus pedidos.";
        return;
    }
    let user;
    try {
        user = JSON.parse(rawUser);
    }
    catch (_a) {
        safeMensajeEl.textContent =
            "⚠️ Error al leer los datos del usuario.";
        return;
    }
    if (!user.id) {
        safeMensajeEl.textContent = "⚠️ Usuario inválido.";
        return;
    }
    safeUsernameEl.textContent = user.nombre || "Usuario";
    /* ---------------- PEDIDOS ---------------- */
    try {
        const pedidos = await apiFetch(`/api/pedidos/usuario/${user.id}`);
        if (!pedidos || pedidos.length === 0) {
            safeMensajeEl.textContent =
                "🧾 No tienes pedidos registrados aún.";
            safeOrdersContainer.innerHTML = "";
            return;
        }
        renderPedidos(pedidos);
    }
    catch (e) {
        console.error("Error obteniendo pedidos:", e);
        safeMensajeEl.textContent =
            "⚠️ No se pudieron cargar tus pedidos.";
    }
    /* ---------------- RENDER ---------------- */
    function renderPedidos(pedidos) {
        safeOrdersContainer.innerHTML = "";
        safeMensajeEl.textContent = "";
        pedidos.forEach((p) => {
            const fecha = new Date(p.fecha);
            const estadoLegible = (p.estado && estadosVisuales[p.estado]) ||
                `⏳ ${p.estado || "Pendiente"}`;
            const detallesHTML = (p.detalles || [])
                .map((d) => {
                if (!d.producto)
                    return "";
                const imagen = imagenesProductos[d.producto.nombre] ||
                    d.producto.imagen ||
                    `https://picsum.photos/seed/${encodeURIComponent(d.producto.nombre)}/80`;
                return `
            <div class="order-item">
              <img src="${imagen}" alt="${d.producto.nombre}" />
              <div>
                <h4>${d.producto.nombre}</h4>
                <p>Cantidad: ${d.cantidad}</p>
                <p>$${d.precioUnitario} c/u</p>
              </div>
            </div>
          `;
            })
                .join("");
            const card = document.createElement("div");
            card.className = "order-card";
            card.innerHTML = `
        <span class="order-status">${estadoLegible}</span>
        <h3>Pedido #${p.id}</h3>
        <p><b>${fecha.toLocaleDateString("es-AR")} — ${fecha.toLocaleTimeString("es-AR")}</b></p>
        <div class="order-items">${detallesHTML}</div>
        <h3 style="margin-top:14px">
          Total: <span style="color:#ff3b00">$${p.total}</span>
        </h3>
      `;
            safeOrdersContainer.appendChild(card);
        });
    }
});
