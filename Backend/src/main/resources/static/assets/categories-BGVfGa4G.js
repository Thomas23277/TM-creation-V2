import{b as u,a as c,d as l,c as m}from"./api-BwTScyzt.js";const n={Bebidas:"/assets/categories/BEBIDAS.png",Snacks:"/assets/categories/SNACKS.png","Frutas y Verduras":"/assets/categories/FRUTASYVERDURAS.png",Todo:"/assets/categories/TODO.png",default:"/assets/categories/default.jpeg"};function d(t){if(!t)return n.default;if(t.includes("/uploads/http")&&(t=t.replace("/uploads/","")),t.includes("cloudinary")||t.includes("res.cloudinary")||t.includes("://"))return t;if(t.includes("categorias_"))return`https://res.cloudinary.com/dg5kaiz2s/image/upload/v1/tmcreation/categorias/${t}`;const e=t.replace(/^\/+/,"");return e.startsWith("uploads/")?`https://tm-creation-v2.onrender.com/${e}`:e.startsWith("categorias/")?`https://tm-creation-v2.onrender.com/uploads/${e}`:`https://tm-creation-v2.onrender.com/uploads/categorias/${e}`}async function p(){try{return await c("/categoria")}catch(t){return console.error("Error cargando categorías:",t),[]}}async function v(t){const e=new FormData;return e.append("nombre",t.nombre),t.imagen&&e.append("imagen",t.imagen),t.id?l(`/categoria/${t.id}`,e,!0):m("/categoria",e,!0)}async function b(t){return u(`/categoria/${t}`)}async function y(){const t=document.getElementById("categories-tbody");if(!t)return;t.innerHTML="<tr><td colspan='5'>Cargando...</td></tr>";const e=await p();if(!e.length){t.innerHTML="<tr><td colspan='5'>No hay categorías.</td></tr>";return}t.innerHTML=e.map(r=>{const o=r.urlImagen?d(r.urlImagen):n[r.nombre]||n.default,a=r.disponible===!1,s=a?"row-oculto":"",i=a?"badge-oculto":"badge-disponible",g=a?"Oculto":"Activa";return`
        <tr class="${s}">
          <td>${r.id}</td>
          <td>
            <img src="${o}" width="60" height="60"
              style="border-radius:6px;object-fit:cover;" 
              onerror="this.src='${n.default}';" />
          </td>
          <td>${r.nombre}</td>
          <td>
            <span class="badge ${i}">${g}</span>
          </td>
          <td>
            <button class="btn-edit-category" data-id="${r.id}">Editar</button>
            ${a?'<span style="font-size: 0.8rem; color: #94a3b8;">(Desactivada)</span>':`<button class="btn-delete-category btn-danger" data-id="${r.id}">Eliminar</button>`}
          </td>
        </tr>
      `}).join(""),f()}function f(){const t=document.getElementById("categories-tbody");t&&(t.querySelectorAll(".btn-delete-category").forEach(e=>{e.addEventListener("click",async()=>{const r=Number(e.getAttribute("data-id"));if(!(!r||!confirm("¿Eliminar categoría?")))try{await b(r),await y()}catch{alert("Error al intentar eliminar.")}})}),t.querySelectorAll(".btn-edit-category").forEach(e=>{e.addEventListener("click",async()=>{var o;const r=Number(e.getAttribute("data-id"));if(r)try{const a=await c(`/categoria/${r}`);document.getElementById("cat-id").value=String(a.id),document.getElementById("cat-nombre").value=a.nombre;const s=document.getElementById("cat-img-preview");if(s){const i=d(a.urlImagen);s.innerHTML=`
              <div style="margin-top: 10px; text-align: center;">
                <img src="${i}" style="width: 120px; height: 120px; object-fit: cover; border-radius: 12px; border: 2px solid #2dd4bf;" onerror="this.src='${n.default}';">
                <p style="font-size: 11px; color: #64748b; margin-top: 4px;">Imagen actual</p>
              </div>`}(o=document.querySelector(".panel-form"))==null||o.scrollIntoView({behavior:"smooth"})}catch(a){console.error("Error al cargar datos:",a)}})}))}export{b as deleteCategory,p as loadCategories,y as renderCategories,v as saveCategory};
