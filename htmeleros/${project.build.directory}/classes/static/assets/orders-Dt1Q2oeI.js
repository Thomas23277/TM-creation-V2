import{b as c,c as i}from"./api-Cd49zLLy.js";const r=document.getElementById("orders-container");async function l(){try{const e=await c("/pedidos");if(r.innerHTML="",!e||e.length===0){r.innerHTML="<p>No hay pedidos registrados.</p>";return}e.forEach(t=>{var a;const o=document.createElement("div");o.className="order-card";const s=((a=t.detalles)==null?void 0:a.map(n=>{var d;return`
              <li>
                <strong>${((d=n.producto)==null?void 0:d.nombre)??"Producto"}</strong> — Cant: ${n.cantidad}
              </li>`}).join(""))??"";o.innerHTML=`
        <h3>Pedido #${t.id}</h3>

        <p><strong>Cliente:</strong> ${t.usuarioNombre??"—"}</p>

        <p><strong>Estado:</strong>
          <select id="estado-${t.id}">
            <option value="PENDIENTE" ${t.estado==="PENDIENTE"?"selected":""}>Pendiente</option>

            <option value="EN_PROCESO" ${t.estado==="EN_PROCESO"?"selected":""}>En proceso</option>

            <option value="EN_CAMINO" ${t.estado==="EN_CAMINO"?"selected":""}>En camino</option>

            <option value="ENTREGADO" ${t.estado==="ENTREGADO"?"selected":""}>Entregado</option>
          </select>
        </p>

        <p><strong>Total:</strong> $${Number(t.total).toFixed(2)}</p>

        <ul>${s}</ul>

        <button class="btn-primary save-btn" data-id="${t.id}">
          Guardar cambios
        </button>
      `,r.appendChild(o)})}catch(e){console.error("Error cargando pedidos:",e)}}document.addEventListener("click",async e=>{const t=e.target.closest(".save-btn");if(!t)return;const o=t.getAttribute("data-id"),a=document.getElementById(`estado-${o}`).value;try{await i(`/pedidos/${o}`,{estado:a}),alert("Estado actualizado correctamente"),await l()}catch(n){console.error("Error actualizando estado:",n),alert("No se pudo actualizar el pedido")}});export{l as loadOrders};
