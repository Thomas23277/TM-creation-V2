import { apiGet, apiPost, apiPut, apiDelete } from "../../../utils/api";
/* =========================================
   IMÁGENES FALLBACK
========================================= */
const imagenesCategorias = {
    Bebidas: "/src/assets/categories/BEBIDAS.png",
    Snacks: "/src/assets/categories/SNACKS.png",
    "Frutas y Verduras": "/src/assets/categories/FRUTASYVERDURAS.png",
    Todo: "/src/assets/categories/TODO.png",
    default: "/src/assets/categories/default.jpeg",
};
function getCategoryImageUrl(file) {
    if (!file)
        return imagenesCategorias.default;
    const clean = file.replace(/^\/+/, "");
    if (clean.startsWith("uploads/"))
        return `http://localhost:8080/${clean}`;
    if (clean.startsWith("categorias/"))
        return `http://localhost:8080/uploads/${clean}`;
    return `http://localhost:8080/uploads/categorias/${clean}`;
}
/* =========================================
   API
========================================= */
export async function loadCategories() {
    try {
        return await apiGet("/categoria");
    }
    catch (error) {
        console.error("Error cargando categorías:", error);
        return [];
    }
}
export async function saveCategory(data) {
    const fd = new FormData();
    fd.append("nombre", data.nombre);
    if (data.imagen)
        fd.append("imagen", data.imagen);
    if (data.id)
        return apiPut(`/categoria/${data.id}`, fd, true);
    return apiPost(`/categoria`, fd, true);
}
export async function deleteCategory(id) {
    return apiDelete(`/categoria/${id}`);
}
/* =========================================
   RENDER
========================================= */
export async function renderCategories() {
    const tbody = document.getElementById("categories-tbody");
    if (!tbody)
        return;
    tbody.innerHTML = "<tr><td colspan='5'>Cargando...</td></tr>";
    const categorias = await loadCategories();
    if (!categorias.length) {
        tbody.innerHTML = "<tr><td colspan='5'>No hay categorías.</td></tr>";
        return;
    }
    tbody.innerHTML = categorias
        .map((c) => {
        const img = c.urlImagen
            ? getCategoryImageUrl(c.urlImagen)
            : imagenesCategorias[c.nombre] || imagenesCategorias.default;
        return `
        <tr>
          <td>${c.id}</td>
          <td>
            <img src="${img}" width="60"
              style="border-radius:6px;object-fit:cover;" />
          </td>
          <td>${c.nombre}</td>
          <td>—</td>
          <td>
            <button class="btn-edit-category" data-id="${c.id}">Editar</button>
            <button class="btn-delete-category" data-id="${c.id}">Eliminar</button>
          </td>
        </tr>
      `;
    })
        .join("");
    attachCategoryEvents();
}
/* =========================================
   EVENTOS (AISLADOS)
========================================= */
function attachCategoryEvents() {
    const tbody = document.getElementById("categories-tbody");
    if (!tbody)
        return;
    // ELIMINAR
    tbody.querySelectorAll(".btn-delete-category").forEach((btn) => {
        btn.addEventListener("click", async () => {
            const id = Number(btn.getAttribute("data-id"));
            if (!id)
                return;
            if (!confirm("¿Eliminar categoría?"))
                return;
            try {
                await deleteCategory(id);
                await renderCategories();
            }
            catch (err) {
                alert(err.message || "No se pudo eliminar la categoría.");
            }
        });
    });
    // EDITAR
    tbody.querySelectorAll(".btn-edit-category").forEach((btn) => {
        btn.addEventListener("click", async () => {
            var _a;
            const id = Number(btn.getAttribute("data-id"));
            if (!id)
                return;
            const cat = await apiGet(`/categoria/${id}`);
            document.getElementById("cat-id").value = String(cat.id);
            document.getElementById("cat-nombre").value = cat.nombre;
            const preview = document.getElementById("cat-preview");
            if (preview) {
                preview.src = cat.urlImagen
                    ? getCategoryImageUrl(cat.urlImagen)
                    : imagenesCategorias[cat.nombre] || imagenesCategorias.default;
            }
            (_a = document.querySelector(".panel-form")) === null || _a === void 0 ? void 0 : _a.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
        });
    });
}
