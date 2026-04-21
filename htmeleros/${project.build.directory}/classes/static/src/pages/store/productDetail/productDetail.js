"use strict";
document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    /* ========= IMÁGENES MAPA ========= */
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
    /* ========= FUNCIÓN CENTRALIZADA DE IMAGEN ========= */
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
    /* ========= ELEMENTOS ========= */
    const imgEl = document.getElementById("product-img");
    const nameEl = document.getElementById("product-name");
    const descEl = document.getElementById("product-desc");
    const priceEl = document.getElementById("product-price");
    const statusEl = document.getElementById("product-status");
    const quantityEl = document.getElementById("quantity");
    const addBtn = document.getElementById("add-to-cart-btn");
    const messageEl = document.getElementById("message");
    const backBtn = document.getElementById("back-btn");
    const cartBadge = document.getElementById("cart-badge");
    if (backBtn)
        backBtn.addEventListener("click", () => window.history.back());
    /* ========= CARRITO ========= */
    function loadCart() {
        try {
            return JSON.parse(localStorage.getItem("carrito") || "[]");
        }
        catch (_a) {
            return [];
        }
    }
    function saveCart(c) {
        localStorage.setItem("carrito", JSON.stringify(c));
        updateBadge();
    }
    function updateBadge() {
        if (!cartBadge)
            return;
        const c = loadCart();
        const t = c.reduce((s, i) => s + (Number(i.cantidad) || 0), 0);
        cartBadge.textContent = String(t);
        cartBadge.style.display = t > 0 ? "inline-block" : "none";
    }
    if (!id) {
        if (messageEl)
            messageEl.textContent = "Producto no encontrado";
        return;
    }
    /* ========= CARGAR PRODUCTO ========= */
    try {
        const r = await fetch(`http://localhost:8080/api/productos/${id}`);
        if (!r.ok)
            throw new Error("no product");
        const product = await r.json();
        const stock = Number(product.stock) || 0;
        const disponible = Boolean(product.disponible);
        const imgSrc = getProductImageUrl(product);
        if (imgEl)
            imgEl.src = imgSrc;
        if (nameEl)
            nameEl.textContent = product.nombre;
        if (descEl)
            descEl.textContent = product.descripcion || "";
        if (priceEl)
            priceEl.textContent = `$${product.precio.toLocaleString()}`;
        /* ========= ESTADO DEL PRODUCTO ========= */
        if (statusEl && addBtn) {
            statusEl.classList.remove("available", "unavailable");
            if (stock === 0) {
                /* ---- CASO 1: SIN STOCK ---- */
                statusEl.textContent = "Sin stock";
                statusEl.classList.add("unavailable");
                addBtn.disabled = true;
            }
            else if (!disponible) {
                /* ---- CASO 2: HAY STOCK PERO NO DISPONIBLE ---- */
                statusEl.textContent = `No Disponible (${stock} unidades)`;
                statusEl.classList.add("unavailable");
                addBtn.disabled = true;
            }
            else {
                /* ---- CASO 3: DISPONIBLE NORMAL ---- */
                statusEl.textContent = `Disponible (${stock} unidades)`;
                statusEl.classList.add("available");
            }
        }
        /* ====== AGREGAR AL CARRITO ====== */
        if (addBtn) {
            addBtn.addEventListener("click", () => {
                if (!quantityEl || !messageEl)
                    return;
                const cantidad = Math.max(1, Number(quantityEl.value) || 1);
                if (cantidad > stock) {
                    messageEl.textContent = `No hay suficiente stock. Disponible: ${stock}`;
                    return;
                }
                const cart = loadCart();
                const existing = cart.find((it) => it.id === product.id);
                if (existing) {
                    if (existing.cantidad + cantidad > stock) {
                        messageEl.textContent = `Stock insuficiente. Solo quedan ${stock}`;
                        return;
                    }
                    existing.cantidad += cantidad;
                }
                else {
                    cart.push({
                        id: product.id,
                        nombre: product.nombre,
                        precio: product.precio,
                        imagen: imgSrc,
                        cantidad,
                        stock,
                    });
                }
                saveCart(cart);
                messageEl.textContent = "✅ Producto agregado al carrito.";
                quantityEl.value = "1";
            });
        }
        updateBadge();
    }
    catch (e) {
        console.error(e);
        if (messageEl)
            messageEl.textContent = "Error al cargar producto";
    }
});
