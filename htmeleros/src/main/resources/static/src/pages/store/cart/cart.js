"use strict";
document.addEventListener("DOMContentLoaded", () => {
    const API_URL = "http://localhost:8080";
    // =======================
    // CARRITO Y USUARIO
    // =======================
    let carrito = [];
    try {
        carrito = JSON.parse(localStorage.getItem("carrito") || "[]");
    }
    catch (_a) {
        carrito = [];
    }
    let user = null;
    try {
        user = JSON.parse(localStorage.getItem("currentUsuario") || "null");
    }
    catch (_b) {
        user = null;
    }
    // =======================
    // ELEMENTOS DEL DOM
    // =======================
    const btnComprar = document.getElementById("btnComprar");
    const btnVaciar = document.getElementById("btnVaciar");
    const contenedorCarrito = document.getElementById("carrito");
    const totalSpan = document.getElementById("total");
    const cartBadge = document.getElementById("cart-badge");
    const backBtn = document.getElementById("back-btn");
    const modal = document.getElementById("checkoutModal");
    const cancelarCompra = document.getElementById("cancelarCompra");
    const checkoutForm = document.getElementById("checkoutForm");
    const loader = document.getElementById("loaderOverlay");
    let isProcessing = false;
    function showLoader() {
        loader === null || loader === void 0 ? void 0 : loader.classList.add("active");
    }
    function hideLoader() {
        loader === null || loader === void 0 ? void 0 : loader.classList.remove("active");
    }
    /* ============================
          BADGE DEL CARRITO
    ============================ */
    function updateCartBadge() {
        const total = carrito.reduce((sum, item) => sum + (item.cantidad || 0), 0);
        if (!cartBadge)
            return;
        cartBadge.textContent = String(total);
        cartBadge.style.display = total > 0 ? "inline-block" : "none";
    }
    function saveCart() {
        localStorage.setItem("carrito", JSON.stringify(carrito));
        updateCartBadge();
    }
    /* ============================
          RENDER DEL CARRITO
    ============================ */
    function renderCarrito() {
        if (!contenedorCarrito)
            return;
        contenedorCarrito.innerHTML = "";
        let total = 0;
        if (carrito.length === 0) {
            contenedorCarrito.innerHTML = `<p>🛒 El carrito está vacío.</p>`;
            if (totalSpan)
                totalSpan.textContent = "Total: $0";
            updateCartBadge();
            return;
        }
        carrito.forEach((item, index) => {
            const cantidad = Number(item.cantidad) || 1;
            const subtotal = cantidad * item.precio;
            total += subtotal;
            const div = document.createElement("div");
            div.className = "cart-item";
            div.innerHTML = `
        <div class="cart-item-info">
          <h4>${item.nombre}</h4>
          <p>Precio: $${item.precio}</p>
          <p>Subtotal: $${subtotal}</p>
        </div>

        <div class="qty-controls">
          <button class="inc" data-index="${index}">+</button>
          <input class="cant" data-index="${index}" type="number" min="1" value="${cantidad}">
          <button class="dec" data-index="${index}">-</button>
        </div>

        <button class="remove-btn del" data-index="${index}">🗑</button>
      `;
            contenedorCarrito.appendChild(div);
        });
        if (totalSpan)
            totalSpan.textContent = `Total: $${total}`;
        document.querySelectorAll(".inc").forEach((btn) => {
            btn.addEventListener("click", () => {
                const i = Number(btn.dataset.index);
                carrito[i].cantidad++;
                saveCart();
                renderCarrito();
            });
        });
        document.querySelectorAll(".dec").forEach((btn) => {
            btn.addEventListener("click", () => {
                const i = Number(btn.dataset.index);
                carrito[i].cantidad = Math.max(1, carrito[i].cantidad - 1);
                saveCart();
                renderCarrito();
            });
        });
        document.querySelectorAll(".del").forEach((btn) => {
            btn.addEventListener("click", () => {
                const i = Number(btn.dataset.index);
                carrito.splice(i, 1);
                saveCart();
                renderCarrito();
            });
        });
        document.querySelectorAll(".cant").forEach((input) => {
            input.addEventListener("change", () => {
                const i = Number(input.dataset.index);
                const val = Math.max(1, parseInt(input.value) || 1);
                carrito[i].cantidad = val;
                saveCart();
                renderCarrito();
            });
        });
    }
    /* ============================
          VACIAR CARRITO
    ============================ */
    btnVaciar === null || btnVaciar === void 0 ? void 0 : btnVaciar.addEventListener("click", () => {
        if (confirm("¿Desea vaciar el carrito?")) {
            carrito = [];
            localStorage.removeItem("carrito");
            renderCarrito();
        }
    });
    /* ============================
          ABRIR MODAL CHECKOUT
    ============================ */
    btnComprar === null || btnComprar === void 0 ? void 0 : btnComprar.addEventListener("click", () => {
        if (!carrito.length) {
            alert("El carrito está vacío.");
            return;
        }
        if (!user || !user.id) {
            alert("Debes iniciar sesión antes de comprar.");
            window.location.href = "/src/pages/auth/login/login.html";
            return;
        }
        modal === null || modal === void 0 ? void 0 : modal.classList.add("active");
    });
    cancelarCompra === null || cancelarCompra === void 0 ? void 0 : cancelarCompra.addEventListener("click", () => {
        modal === null || modal === void 0 ? void 0 : modal.classList.remove("active");
    });
    /* ============================
          ENVIAR PEDIDO FINAL
    ============================ */
    checkoutForm === null || checkoutForm === void 0 ? void 0 : checkoutForm.addEventListener("submit", async (e) => {
        var _a;
        e.preventDefault();
        if (isProcessing)
            return;
        isProcessing = true;
        if (!user || !user.id) {
            alert("Usuario no válido.");
            isProcessing = false;
            return;
        }
        const nombre = document.getElementById("nombre").value;
        const email = document.getElementById("email").value;
        const telefono = document.getElementById("telefono").value;
        const direccion = document.getElementById("direccion").value;
        const metodoPago = (_a = document.querySelector("input[name='pago']:checked")) === null || _a === void 0 ? void 0 : _a.value;
        if (!metodoPago) {
            alert("Selecciona un método de pago.");
            isProcessing = false;
            return;
        }
        showLoader();
        try {
            const detalles = carrito.map((item) => ({
                productoId: item.id,
                cantidad: item.cantidad || 1,
            }));
            const total = carrito.reduce((acc, item) => acc + item.precio * (item.cantidad || 1), 0);
            const pedidoDTO = {
                usuarioId: user.id,
                nombre,
                email,
                telefono,
                direccionEntrega: direccion,
                metodoPago,
                estado: "PENDIENTE",
                total,
                detalles,
            };
            const res = await fetch(`${API_URL}/api/pedidos/checkout`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(pedidoDTO),
            });
            if (!res.ok) {
                const msg = await res.text();
                hideLoader();
                alert("Error al crear el pedido: " + msg);
                isProcessing = false;
                return;
            }
            hideLoader();
            alert("Pedido enviado correctamente. Revisa tu email.");
            carrito = [];
            localStorage.removeItem("carrito");
            modal === null || modal === void 0 ? void 0 : modal.classList.remove("active");
            renderCarrito();
            updateCartBadge();
        }
        catch (err) {
            hideLoader();
            console.error("Error al procesar la compra:", err);
            alert("Error inesperado al procesar la compra.");
        }
        isProcessing = false;
    });
    /* ============================
          BACK
    ============================ */
    backBtn === null || backBtn === void 0 ? void 0 : backBtn.addEventListener("click", () => {
        if (document.referrer)
            history.back();
        else
            window.location.href = "/src/pages/store/home/home.html";
    });
    renderCarrito();
    updateCartBadge();
});
