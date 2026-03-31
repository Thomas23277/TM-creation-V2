import { apiGet, apiPut } from "../../../utils/api";
const ordersContainer = document.getElementById("orders-container");
export async function loadOrders() {
    try {
        const pedidos = await apiGet("/pedidos");
        ordersContainer.innerHTML = "";
        if (!pedidos || pedidos.length === 0) {
            ordersContainer.innerHTML = `<p>No hay pedidos registrados.</p>`;
            return;
        }
        pedidos.forEach((pedido) => {
            var _a, _b, _c;
            const div = document.createElement("div");
            div.className = "order-card";
            const productosHTML = (_b = (_a = pedido.detalles) === null || _a === void 0 ? void 0 : _a.map((d) => {
                var _a, _b;
                return `
              <li>
                <strong>${(_b = (_a = d.producto) === null || _a === void 0 ? void 0 : _a.nombre) !== null && _b !== void 0 ? _b : "Producto"}</strong> — Cant: ${d.cantidad}
              </li>`;
            }).join("")) !== null && _b !== void 0 ? _b : "";
            div.innerHTML = `
        <h3>Pedido #${pedido.id}</h3>

        <p><strong>Cliente:</strong> ${(_c = pedido.usuarioNombre) !== null && _c !== void 0 ? _c : "—"}</p>

        <p><strong>Estado:</strong>
          <select id="estado-${pedido.id}">
            <option value="PENDIENTE" ${pedido.estado === "PENDIENTE" ? "selected" : ""}>Pendiente</option>

            <option value="EN_PROCESO" ${pedido.estado === "EN_PROCESO" ? "selected" : ""}>En proceso</option>

            <option value="EN_CAMINO" ${pedido.estado === "EN_CAMINO" ? "selected" : ""}>En camino</option>

            <option value="ENTREGADO" ${pedido.estado === "ENTREGADO" ? "selected" : ""}>Entregado</option>
          </select>
        </p>

        <p><strong>Total:</strong> $${Number(pedido.total).toFixed(2)}</p>

        <ul>${productosHTML}</ul>

        <button class="btn-primary save-btn" data-id="${pedido.id}">
          Guardar cambios
        </button>
      `;
            ordersContainer.appendChild(div);
        });
    }
    catch (e) {
        console.error("Error cargando pedidos:", e);
    }
}
// ✅ Delegación — evita listeners duplicados
document.addEventListener("click", async (e) => {
    const btn = e.target.closest(".save-btn");
    if (!btn)
        return;
    const id = btn.getAttribute("data-id");
    const estadoSel = document.getElementById(`estado-${id}`);
    const nuevoEstado = estadoSel.value;
    try {
        await apiPut(`/pedidos/${id}`, { estado: nuevoEstado });
        alert("Estado actualizado correctamente");
        // Recargar lista
        await loadOrders();
    }
    catch (err) {
        console.error("Error actualizando estado:", err);
        alert("No se pudo actualizar el pedido");
    }
});
