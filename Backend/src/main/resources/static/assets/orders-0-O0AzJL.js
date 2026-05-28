import{a as f,d as p}from"./api-BwTScyzt.js";let u=[];function b(){u.forEach(clearInterval),u=[]}function m(a){switch(a){case"PENDIENTE":return"badge badge-pendiente";case"EN_PROCESO":case"EN_CAMINO":return"badge badge-aprobado";case"ENTREGADO":return"badge badge-entregado";case"CANCELADO":return"badge badge-cancelado";default:return"badge"}}function N(a){const t=Math.floor(a/36e5),e=Math.floor(a%(1e3*60*60)/(1e3*60)),o=Math.floor(a%(1e3*60)/1e3);return`${t}h ${e}m ${o}s`}function v(a,t){const e=new Date(a),o=new Date(e.getTime()+72*60*60*1e3),n=()=>{const r=new Date,i=o.getTime()-r.getTime();if(i<=0)return t.innerHTML='<span class="countdown-expired">Expirado</span>',!0;const s=i/(1e3*60*60);let d="countdown";return s<=6?d+=" countdown-danger":s<=24&&(d+=" countdown-warning"),t.innerHTML=`
      <span class="${d}">⏳ ${N(i)}</span>
      <div class="countdown-msg" style="font-size: 0.8rem; color: #64748b; margin-top: 4px;">
        Se cancelará automáticamente si no cambia el estado.
      </div>
    `,!1};if(!n()){const r=window.setInterval(()=>{n()&&clearInterval(r)},1e3);u.push(r)}}async function $(){const a=document.getElementById("orders-container");if(a)try{const t=await f("/pedidos");if(b(),a.innerHTML="",!t||t.length===0){a.innerHTML='<p class="no-data" style="padding: 2rem; text-align: center; color: #64748b;">No hay pedidos registrados.</p>';return}t.reverse(),t.forEach(e=>{var E;const o=document.createElement("div");o.className="order-card";const n=((E=e.detalles)==null?void 0:E.map(l=>{var g;return`
        <li style="margin-bottom: 8px; border-bottom: 1px dashed #e2e8f0; padding-bottom: 8px;">
          <strong>${((g=l.producto)==null?void 0:g.nombre)??"Producto"}</strong> — Cant: ${l.cantidad}
        </li>`}).join(""))??"",c=e.estado==="CANCELADO",r=e.estado==="ENTREGADO",i=c||r?"disabled":"";if(o.innerHTML=`
        <div class="order-header">
          <h3>Pedido #${e.id}</h3>
          <span class="${m(e.estado)}" id="badge-${e.id}">${e.estado}</span>
        </div>

        <p style="margin-bottom: 10px;"><strong>Cliente:</strong> ${e.usuarioNombre??"—"}</p>

        ${e.estado==="PENDIENTE"&&e.fecha?`<div id="contador-${e.id}" class="contador-wrap" style="background: #f8fafc; padding: 10px; border-radius: 8px; border: 1px solid #e2e8f0; margin-bottom: 15px;"></div>`:""}

        <p style="font-size: 1.1rem; color: #0f172a; margin-bottom: 15px;">
          <strong>Total:</strong> $${Number(e.total||0).toFixed(2)}
        </p>

        <ul class="order-products" style="list-style: none; padding: 0; margin-bottom: 20px; color: #475569;">
          ${n}
        </ul>

        <div class="order-actions">
          <select id="estado-${e.id}" ${i} class="order-select">
            <option value="PENDIENTE" ${e.estado==="PENDIENTE"?"selected":""}>PENDIENTE</option>
            <option value="EN_PROCESO" ${e.estado==="EN_PROCESO"?"selected":""}>EN PROCESO</option>
            <option value="EN_CAMINO" ${e.estado==="EN_CAMINO"?"selected":""}>EN CAMINO</option>
            <option value="ENTREGADO" ${e.estado==="ENTREGADO"?"selected":""}>ENTREGADO</option>
            <option value="CANCELADO" ${e.estado==="CANCELADO"?"selected":""}>CANCELADO</option>
          </select>

          ${!c&&!r?`<button class="btn-primary save-order-btn" data-id="${e.id}">Guardar cambios</button>`:'<div class="status-fixed" style="color: #10b981; font-weight: bold; padding: 0.8rem;">✔️ Gestión finalizada</div>'}
        </div>
      `,a.appendChild(o),e.estado==="PENDIENTE"&&e.fecha){const l=document.getElementById(`contador-${e.id}`);l&&v(e.fecha,l)}const s=document.getElementById(`estado-${e.id}`),d=document.getElementById(`badge-${e.id}`);s&&d&&s.addEventListener("change",()=>{d.className=m(s.value),d.textContent=s.value})})}catch(t){console.error("Error cargando pedidos:",t),a.innerHTML='<p class="no-data" style="color: red; padding: 2rem;">Error al cargar los pedidos. Por favor, recarga la página.</p>'}}window._orderEventsAttached||(document.addEventListener("click",async a=>{const t=a.target.closest(".save-order-btn");if(!t)return;const e=t.getAttribute("data-id"),o=document.getElementById(`estado-${e}`);if(!e||!o)return;const n=o.value;if(!(n==="CANCELADO"&&!confirm("¿Seguro que deseas cancelar este pedido? El cliente será notificado.")))try{t.disabled=!0,t.textContent="Guardando...",await p(`/pedidos/${e}/estado`,{estado:n}),alert(`✅ Pedido #${e} actualizado a ${n}.`),await $()}catch(c){console.error("Error:",c),alert("❌ Error: No se pudo actualizar el estado. Revisa tu conexión.")}finally{t&&(t.disabled=!1,t.textContent="Guardar cambios")}}),window._orderEventsAttached=!0);export{$ as loadOrders};
