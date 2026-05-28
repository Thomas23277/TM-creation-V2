import{h as y}from"../../../../api-BwTScyzt.js";const c="https://tm-creation-v2.onrender.com";async function m(e,o={}){const a=await fetch(e,{...o,credentials:"include"});if(a.status===401||a.status===403)throw y(),new Error("UNAUTHORIZED");return a}document.addEventListener("DOMContentLoaded",()=>{const e=document.querySelectorAll(".admin-tab"),o=document.querySelectorAll(".admin-section");e.forEach(a=>{a.addEventListener("click",()=>{var r;const t=a.getAttribute("data-tab");e.forEach(n=>n.classList.remove("active")),o.forEach(n=>n.classList.remove("active")),a.classList.add("active"),(r=document.getElementById(`${t}-section`))==null||r.classList.add("active")})}),h(),E(),f()});async function h(){const e=document.getElementById("reviews-list");if(e)try{const o=await fetch(`${c}/api/resenas`);if(!o.ok)throw new Error("Error al cargar reseñas");const a=await o.json();if(a.length===0){e.innerHTML='<div class="empty-state">No hay reseñas aún</div>';return}e.innerHTML=`
      <table class="reviews-table">
        <thead>
          <tr>
            <th>Usuario</th>
            <th>Producto</th>
            <th>Calificación</th>
            <th>Comentario</th>
            <th>Fecha</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          ${a.map(t=>{var r,n;return`
            <tr>
              <td>${((r=t.usuario)==null?void 0:r.nombre)||"Usuario"}</td>
              <td>${((n=t.producto)==null?void 0:n.nombre)||"Producto"}</td>
              <td><span class="star-display">${"★".repeat(t.estrellas)}${"☆".repeat(5-t.estrellas)}</span></td>
              <td>${t.comentario||"-"}</td>
              <td>${new Date(t.fecha).toLocaleDateString("es-AR")}</td>
              <td>
                <button class="btn-delete" data-id="${t.id}">Eliminar</button>
              </td>
            </tr>
          `}).join("")}
        </tbody>
      </table>
    `,e.querySelectorAll(".btn-delete").forEach(t=>{t.addEventListener("click",async r=>{const n=r.target.dataset.id;confirm("¿Eliminar esta reseña?")&&(await m(`${c}/api/resenas/${n}`,{method:"DELETE"}),h())})}),e.querySelectorAll(".btn-reply").forEach(t=>{t.addEventListener("click",async r=>{const n=r.target,p=n.dataset.usuario,u=n.dataset.producto,s=n.dataset.estrellas,d=n.dataset.comentario,i=`Nueva reseña de ${p} para ${u}\\n\\n⭐ Estrella(s): ${s}\\n\\n💬 Comentario: ${d||"Sin comentario"}\\n\\n¿Querés responder?`;confirm(i)&&(window.location.href="/src/pages/auth/login/login.html?redirect= responder")})})}catch(o){console.error(o),e.innerHTML='<div class="empty-state">Error al cargar reseñas</div>'}}let l=null;function f(){const e=document.getElementById("add-promo-btn"),o=document.getElementById("promo-form"),a=document.getElementById("cancel-promo-btn"),t=document.getElementById("save-promo-btn");e==null||e.addEventListener("click",()=>{l=null,document.getElementById("form-title").textContent="Nueva Promoción",document.getElementById("promo-titulo").value="",document.getElementById("promo-enlace").value="",document.getElementById("promo-orden").value="0",document.getElementById("promo-imagen").value="",o==null||o.classList.add("show")}),a==null||a.addEventListener("click",()=>{o==null||o.classList.remove("show")}),t==null||t.addEventListener("click",async()=>{var d;const r=document.getElementById("promo-titulo").value,n=document.getElementById("promo-enlace").value,p=parseInt(document.getElementById("promo-orden").value)||0,u=(d=document.getElementById("promo-imagen").files)==null?void 0:d[0];if(!r){alert("El título es obligatorio");return}const s=new FormData;s.append("titulo",r),s.append("urlEnlace",n),s.append("orden",String(p)),u&&s.append("imagen",u);try{const i=l?`${c}/api/promociones/${l}`:`${c}/api/promociones`,g=await m(i,{method:l?"PUT":"POST",body:s});if(!g.ok){const v=await g.text();throw new Error(v)}o==null||o.classList.remove("show"),l=null,document.getElementById("form-title").textContent="Nueva Promoción",E()}catch(i){console.error(i),alert("Error al guardar promoción: "+i.message)}})}async function E(){const e=document.getElementById("promos-list");if(e)try{const o=await m(`${c}/api/promociones`);if(!o.ok)throw new Error("Error al cargar promociones");const a=await o.json();if(a.length===0){e.innerHTML='<div class="empty-state">No hay promociones aún. Crea una nueva!</div>';return}e.innerHTML=a.sort((t,r)=>t.orden-r.orden).map(t=>`
      <div class="promo-card">
        <img src="${$(t.urlImagen)}" alt="${t.titulo}" onerror="this.src='https://picsum.photos/300/150'">
        <h4>${t.titulo}</h4>
        <div class="promo-actions">
          <button class="btn-toggle ${t.activo?"active":"inactive"}" data-id="${t.id}">
            ${t.activo?"✓ Activa":"○ Inactiva"}
          </button>
          <button class="btn-edit" data-id="${t.id}" data-orden="${t.orden}">Editar</button>
          <button class="btn-delete" data-delete-id="${t.id}">Eliminar</button>
        </div>
      </div>
    `).join(""),e.querySelectorAll(".btn-toggle").forEach(t=>{t.addEventListener("click",async r=>{const n=r.target.dataset.id;await m(`${c}/api/promociones/${n}/toggle`,{method:"PATCH"}),E()})}),e.querySelectorAll(".btn-edit").forEach(t=>{t.addEventListener("click",async r=>{var d;const n=r.target.dataset.id,p=r.target.dataset.orden,s=(await(await m(`${c}/api/promociones`)).json()).find(i=>i.id===parseInt(n));s&&(l=parseInt(n),document.getElementById("form-title").textContent="Editar Promoción",document.getElementById("promo-titulo").value=s.titulo,document.getElementById("promo-enlace").value=s.urlEnlace||"",document.getElementById("promo-orden").value=String(s.orden),(d=document.getElementById("promo-form"))==null||d.classList.add("show"))})}),e.querySelectorAll(".btn-delete").forEach(t=>{t.addEventListener("click",async r=>{const n=r.target.dataset.deleteId;confirm("¿Eliminar esta promoción?")&&(await m(`${c}/api/promociones/${n}`,{method:"DELETE"}),E())})})}catch(o){console.error(o),e.innerHTML='<div class="empty-state">Error al cargar promociones</div>'}}function $(e){return e?e.includes("cloudinary")||e.includes("://")?e:e.includes("promociones_")?`https://res.cloudinary.com/dg5kaiz2s/image/upload/v1/tmcreation/promociones/${e}`:`https://tm-creation-v2.onrender.com/uploads/promociones/${e}`:"https://picsum.photos/300/150"}
