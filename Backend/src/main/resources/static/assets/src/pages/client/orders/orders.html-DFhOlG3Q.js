import{h as k}from"../../../../api-BwTScyzt.js";async function T(a,n={}){const i=await fetch(`https://tm-creation-v2.onrender.com${a}`,{credentials:"include",headers:{Accept:"application/json",...n.body&&!(n.body instanceof FormData)?{"Content-Type":"application/json"}:{},...n.headers||{}},...n});if(i.status===401)throw k(),new Error("UNAUTHORIZED");if(!i.ok)throw new Error(`HTTP ${i.status}: ${await i.text()}`);return i.status===204?{}:i.json()}function M(a){if(!a||a==="null")return"/assets/food/default.jpeg";if((a.includes("/uploads/http")||a.includes("/uploads/productos/http"))&&(a=a.replace("/uploads/productos/","").replace("/uploads/","")),a.includes("cloudinary")||a.includes("res.cloudinary")||a.includes("://"))return a;if(a.includes("productos_"))return`https://res.cloudinary.com/dg5kaiz2s/image/upload/v1/tmcreation/productos/${a}`;const n=a.replace(/^\/+/,"");return`https://tm-creation-v2.onrender.com/${n.startsWith("uploads")?n:"uploads/productos/"+n}`}const S={PENDIENTE:"Pendiente",EN_PROCESO:"En proceso",EN_CAMINO:"En camino",ENTREGADO:"Entregado",CANCELADO:"Cancelado"},R={PENDIENTE:"⏳",EN_PROCESO:"🔧",EN_CAMINO:"🚚",ENTREGADO:"✅",CANCELADO:"❌"};document.addEventListener("DOMContentLoaded",async()=>{const a=document.getElementById("logout-btn"),n=document.getElementById("back-btn"),v=document.getElementById("mensaje"),i=document.getElementById("orders-container"),A=document.getElementById("username-tag");if(!v||!i||!A)return;const m=v,f=i,O=A;n&&n.addEventListener("click",()=>{document.referrer?history.back():window.location.href="/src/pages/store/home/home.html"}),a==null||a.addEventListener("click",()=>{localStorage.removeItem("currentUsuario"),window.location.href="/src/pages/auth/login/login.html"});const L=localStorage.getItem("currentUsuario");if(!L){m.textContent="Debes iniciar sesión para ver tus pedidos.",setTimeout(()=>{window.location.href="/src/pages/auth/login/login.html"},1500);return}let h;try{h=JSON.parse(L)}catch{m.textContent="Error al leer los datos del usuario.";return}if(!h.id){m.textContent="Usuario inválido.";return}O.textContent=h.nombre||"Usuario";try{const e=await T(`/api/pedidos/usuario/${h.id}`);if(!e||e.length===0){m.textContent="No tienes pedidos registrados aún.",f.innerHTML="";return}const t=[...e].sort((r,p)=>new Date(p.fecha).getTime()-new Date(r.fecha).getTime());P(t);const c=new URLSearchParams(window.location.search).get("orderId");c&&U(`Pedido N°${c} enviado, revisa tu correo!`)}catch(e){if(console.error("Error obteniendo pedidos:",e),e.message==="UNAUTHORIZED"){m.textContent="Sesión expirada o sin permisos.",localStorage.removeItem("currentUsuario"),setTimeout(()=>{window.location.href="/src/pages/auth/login/login.html"},1500);return}m.textContent="No se pudieron cargar tus pedidos."}function P(e){f.innerHTML="",m.textContent="",e.forEach(t=>{var E;const g=new Date(t.fecha),c=t.estado||"PENDIENTE",r=`${R[c]||""} ${S[c]||c}`,p=c.toLowerCase(),$=new Intl.NumberFormat("es-AR",{style:"currency",currency:"ARS"}).format(t.total??0),y=(t.detalles||[]).slice(0,3).map(o=>`<span class="order-item-preview">${o.nombreTamano?`${o.productoNombre} (${o.nombreTamano})`:o.productoNombre||"Producto"} x${o.cantidad}</span>`).join(", "),w=(((E=t.detalles)==null?void 0:E.length)||0)>3?` y +${t.detalles.length-3} más`:"",u=document.createElement("div");u.className=`order-card ${p}`,u.dataset.orderId=String(t.id),u.innerHTML=`
        <div class="order-card-header">
          <span class="order-status ${p}">${r}</span>
          <span class="order-card-total">${$}</span>
        </div>
        <h3 class="order-card-title">Pedido #${t.id}</h3>
        <p class="order-card-date">${g.toLocaleDateString("es-AR",{day:"numeric",month:"long",year:"numeric",hour:"2-digit",minute:"2-digit"})}</p>
        <p class="order-card-items">${y}${w}</p>
      `,u.addEventListener("click",()=>x(t)),f.appendChild(u)})}function x(e){var I;const t=document.getElementById("order-detail-modal");t&&t.remove();const g=new Date(e.fecha),c=e.fechaActualizacion?new Date(e.fechaActualizacion):null,r=e.estado||"PENDIENTE",p=`${R[r]||""} ${S[r]||r}`,$=r.toLowerCase(),D=new Intl.NumberFormat("es-AR",{style:"currency",currency:"ARS"}).format(e.total??0),y=r!=="CANCELADO"&&r!=="ENTREGADO",w={transferencia:"Transferencia bancaria",efectivo:"Efectivo",mercadopago:"Mercado Pago",tarjeta:"Tarjeta de crédito/débito"},u=e.metodoPago?w[e.metodoPago.toLowerCase()]||e.metodoPago:"—",E=(e.detalles||[]).map(s=>{const l=M(s.productoImagen),N=s.nombreTamano?`${s.productoNombre} (${s.nombreTamano})`:s.productoNombre||"Producto",C=new Intl.NumberFormat("es-AR",{style:"currency",currency:"ARS"}).format(s.precioUnitario),j=new Intl.NumberFormat("es-AR",{style:"currency",currency:"ARS"}).format(s.subtotal??s.precioUnitario*s.cantidad);return`
        <div class="modal-item">
          <img src="${l}" alt="${s.productoNombre||""}" onerror="this.src='/assets/food/default.jpeg';" />
          <div class="modal-item-info">
            <h4>${N}</h4>
            <p class="modal-item-price">${C} x ${s.cantidad}</p>
            <p class="modal-item-subtotal">Subtotal: ${j}</p>
          </div>
        </div>
      `}).join(""),o=document.createElement("div");o.className="modal-overlay",o.id="order-detail-modal",o.innerHTML=`
      <div class="modal-content">
        <button class="modal-close">&times;</button>

        <div class="modal-header">
          <span class="order-status ${$}">${p}</span>
          <h2>Pedido #${e.id}</h2>
        </div>

        <div class="modal-meta">
          <div class="meta-row">
            <span class="meta-label">Fecha</span>
            <span class="meta-value">${g.toLocaleDateString("es-AR",{day:"numeric",month:"long",year:"numeric",hour:"2-digit",minute:"2-digit"})}</span>
          </div>
          ${c?`
          <div class="meta-row">
            <span class="meta-label">Última actualización</span>
            <span class="meta-value">${c.toLocaleDateString("es-AR",{day:"numeric",month:"long",year:"numeric",hour:"2-digit",minute:"2-digit"})}</span>
          </div>`:""}
          <div class="meta-row">
            <span class="meta-label">Método de pago</span>
            <span class="meta-value">${u}</span>
          </div>
          ${e.direccionEntrega?`
          <div class="meta-row">
            <span class="meta-label">Dirección de entrega</span>
            <span class="meta-value">${e.direccionEntrega}</span>
          </div>`:""}
          ${e.telefonoContacto?`
          <div class="meta-row">
            <span class="meta-label">Teléfono de contacto</span>
            <span class="meta-value">${e.telefonoContacto}</span>
          </div>`:""}
          <div class="meta-row">
            <span class="meta-label">Contacto para reportes</span>
            <span class="meta-value"><a href="mailto:tmcreation233@gmail.com" class="contact-link">tmcreation233@gmail.com</a></span>
          </div>
        </div>

        <h3 class="modal-section-title">Productos</h3>
        <div class="modal-items">${E}</div>

        <div class="modal-total">
          <span>Total</span>
          <span>${D}</span>
        </div>

        ${y?`
        <div class="modal-actions">
          <button class="btn-cancel-order" data-id="${e.id}">Eliminar pedido</button>
        </div>`:`
        <div class="modal-actions">
          <p class="modal-finished">Este pedido ya fue ${r==="ENTREGADO"?"entregado":"cancelado"}.</p>
        </div>`}
      </div>
    `,document.body.appendChild(o),requestAnimationFrame(()=>o.classList.add("open")),(I=o.querySelector(".modal-close"))==null||I.addEventListener("click",()=>b(o)),o.addEventListener("click",s=>{s.target===o&&b(o)});const d=o.querySelector(".btn-cancel-order");d&&d.addEventListener("click",async()=>{const s=d.dataset.id;if(!(!s||!confirm("¿Estás seguro de eliminar este pedido? Esta acción no se puede deshacer.")))try{d.disabled=!0,d.textContent="Eliminando...",await T(`/api/pedidos/${s}`,{method:"DELETE"}),b(o);const l=await T(`/api/pedidos/usuario/${h.id}`);l!=null&&l.length&&P([...l].sort((N,C)=>new Date(C.fecha).getTime()-new Date(N.fecha).getTime()))}catch(l){alert(`No se pudo eliminar el pedido: ${l.message}`),d.disabled=!1,d.textContent="Eliminar pedido"}})}function b(e){e.classList.remove("open"),setTimeout(()=>e.remove(),300)}function U(e){const t=document.createElement("div");t.className="order-toast",t.textContent=e,document.body.appendChild(t),t.classList.add("show"),navigator.vibrate&&navigator.vibrate(200),new Audio("/assets/success-sound.mp3").play().catch(()=>{}),setTimeout(()=>{t.classList.remove("show"),document.body.contains(t)&&document.body.removeChild(t)},2500)}});
