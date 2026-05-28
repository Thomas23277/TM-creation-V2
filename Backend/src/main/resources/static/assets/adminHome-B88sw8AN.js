import{c as K,l as Q}from"./auth-ieWdpgp-.js";import{a as g,b as k,d as q,c as S}from"./api-BwTScyzt.js";let E=[],y=[],x=[],$=new Set,b=new Set;function A(){var t;const e=document.getElementById("variants-container");if(!e)return;const n=(t=document.getElementById("colores-activo-toggle"))==null?void 0:t.checked;e.innerHTML=E.map((r,a)=>{var o=r.colorHex||"#cccccc";return`
    <div class="variant-row">
      <input type="text" class="variant-name" value="${r.nombre}" placeholder="Ej: 10cm, Chico, 500ml" data-index="${a}" />
      <input type="number" class="variant-price" value="${r.precio}" placeholder="Precio" step="0.01" data-index="${a}" />
      ${n?`
      <div class="color-picker-wrap">
        <input type="color" class="variant-color" value="${o}" data-index="${a}" />
        <input type="text" class="variant-color-text" value="${o}" placeholder="#hex" maxlength="7" data-index="${a}" />
      </div>`:""}
      <button type="button" class="remove-variant" data-index="${a}">✕</button>
    </div>
  `}).join(""),e.querySelectorAll(".variant-name").forEach(r=>r.addEventListener("change",a=>{const o=parseInt(a.target.dataset.index);E[o].nombre=a.target.value})),e.querySelectorAll(".variant-price").forEach(r=>r.addEventListener("change",a=>{const o=parseInt(a.target.dataset.index);E[o].precio=parseFloat(a.target.value)||0})),e.querySelectorAll(".variant-color").forEach(r=>r.addEventListener("input",a=>{var s;const o=parseInt(a.target.dataset.index);var i=a.target.value;E[o].colorHex=i;var d=(s=a.target.closest(".color-picker-wrap"))==null?void 0:s.querySelector(".variant-color-text");d&&(d.value=i)})),e.querySelectorAll(".variant-color-text").forEach(r=>r.addEventListener("input",a=>{var s;const o=parseInt(a.target.dataset.index);var i=a.target.value;if(/^#[0-9a-fA-F]{6}$/.test(i)){E[o].colorHex=i;var d=(s=a.target.closest(".color-picker-wrap"))==null?void 0:s.querySelector(".variant-color");d&&(d.value=i)}})),e.querySelectorAll(".remove-variant").forEach(r=>r.addEventListener("click",a=>{const o=parseInt(a.currentTarget.dataset.index);E.splice(o,1),A()}))}function H(){var t;E=[{nombre:"",precio:0}],A();const e=document.getElementById("add-variant");e&&(e.replaceWith(e.cloneNode(!0)),(t=document.getElementById("add-variant"))==null||t.addEventListener("click",()=>{E.push({nombre:"",precio:0}),A()}));const n=document.getElementById("colores-activo-toggle");n&&(n.removeEventListener("change",z),n.addEventListener("change",z))}function z(){A()}function B(){const e=document.getElementById("colores-propios-container");e&&(e.innerHTML=y.map((n,t)=>{var r=n.colorHex||"#cccccc",a=n.urlImagen||"",o=a?"display:inline-block;width:36px;height:36px;object-fit:cover;border-radius:6px;border:1px solid #334155;":"display:none;";return`
    <div class="variant-row" style="margin-bottom:8px;flex-wrap:wrap;">
      <input type="text" class="color-propio-name" value="${n.nombre}" placeholder="Nombre del color" data-index="${t}" style="flex:1;min-width:100px;" />
      <div class="color-picker-wrap">
        <input type="color" class="color-propio-hex" value="${r}" data-index="${t}" />
        <input type="text" class="color-propio-hex-text" value="${r}" placeholder="#hex" maxlength="7" data-index="${t}" />
      </div>
      <div style="display:flex;align-items:center;gap:6px;">
        <img class="color-img-preview" src="${a}" style="${o}" data-index="${t}" onerror="this.style.display='none'" />
        <input type="file" class="color-propio-file" accept="image/*" data-index="${t}" style="font-size:11px;max-width:100px;" />
      </div>
      <button type="button" class="remove-color-propio" data-index="${t}" style="background:#ef4444;border:none;border-radius:50%;width:36px;height:36px;color:white;font-size:18px;cursor:pointer;display:flex;align-items:center;justify-content:center;">✕</button>
    </div>
  `}).join(""),e.querySelectorAll(".color-propio-name").forEach(n=>n.addEventListener("change",t=>{const r=parseInt(t.target.dataset.index);y[r].nombre=t.target.value})),e.querySelectorAll(".color-propio-hex").forEach(n=>n.addEventListener("input",t=>{var i;const r=parseInt(t.target.dataset.index);var a=t.target.value;y[r].colorHex=a;var o=(i=t.target.closest(".color-picker-wrap"))==null?void 0:i.querySelector(".color-propio-hex-text");o&&(o.value=a)})),e.querySelectorAll(".color-propio-hex-text").forEach(n=>n.addEventListener("input",t=>{var i;const r=parseInt(t.target.dataset.index);var a=t.target.value;if(/^#[0-9a-fA-F]{6}$/.test(a)){y[r].colorHex=a;var o=(i=t.target.closest(".color-picker-wrap"))==null?void 0:i.querySelector(".color-propio-hex");o&&(o.value=a)}})),e.querySelectorAll(".color-propio-file").forEach(n=>n.addEventListener("change",t=>{var i;const r=parseInt(t.target.dataset.index),a=((i=t.target.files)==null?void 0:i[0])||null;if(x[r]=a,a){var o=new FileReader;o.onload=d=>{var p;var s=e.querySelector(".color-img-preview[data-index='"+r+"']");s&&(s.src=(p=d.target)==null?void 0:p.result,s.style.display="inline-block")},o.readAsDataURL(a)}})),e.querySelectorAll(".remove-color-propio").forEach(n=>n.addEventListener("click",t=>{const r=parseInt(t.currentTarget.dataset.index);y.splice(r,1),x.splice(r,1),B()})))}function D(){var r;y=[],x=[],B();const e=document.getElementById("colores-propios-toggle"),n=document.getElementById("colores-propios-manager");e&&(e.checked=!1),n&&(n.style.display="none"),e&&n&&(e.removeEventListener("change",()=>{}),e.addEventListener("change",()=>{n.style.display=e.checked?"block":"none",e.checked||(y=[],B())}));const t=document.getElementById("add-color-propio");t&&(t.replaceWith(t.cloneNode(!0)),(r=document.getElementById("add-color-propio"))==null||r.addEventListener("click",()=>{y.push({nombre:"",colorHex:"#2dd4bf"}),x.push(null),B()}))}const X="3223";let L=null;function Y(){const e=document.getElementById("clear-orders-btn");if(!e)return;e.replaceWith(e.cloneNode(!0));const n=document.getElementById("clear-orders-btn");n&&n.addEventListener("click",()=>U())}function U(e){L=e??null;const n=document.getElementById("clear-pin-overlay"),t=document.getElementById("pin-input"),r=document.getElementById("pin-error"),a=document.getElementById("pin-confirm-btn"),o=document.getElementById("pin-cancel-btn");if(!n||!t||!r||!a||!o)return;const i=n.querySelector(".pin-modal-header h3"),d=n.querySelector(".pin-modal-header p");t.value="",r.textContent="",a.disabled=!1,e?(i.textContent=`🔐 Eliminar pedido #${e}`,d.textContent="Esta acción es irreversible. Ingresá el PIN de seguridad para continuar.",a.textContent="🗑️ Eliminar pedido"):(i.textContent="🔐 Eliminar todos los pedidos",d.textContent="Esta acción es irreversible. Ingresá el PIN de seguridad para continuar.",a.textContent="🗑️ Eliminar todo"),n.style.display="flex",setTimeout(()=>t.focus(),100);const s=()=>{n.style.display="none",p()},p=()=>{a.removeEventListener("click",c),o.removeEventListener("click",s),t.removeEventListener("keydown",u),n.removeEventListener("click",m)},c=async()=>{if(t.value.trim()!==X){r.textContent="PIN incorrecto. Intentá de nuevo.",t.value="",t.focus();return}a.disabled=!0,a.textContent="Eliminando...";try{L?(await k(`/pedidos/${L}`),v(`Pedido #${L} eliminado ✅`,"success")):(await k("/pedidos"),v("Todos los pedidos fueron eliminados ✅","success")),s(),await R(),await I()}catch{v("Error al eliminar","error"),a.disabled=!1,a.textContent=L?"🗑️ Eliminar pedido":"🗑️ Eliminar todo"}},u=l=>{l.key==="Enter"&&c(),l.key==="Escape"&&s();const f=/^[0-9]$/;l.key.length===1&&!f.test(l.key)&&l.preventDefault()},m=l=>{l.target===n&&s()};a.addEventListener("click",c),o.addEventListener("click",s),t.addEventListener("keydown",u),n.addEventListener("click",m)}function Z(e){return e?new Date(e).toLocaleDateString("es-AR",{day:"2-digit",month:"2-digit",year:"numeric",hour:"2-digit",minute:"2-digit"}):"—"}document.addEventListener("DOMContentLoaded",async()=>{await ee()});async function ee(){var e;try{const n=await K();if(!n||((e=n.rol)==null?void 0:e.toUpperCase())!=="ADMIN"){window.location.href="../../auth/login/login.html";return}te(n),ae(),de(),oe(),ie(),await I(),se(),ve(),be(),ye(),re("cat-file","cat-img-preview"),ne()}catch(n){console.error("Error inicializando admin:",n)}}function te(e){const n=document.getElementById("admin-name"),t=document.getElementById("admin-email");n&&(n.textContent=e.nombre),t&&(t.textContent=e.email)}function ne(){const e=document.getElementById("prod-file");e&&e.addEventListener("change",()=>{const n=document.getElementById("prod-images-preview");if(!(!n||!e.files)){n.innerHTML="";for(let t=0;t<e.files.length;t++){const r=new FileReader;r.onload=a=>{var i;const o=document.createElement("div");o.style.cssText="display:inline-block;margin:4px;position:relative;",o.innerHTML=`<img src="${(i=a.target)==null?void 0:i.result}" style="width:80px;height:80px;object-fit:cover;border-radius:8px;border:2px solid #2dd4bf;">`,n.appendChild(o)},r.readAsDataURL(e.files[t])}}})}function re(e,n){const t=document.getElementById(e),r=document.getElementById(n);!t||!r||t.addEventListener("change",()=>{var o;const a=(o=t.files)==null?void 0:o[0];if(a){const i=new FileReader;i.onload=d=>{var s;r.innerHTML=`
          <div style="margin-top: 10px; text-align: center;">
            <img src="${(s=d.target)==null?void 0:s.result}" style="width: 120px; height: 120px; object-fit: cover; border-radius: 12px; border: 2px solid #2dd4bf;">
            <p style="font-size: 11px; color: #64748b; margin-top: 4px;">Vista previa seleccionada</p>
          </div>
        `},i.readAsDataURL(a)}else r.innerHTML=""})}function ae(){const e=document.getElementById("logoutBtn")||document.getElementById("logout-btn");e==null||e.addEventListener("click",async n=>{n.preventDefault(),await Q(),window.location.href="../../auth/login/login.html"})}function oe(){const e=document.getElementById("menu-toggle"),n=document.querySelector(".sidebar");!e||!n||(e.addEventListener("click",t=>{t.stopPropagation(),n.classList.toggle("active")}),document.addEventListener("click",t=>{const r=t.target;n.classList.contains("active")&&!n.contains(r)&&!e.contains(r)&&n.classList.remove("active")}))}function ie(){document.querySelectorAll(".quick-action").forEach(e=>{e.addEventListener("click",()=>{const n=e.dataset.view;n&&_(n)})})}function de(){const e=document.querySelectorAll(".nav-btn");e.forEach(n=>{n.addEventListener("click",async()=>{var r;e.forEach(a=>a.classList.remove("active")),n.classList.add("active");const t=n.getAttribute("data-view");t&&(V(t),await G(t),(r=document.querySelector(".sidebar"))==null||r.classList.remove("active"))})})}function se(){const e=document.querySelector(".nav-btn");if(!e)return;e.classList.add("active");const n=e.dataset.view;V(n),G(n)}function V(e){var n;document.querySelectorAll(".view").forEach(t=>t.classList.remove("active")),(n=document.getElementById("view-"+e))==null||n.classList.add("active")}async function I(){try{const[e,n,t]=await Promise.all([g("/productos"),g("/categoria"),g("/pedidos")]),r=(e==null?void 0:e.filter(o=>o.disponible))||[],a=(n==null?void 0:n.filter(o=>o.disponible!==!1))||[];w("metric-products",r.length),w("metric-categories",a.length),w("metric-orders",(t==null?void 0:t.length)||0),w("card-products",r.length),w("card-categories",a.length),w("card-orders",(t==null?void 0:t.length)||0)}catch(e){console.error("Error al cargar métricas:",e)}}function w(e,n){const t=document.getElementById(e);t&&(t.textContent=String(n))}function v(e,n="info"){const t=document.getElementById("toast-container");if(!t)return;const r=document.createElement("div");r.className=`toast ${n}`;const a={success:"✅",error:"❌",info:"ℹ️"};r.innerHTML=`${a[n]} ${e}`,t.appendChild(r),setTimeout(()=>{r.classList.add("out"),setTimeout(()=>r.remove(),250)},3e3)}function ce(e){const n=document.getElementById("doughnut-svg"),t=document.getElementById("doughnut-center"),r=document.getElementById("doughnut-legend"),a=document.getElementById("doughnut-skeleton");if(a&&a.classList.add("hidden"),!(e!=null&&e.length)){t&&(t.textContent="0");return}const o=["PENDIENTE","EN_PROCESO","EN_CAMINO","ENTREGADO","CANCELADO"],i=["#f59e0b","#6366f1","#06b6d4","#10b981","#ef4444"],d=["Pendiente","En Proceso","En Camino","Entregado","Cancelado"],s=o.map(c=>e.filter(u=>u.estado===c).length),p=s.reduce((c,u)=>c+u,0);if(t&&(t.textContent=String(p)),n){const u=2*Math.PI*50;let m=0;n.querySelectorAll(".doughnut-segment").forEach(f=>f.remove()),s.forEach((f,T)=>{if(f===0)return;const M=f/p*u,h=document.createElementNS("http://www.w3.org/2000/svg","circle");h.setAttribute("cx","60"),h.setAttribute("cy","60"),h.setAttribute("r",String(50)),h.setAttribute("fill","none"),h.setAttribute("stroke",i[T]),h.setAttribute("stroke-width","20"),h.setAttribute("stroke-dasharray",`${M} ${u-M}`),h.setAttribute("stroke-dashoffset",String(-m)),h.setAttribute("class","doughnut-segment"),h.style.transition="stroke-dasharray 0.8s ease",n.appendChild(h),m+=M})}r&&(r.innerHTML=s.map((c,u)=>c>0?`
      <div class="doughnut-legend-item">
        <span class="doughnut-legend-dot" style="background:${i[u]}"></span>
        ${d[u]}: ${c}
      </div>
    `:"").join(""))}function le(e){const n=document.getElementById("bar-chart"),t=document.getElementById("bar-skeleton");if(t&&t.classList.add("hidden"),!n||!(e!=null&&e.length))return;const r={};e.forEach(i=>{var s;const d=((s=i.categoria)==null?void 0:s.nombre)||"Sin categoría";r[d]=(r[d]||0)+1});const a=Object.entries(r).sort((i,d)=>d[1]-i[1]).slice(0,8),o=Math.max(...a.map(i=>i[1]),1);n.innerHTML=a.map(([i,d])=>{const s=d/o*100;return`
      <div class="bar-item">
        <div class="bar" style="height:${Math.max(s,4)}%"></div>
        <span class="bar-label">${i} (${d})</span>
      </div>
    `}).join("")}function ue(e){const n=document.getElementById("recent-orders");if(!n)return;if(!(e!=null&&e.length)){n.innerHTML='<p class="muted">No hay pedidos aún.</p>';return}const t=[...e].reverse().slice(0,6);n.innerHTML=t.map(r=>`
    <div class="recent-order" data-id="${r.id}">
      <div class="recent-order-left">
        <span class="recent-order-id">#${r.id}</span>
        <span class="recent-order-client">${r.usuarioNombre||"Usuario"}</span>
      </div>
      <span class="recent-order-total">$${Number(r.total).toFixed(2)}</span>
    </div>
  `).join(""),n.querySelectorAll(".recent-order").forEach(r=>{r.addEventListener("click",()=>{r.dataset.id,_("orders")})})}async function pe(){try{const[e,n]=await Promise.all([g("/productos"),g("/pedidos")]);ce(n),le(e),ue(n)}catch{v("Error al cargar datos del dashboard","error")}}function _(e){const n=document.querySelector(`.nav-btn[data-view="${e}"]`);n&&n.click()}async function G(e){e==="dashboard"&&await pe(),e==="categories"&&await O(),e==="products"&&(await Ee(),await F(),H(),D(),await C()),e==="orders"&&(await R(),Y()),e==="users"&&await Ie(),e==="etiquetas"&&await j()}function me(e){if(!e)return"/assets/categories/default.jpeg";if((e.includes("/uploads/http")||e.includes("/uploads/productos/http"))&&(e=e.replace("/uploads/productos/","").replace("/uploads/","")),e.includes("cloudinary")||e.includes("res.cloudinary")||e.includes("://"))return e;if(e.includes("categorias_"))return`https://res.cloudinary.com/dg5kaiz2s/image/upload/v1/tmcreation/categorias/${e}`;const n=e.replace(/^\/+/,"");return`https://tm-creation-v2.onrender.com/${n.startsWith("uploads")?n:"uploads/categorias/"+n}`}async function O(){const e=document.getElementById("categories-tbody");if(e){e.innerHTML="<tr><td colspan='5'>Cargando...</td></tr>";try{const n=await g("/categoria");if(!(n!=null&&n.length)){e.innerHTML="<tr><td colspan='5'>No hay categorías</td></tr>";return}e.innerHTML=n.map(t=>{const r=t.disponible===!1,a=r?"row-oculto":"",o=r?"badge-oculto":"badge-disponible",i=r?"Oculto":"Activo";return`
      <tr class="${a}">
        <td>${t.id}</td>
        <td><img src="${me(t.urlImagen)}" width="50" height="50" style="object-fit:cover; border-radius: 8px;"></td>
        <td>${t.nombre}</td>
        <td>
          <span class="badge ${o}">
            ${i}
          </span>
        </td>
        <td>
          <button class="btn-edit-category" data-id="${t.id}">Editar</button>
          ${r?'<span style="font-size: 0.8rem; color: #94a3b8;">(Desactivada)</span>':`<button class="btn-delete-category btn-danger" data-id="${t.id}">Eliminar</button>`}
        </td>
      </tr>
      `}).join(""),ge()}catch{e.innerHTML="<tr><td colspan='5'>Error cargando datos</td></tr>"}}}function ge(){document.querySelectorAll(".btn-edit-category").forEach(e=>{e.addEventListener("click",async()=>{const n=e.getAttribute("data-id");if(n)try{const t=await g(`/categoria/${n}`);if(!t)return;document.getElementById("cat-id").value=String(t.id),document.getElementById("cat-nombre").value=t.nombre,window.scrollTo({top:0,behavior:"smooth"})}catch(t){console.error(t)}})}),document.querySelectorAll(".btn-delete-category").forEach(e=>{e.addEventListener("click",async()=>{const n=e.getAttribute("data-id");if(!(!n||!confirm("¿Eliminar categoría? Esta acción borrará la categoría. Si tiene productos asociados, se ocultará automáticamente para proteger los datos.")))try{await k(`/categoria/${n}`),await O(),await I()}catch{v("Error al eliminar categoría","error")}})})}function ve(){var n;const e=document.getElementById("cat-form");e&&(e.addEventListener("submit",async t=>{var d;t.preventDefault();const r=document.getElementById("cat-id"),a=document.getElementById("cat-nombre"),o=document.getElementById("cat-file"),i=new FormData;i.append("nombre",a.value),(d=o.files)!=null&&d[0]&&i.append("imagen",o.files[0]);try{r.value?await q(`/categoria/${r.value}`,i,!0):await S("/categoria",i,!0),e.reset(),r.value="";const s=document.getElementById("cat-img-preview");s&&(s.innerHTML=""),await O(),await I()}catch{v("Error al guardar categoría","error")}}),(n=document.getElementById("cat-clear"))==null||n.addEventListener("click",()=>{e.reset(),document.getElementById("cat-id").value="";const t=document.getElementById("cat-img-preview");t&&(t.innerHTML="")}))}function W(e){return`display:inline-block;padding:2px 10px;border-radius:12px;font-size:0.75rem;font-weight:600;color:#fff;background:${e||"#2dd4bf"}`}function fe(e){return`<span style="${W(e.colorHex)}">${e.nombre}</span>`}async function j(){const e=document.getElementById("etiquetas-tbody");if(e){e.innerHTML="<tr><td colspan='6'>Cargando...</td></tr>";try{const n=await g("/etiquetas");if(!(n!=null&&n.length)){e.innerHTML="<tr><td colspan='6'>No hay etiquetas</td></tr>";return}e.innerHTML=n.map(t=>`
      <tr>
        <td>${t.id}</td>
        <td>${t.nombre}</td>
        <td>${fe(t)}</td>
        <td>${t.visible?'<span class="badge badge-disponible">Sí</span>':'<span class="badge badge-oculto">No</span>'}</td>
        <td>${t.interna?'<span class="badge badge-disponible">Sí</span>':'<span class="badge badge-oculto">No</span>'}</td>
        <td>
          <button class="btn-edit-etiqueta" data-id="${t.id}">Editar</button>
          <button class="btn-delete-etiqueta btn-danger" data-id="${t.id}">Eliminar</button>
        </td>
      </tr>
    `).join(""),document.querySelectorAll(".btn-edit-etiqueta").forEach(t=>{t.addEventListener("click",async()=>{const r=t.getAttribute("data-id");if(r)try{const a=await g(`/etiquetas/${r}`);if(!a)return;document.getElementById("etiqueta-id").value=String(a.id),document.getElementById("etiqueta-nombre").value=a.nombre,document.getElementById("etiqueta-color").value=a.colorHex||"#2dd4bf",document.getElementById("etiqueta-visible").checked=a.visible,document.getElementById("etiqueta-interna").checked=a.interna,window.scrollTo({top:0,behavior:"smooth"})}catch(a){console.error(a)}})}),document.querySelectorAll(".btn-delete-etiqueta").forEach(t=>{t.addEventListener("click",async()=>{const r=t.getAttribute("data-id");if(!(!r||!confirm("¿Eliminar esta etiqueta? Se removerá de todos los productos asociados.")))try{await k(`/etiquetas/${r}`),await j(),await C()}catch{v("Error al eliminar etiqueta","error")}})})}catch{e.innerHTML="<tr><td colspan='6'>Error cargando etiquetas</td></tr>"}}}function ye(){var n;const e=document.getElementById("etiqueta-form");e&&(e.addEventListener("submit",async t=>{t.preventDefault();const r=document.getElementById("etiqueta-id"),a=document.getElementById("etiqueta-nombre"),o=document.getElementById("etiqueta-color"),i=document.getElementById("etiqueta-visible"),d=document.getElementById("etiqueta-interna"),s={nombre:a.value,colorHex:o.value,visible:i.checked,interna:d.checked};try{r.value?await q(`/etiquetas/${r.value}`,s):await S("/etiquetas",s),e.reset(),r.value="",document.getElementById("etiqueta-color").value="#2dd4bf",document.getElementById("etiqueta-visible").checked=!0,await j(),await C(),v("Etiqueta guardada","success")}catch{v("Error al guardar etiqueta","error")}}),(n=document.getElementById("etiqueta-clear"))==null||n.addEventListener("click",()=>{e.reset(),document.getElementById("etiqueta-id").value="",document.getElementById("etiqueta-color").value="#2dd4bf",document.getElementById("etiqueta-visible").checked=!0}))}async function C(e){const n=document.getElementById("etiquetas-select");if(n)try{const t=await g("/etiquetas");if(!(t!=null&&t.length)){n.innerHTML='<p class="field-hint">No hay etiquetas disponibles. Creá algunas desde la sección Etiquetas.</p>';return}const r=e||b;n.innerHTML=t.map(a=>{const o=r.has(a.id)?"checked":"";return`
        <label class="tag-checkbox" style="display:inline-flex;align-items:center;gap:6px;margin:4px 8px 4px 0;cursor:pointer;">
          <input type="checkbox" class="etiqueta-cb" value="${a.id}" ${o} />
          <span style="${W(a.colorHex)}">${a.nombre}</span>
        </label>
      `}).join(""),n.querySelectorAll(".etiqueta-cb").forEach(a=>{a.addEventListener("change",()=>{const o=Number(a.value);a.checked?b.add(o):b.delete(o)})})}catch{n.innerHTML='<p class="field-hint">Error cargando etiquetas</p>'}}async function Ee(){const e=document.getElementById("prod-categoria");if(e)try{const n=await g("/categoria");e.innerHTML='<option value="">Seleccione categoría</option>',n==null||n.forEach(t=>{t.disponible!==!1&&(e.innerHTML+=`<option value="${t.id}">${t.nombre}</option>`)})}catch(n){console.error(n)}}function P(e){if(!e||e==="null")return"/assets/food/default.jpeg";if((e.includes("/uploads/http")||e.includes("/uploads/productos/http"))&&(e=e.replace("/uploads/productos/","").replace("/uploads/","")),e.includes("cloudinary")||e.includes("res.cloudinary")||e.includes("://"))return e;if(e.includes("productos_"))return`https://res.cloudinary.com/dg5kaiz2s/image/upload/v1/tmcreation/productos/${e}`;const n=e.replace(/^\/+/,"");return`https://tm-creation-v2.onrender.com/${n.startsWith("uploads")?n:"uploads/productos/"+n}`}async function F(){const e=document.getElementById("products-tbody");if(e){e.innerHTML="<tr><td colspan='9'>Cargando...</td></tr>";try{const n=await g("/productos");if(!(n!=null&&n.length)){e.innerHTML="<tr><td colspan='9'>No hay productos</td></tr>";return}e.innerHTML=n.map(t=>{var u,m;const r=!t.disponible,a=r?"row-oculto":"",o=r?"badge-oculto":"badge-disponible",i=r?"Oculto":"Disponible",d=(u=t.imagenes)!=null&&u.length?t.imagenes[0].urlImagen:t.urlImagen,s=t.variantes&&t.variantes.length>0,p=s?`$${Math.min(...t.variantes.map(l=>l.precio)).toLocaleString()} - $${Math.max(...t.variantes.map(l=>l.precio)).toLocaleString()}`:`$${Number(t.precio).toFixed(2)}`,c=s?`${t.variantes.length} tams.`:"—";return`
      <tr class="${a}">
        <td>${t.id}</td>
        <td><img src="${P(d)}" width="50" height="50" style="object-fit:cover; border-radius: 8px;" onerror="this.src='/assets/food/default.jpeg';"></td>
        <td>${t.nombre}</td>
        <td>${p}</td>
        <td>${t.stock}</td>
        <td>${c}</td>
        <td>${((m=t.categoria)==null?void 0:m.nombre)||"-"}</td>
        <td><span class="badge ${o}">${i}</span></td>
        <td>
          <button class="btn-edit-product" data-id="${t.id}">Editar</button>
          ${r?'<span style="font-size: 0.8rem; color: #94a3b8;">(Desactivado)</span>':`<button class="btn-delete-product btn-danger" data-id="${t.id}">Eliminar</button>`}
        </td>
      </tr>`}).join(""),he()}catch{e.innerHTML="<tr><td colspan='9'>Error cargando productos</td></tr>"}}}function he(){document.querySelectorAll(".btn-edit-product").forEach(e=>{e.addEventListener("click",async()=>{var t;const n=e.getAttribute("data-id");if(n)try{const r=await g(`/productos/${n}`);if(!r)return;const a=document.getElementById("prod-file");a&&(a.value=""),document.getElementById("prod-id").value=String(r.id),document.getElementById("prod-nombre").value=r.nombre,document.getElementById("prod-precio").value=String(r.precio),document.getElementById("prod-stock").value=String(r.stock),document.getElementById("prod-descripcion").value=r.descripcion||"";const o=document.getElementById("prod-disponible");o&&(o.checked=!!r.disponible);const i=document.getElementById("colores-activo-toggle");i&&(i.checked=!!r.coloresActivo);const d=document.getElementById("colores-propios-toggle"),s=document.getElementById("colores-propios-manager");d&&s&&(d.checked=!!r.coloresActivo,s.style.display=d.checked?"block":"none"),y=(r.colores||[]).map(l=>({nombre:l.nombre,colorHex:l.colorHex,urlImagen:l.urlImagen})),x=new Array(y.length).fill(null),B();const p=document.getElementById("stock-control-toggle");p&&(p.checked=r.stockControl!==!1);const c=document.getElementById("prod-categoria");c&&((t=r.categoria)!=null&&t.id)&&(c.value=String(r.categoria.id)),E=(r.variantes||[]).map(l=>({nombre:l.nombre,precio:l.precio,colorHex:l.colorHex})),E.length===0&&E.push({nombre:"",precio:0}),A(),b=new Set((r.etiquetas||[]).map(l=>l.id)),await C(b);const u=document.getElementById("prod-images-preview");u&&(u.innerHTML=""),$=new Set;const m=document.getElementById("prod-img-preview");if(m){const l=r.imagenes||[];l.length>0?(m.innerHTML=`
                <p style="font-size:0.8rem;color:var(--text-muted);margin-bottom:8px;">${l.length} imagen(es) guardada(s) — hacé clic en × para eliminar</p>
                <div style="display:flex;flex-wrap:wrap;gap:8px;">
                ${l.map(f=>`
                  <div class="img-item" data-id="${f.id}" style="position:relative;width:80px;height:80px;">
                    <img src="${P(f.urlImagen)}" style="width:80px;height:80px;object-fit:cover;border-radius:8px;border:2px solid #2dd4bf;" onerror="this.src='/assets/food/default.jpeg';">
                    <button type="button" class="btn-del-img" data-id="${f.id}" style="position:absolute;top:-6px;right:-6px;width:22px;height:22px;border-radius:50%;border:none;background:#ef4444;color:white;font-size:14px;line-height:1;cursor:pointer;display:flex;align-items:center;justify-content:center;">×</button>
                  </div>
                `).join("")}
                </div>`,m.querySelectorAll(".btn-del-img").forEach(f=>{f.addEventListener("click",()=>{const T=Number(f.dataset.id);$.add(T);const N=f.closest(".img-item");N&&(N.style.display="none")})})):r.urlImagen?m.innerHTML=`
                <div style="margin-top: 10px; text-align: center;">
                  <img src="${P(r.urlImagen)}" style="width: 120px; height: 120px; object-fit: cover; border-radius: 12px; border: 2px solid #2dd4bf;" onerror="this.src='/assets/food/default.jpeg';">
                  <p style="font-size: 11px; color: #64748b; margin-top: 4px;">Imagen actual</p>
                </div>`:m.innerHTML='<p style="font-size:0.85rem;color:var(--text-muted);padding:10px;">Sin imágenes. Seleccioná archivos para agregar.</p>'}window.scrollTo({top:0,behavior:"smooth"})}catch(r){console.error(r)}})}),document.querySelectorAll(".btn-delete-product").forEach(e=>{e.addEventListener("click",async()=>{const n=e.getAttribute("data-id");if(!(!n||!confirm("¿Eliminar producto? Si ya tiene ventas registradas, se ocultará en lugar de borrarse para proteger el historial de pedidos.")))try{await k(`/productos/${n}`),await F(),await I()}catch{v("Error al procesar el producto","error")}})})}function be(){var n;const e=document.getElementById("prod-form");e&&(e.addEventListener("submit",async t=>{var s,p;t.preventDefault();const r=document.getElementById("prod-id"),a=document.getElementById("prod-categoria"),o=document.getElementById("prod-disponible"),i=document.getElementById("prod-file");if(!a.value)return v("Seleccione una categoría","error");const d=new FormData;d.append("nombre",document.getElementById("prod-nombre").value),d.append("precio",document.getElementById("prod-precio").value),d.append("stock",document.getElementById("prod-stock").value),d.append("descripcion",document.getElementById("prod-descripcion").value),d.append("categoriaId",a.value),d.append("disponible",String(o.checked)),d.append("coloresActivo",String(((s=document.getElementById("colores-propios-toggle"))==null?void 0:s.checked)??!1)),d.append("stockControl",String(((p=document.getElementById("stock-control-toggle"))==null?void 0:p.checked)??!0)),d.append("variantes",JSON.stringify(E.filter(c=>c.nombre&&c.precio>0)));for(let c=0;c<y.length;c++){const u=x[c];if(u)try{const m=new FormData;m.append("file",u);const l=await S("/upload",m,!0);y[c].urlImagen=l,x[c]=null}catch{v("Error al subir imagen del color","error")}}if(d.append("colores",JSON.stringify(y)),b.size>0&&d.append("etiquetaIds",JSON.stringify(Array.from(b))),i.files)for(let c=0;c<i.files.length;c++)d.append("imagenes",i.files[c]);r.value&&$.size>0&&d.append("imagenesEliminarIds",JSON.stringify(Array.from($)));try{r.value?await q(`/productos/${r.value}`,d,!0):await S("/productos",d,!0),e.reset(),r.value="",$=new Set,b=new Set;const c=document.getElementById("prod-img-preview");c&&(c.innerHTML="");const u=document.getElementById("prod-images-preview");u&&(u.innerHTML=""),o&&(o.checked=!0),H(),D(),await F(),await I()}catch{v("Error al guardar producto","error")}}),(n=document.getElementById("prod-clear"))==null||n.addEventListener("click",()=>{e.reset(),document.getElementById("prod-id").value="",$=new Set;const t=document.getElementById("prod-img-preview");t&&(t.innerHTML='<p style="font-size:0.85rem;color:var(--text-muted);padding:10px;">Sin imágenes. Seleccioná archivos para agregar.</p>');const r=document.getElementById("prod-images-preview");r&&(r.innerHTML="");const a=document.getElementById("prod-disponible");a&&(a.checked=!0),H(),D(),b=new Set;const o=document.getElementById("etiquetas-select");o&&(o.innerHTML="")}))}function J(e){switch(e){case"PENDIENTE":return"badge badge-pendiente";case"EN_PROCESO":case"EN_CAMINO":return"badge badge-aprobado";case"ENTREGADO":return"badge badge-entregado";case"CANCELADO":return"badge badge-cancelado";default:return"badge"}}async function R(){const e=document.getElementById("orders-container");if(e){e.innerHTML="<p>Cargando pedidos...</p>";try{let n=await g("/pedidos");if(!(n!=null&&n.length)){e.innerHTML="<p>No hay pedidos registrados.</p>";return}n.reverse(),e.innerHTML=n.map(t=>{const r=t.estado==="CANCELADO"||t.estado==="ENTREGADO",a=t.detalles||[],o=a.map(i=>{var p;const d=Number(i.cantidad)||0,s=Number(i.precio)||0;return`
          <tr>
            <td><strong>${((p=i.producto)==null?void 0:p.nombre)||"—"}</strong></td>
            <td>${i.tamanio||i.varianteNombre||"—"}</td>
            <td>${d}</td>
            <td>$${s.toFixed(2)}</td>
            <td>$${(s*d).toFixed(2)}</td>
          </tr>
        `}).join("");return`
        <div class="order-card">
          <div class="order-header">
            <h3>Pedido #${t.id}</h3>
            <span class="${J(t.estado)}" id="badge-${t.id}">${t.estado}</span>
          </div>

          <div class="order-details-grid">
            <div class="order-detail">
              <span class="detail-label">Cliente</span>
              <span class="detail-value">${t.usuarioNombre||"—"}</span>
            </div>
            <div class="order-detail">
              <span class="detail-label">Email</span>
              <span class="detail-value">${t.usuarioEmail||"—"}</span>
            </div>
            <div class="order-detail">
              <span class="detail-label">Dirección</span>
              <span class="detail-value">${t.direccion||"—"}</span>
            </div>
            <div class="order-detail">
              <span class="detail-label">Fecha</span>
              <span class="detail-value">${Z(t.fecha)}</span>
            </div>
            <div class="order-detail">
              <span class="detail-label">Total</span>
              <span class="detail-value total">$${Number(t.total).toFixed(2)}</span>
            </div>
          </div>

          <div class="order-products-section">
            <div class="order-products-header">
              <h4>Productos</h4>
              <span class="order-products-count">${a.length} artículo(s)</span>
            </div>
            <div class="table-responsive">
              <table class="order-products-table">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Tamaño</th>
                    <th>Cant.</th>
                    <th>Precio</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  ${o||'<tr><td colspan="5" style="text-align:center;color:var(--text-muted);">Sin detalles</td></tr>'}
                </tbody>
              </table>
            </div>
          </div>

          <div class="order-actions">
            <select class="order-select" id="select-status-${t.id}" ${r?"disabled":""}>
              <option value="PENDIENTE" ${t.estado==="PENDIENTE"?"selected":""}>PENDIENTE</option>
              <option value="EN_PROCESO" ${t.estado==="EN_PROCESO"?"selected":""}>EN PROCESO</option>
              <option value="EN_CAMINO" ${t.estado==="EN_CAMINO"?"selected":""}>EN CAMINO</option>
              <option value="ENTREGADO" ${t.estado==="ENTREGADO"?"selected":""}>ENTREGADO</option>
              <option value="CANCELADO" ${t.estado==="CANCELADO"?"selected":""}>CANCELADO</option>
            </select>
            <div class="order-actions-buttons">
              ${r?'<span class="status-fixed">✔️ Finalizado</span>':`<button class="btn-primary save-order-btn" data-id="${t.id}">Guardar</button>`}
              <button class="delete-order-btn" data-id="${t.id}">🗑️ Eliminar</button>
            </div>
          </div>
        </div>
      `}).join(""),xe()}catch{e.innerHTML="<p>Error al cargar pedidos.</p>"}}}function xe(){document.querySelectorAll(".save-order-btn").forEach(e=>{e.addEventListener("click",async()=>{const n=e.getAttribute("data-id");if(!n)return;const r=document.getElementById(`select-status-${n}`).value;if(!(r==="CANCELADO"&&!confirm("¿Cancelar pedido?")))try{await q(`/pedidos/${n}/estado`,{estado:r}),await R(),await I()}catch{v("Error al actualizar pedido","error")}})}),document.querySelectorAll(".delete-order-btn").forEach(e=>{e.addEventListener("click",n=>{n.stopPropagation();const t=e.getAttribute("data-id");t&&U(parseInt(t))})})}async function Ie(){const e=document.getElementById("users-tbody"),n=document.getElementById("user-detail-panel");if(e){e.innerHTML="<tr><td colspan='6'>Cargando...</td></tr>",n&&(n.style.display="none");try{const[t,r]=await Promise.all([g("/usuarios"),g("/pedidos")]);if(!(t!=null&&t.length)){e.innerHTML="<tr><td colspan='6'>No hay usuarios registrados</td></tr>";return}const a={};r&&r.forEach(o=>{const i=o.usuarioId;i&&(a[i]=(a[i]||0)+1)}),e.innerHTML=t.map(o=>{const i=a[o.id]||0,d=o.rol==="ADMIN"?"badge badge-pendiente":"badge badge-disponible",s=o.rol==="ADMIN"?"Admin":"Usuario";return`
        <tr>
          <td>${o.id}</td>
          <td><strong>${o.nombre} ${o.apellido||""}</strong></td>
          <td>${o.email}</td>
          <td><span class="${d}">${s}</span></td>
          <td>${i}</td>
          <td><button class="btn-edit-product view-user-btn" data-id="${o.id}">Ver Detalle</button></td>
        </tr>
      `}).join(""),e.querySelectorAll(".view-user-btn").forEach(o=>{o.addEventListener("click",()=>we(parseInt(o.dataset.id)))})}catch{e.innerHTML="<tr><td colspan='6'>Error al cargar usuarios</td></tr>",v("Error al cargar usuarios","error")}}}async function we(e){var a;const n=document.getElementById("user-detail-panel"),t=document.getElementById("detail-user-name"),r=document.getElementById("user-detail-body");if(!(!n||!t||!r)){n.style.display="block",t.textContent="Cargando...",r.innerHTML="";try{const[o,i]=await Promise.all([g(`/usuarios/${e}`),g(`/pedidos/usuario/${e}`)]);t.textContent=`${o.nombre} ${o.apellido||""}`;const d=i!=null&&i.length?i.map(s=>`
        <div class="user-order-item">
          <span class="user-order-id">#${s.id}</span>
          <span class="${J(s.estado)}">${s.estado}</span>
          <span class="user-order-total">$${Number(s.total).toFixed(2)}</span>
          <span class="user-order-date">${new Date(s.fecha).toLocaleDateString("es-AR")}</span>
        </div>
      `).join(""):'<p class="muted">Sin pedidos</p>';r.innerHTML=`
      <div class="user-info-block">
        <div class="user-info-row"><span>Email</span><span>${o.email}</span></div>
        <div class="user-info-row"><span>Teléfono</span><span>${o.celular||"—"}</span></div>
        <div class="user-info-row"><span>Rol</span><span>${o.rol==="ADMIN"?"Administrador":"Usuario"}</span></div>
      </div>
      <h4 style="margin:20px 0 12px;color:var(--text-muted);font-size:0.85rem;text-transform:uppercase;letter-spacing:0.5px;">Pedidos (${(i==null?void 0:i.length)||0})</h4>
      <div class="user-orders-list">${d}</div>
    `,(a=document.getElementById("close-user-detail"))==null||a.addEventListener("click",()=>{n.style.display="none"})}catch{t.textContent="Error",r.innerHTML='<p class="muted">No se pudo cargar el detalle del usuario</p>',v("Error al cargar detalle del usuario","error")}}}
