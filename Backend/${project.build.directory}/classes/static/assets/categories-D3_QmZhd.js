import{a as d,b as s,c as u,d as g}from"./api-Cd49zLLy.js";const o={Bebidas:"/src/assets/categories/BEBIDAS.png",Snacks:"/src/assets/categories/SNACKS.png","Frutas y Verduras":"/src/assets/categories/FRUTASYVERDURAS.png",Todo:"/src/assets/categories/TODO.png",default:"/src/assets/categories/default.jpeg"};function c(t){if(!t)return o.default;const e=t.replace(/^\/+/,"");return e.startsWith("uploads/")?`http://localhost:8080/${e}`:e.startsWith("categorias/")?`http://localhost:8080/uploads/${e}`:`http://localhost:8080/uploads/categorias/${e}`}async function l(){try{return await s("/categoria")}catch(t){return console.error("Error cargando categorías:",t),[]}}async function b(t){const e=new FormData;return e.append("nombre",t.nombre),t.imagen&&e.append("imagen",t.imagen),t.id?u(`/categoria/${t.id}`,e,!0):g("/categoria",e,!0)}async function m(t){return d(`/categoria/${t}`)}async function y(){const t=document.getElementById("categories-tbody");if(!t)return;t.innerHTML="<tr><td colspan='5'>Cargando...</td></tr>";const e=await l();if(!e.length){t.innerHTML="<tr><td colspan='5'>No hay categorías.</td></tr>";return}t.innerHTML=e.map(r=>{const a=r.urlImagen?c(r.urlImagen):o[r.nombre]||o.default;return`
        <tr>
          <td>${r.id}</td>
          <td>
            <img src="${a}" width="60"
              style="border-radius:6px;object-fit:cover;" />
          </td>
          <td>${r.nombre}</td>
          <td>—</td>
          <td>
            <button class="btn-edit-category" data-id="${r.id}">Editar</button>
            <button class="btn-delete-category" data-id="${r.id}">Eliminar</button>
          </td>
        </tr>
      `}).join(""),p()}function p(){const t=document.getElementById("categories-tbody");t&&(t.querySelectorAll(".btn-delete-category").forEach(e=>{e.addEventListener("click",async()=>{const r=Number(e.getAttribute("data-id"));if(r&&confirm("¿Eliminar categoría?"))try{await m(r),await y()}catch(a){alert(a.message||"No se pudo eliminar la categoría.")}})}),t.querySelectorAll(".btn-edit-category").forEach(e=>{e.addEventListener("click",async()=>{var i;const r=Number(e.getAttribute("data-id"));if(!r)return;const a=await s(`/categoria/${r}`);document.getElementById("cat-id").value=String(a.id),document.getElementById("cat-nombre").value=a.nombre;const n=document.getElementById("cat-preview");n&&(n.src=a.urlImagen?c(a.urlImagen):o[a.nombre]||o.default),(i=document.querySelector(".panel-form"))==null||i.scrollIntoView({behavior:"smooth",block:"start"})})}))}export{m as deleteCategory,l as loadCategories,y as renderCategories,b as saveCategory};
