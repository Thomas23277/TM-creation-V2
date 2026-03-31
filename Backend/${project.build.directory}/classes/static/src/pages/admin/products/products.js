import { apiGet, apiPost, apiPut, apiDelete } from "../../../utils/api";
/* =========================================
   IMÁGENES FALLBACK
========================================= */
const imagenesProductos = {
    "Gomitas de oso": "/src/assets/food/GOMITASDEOSO.jpeg",
    Manzana: "/src/assets/food/MANZANA.jpeg",
    "Coca Cola 2l": "/src/assets/food/COCACOLA2L.jpeg",
    "Doritos 129g": "/src/assets/food/DORITO.jpeg",
    Lechuga: "/src/assets/food/LECHUGA.jpeg",
    "Agua mineral": "/src/assets/food/AGUAMINERAL.jpeg",
    "Coca Cola 1l": "/src/assets/food/COCACOLA1L.jpeg",
    "Fanta 2l": "/src/assets/food/FANTA2L.jpeg",
    "Priti 2l": "/src/assets/food/PRITTY2L.jpg",
    "Lata Quilmes 500ml": "/src/assets/food/LATAQUILMES.jpeg",
    "Quilmes Rubia 1l": "/src/assets/food/QUILMESRUBIA.jpeg",
    "Oreos paquete": "/src/assets/food/OREOSPAQUETE.jpeg",
    "Lays Clasicas 134g": "/src/assets/food/LAYSCLASICAS.jpeg",
    Pera: "/src/assets/food/PERA.jpeg",
    "Pringles Jamon Crudo": "/src/assets/food/PRINGLESJAMONCRUDO.jpeg",
    "Chicles de menta": "/src/assets/food/CHICLESDEMENTA.jpeg",
    Banana: "/src/assets/food/BANANA.jpeg",
    Tomate: "/src/assets/food/TOMATE.jpeg",
    Naranja: "/src/assets/food/NARANJA.jpeg",
};
function getProductImageUrl(file) {
    if (!file)
        return "/src/assets/food/default.jpeg";
    if (file.startsWith("/uploads/"))
        return `http://localhost:8080${file}`;
    if (file.startsWith("http"))
        return file;
    return `http://localhost:8080/uploads/${file}`;
}
/* =========================================
   LOAD PRODUCTS
========================================= */
export async function loadProducts() {
    var _a, _b;
    const tbody = document.getElementById("products-tbody");
    if (!tbody)
        return;
    try {
        const productos = (_a = (await apiGet("/productos"))) !== null && _a !== void 0 ? _a : [];
        const categorias = (_b = (await apiGet("/categoria"))) !== null && _b !== void 0 ? _b : [];
        const selectCat = document.getElementById("prod-categoria");
        if (selectCat) {
            selectCat.innerHTML =
                categorias.length > 0
                    ? categorias.map((c) => `<option value="${c.id}">${c.nombre}</option>`).join("")
                    : `<option value="">Sin categorías</option>`;
        }
        if (!Array.isArray(productos) || productos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9">No hay productos</td></tr>';
            return;
        }
        tbody.innerHTML = productos
            .map((p) => {
            var _a, _b, _c, _d, _e, _f, _g;
            const img = p.urlImagen
                ? getProductImageUrl(p.urlImagen)
                : imagenesProductos[p.nombre] || "/src/assets/food/default.jpeg";
            return `
          <tr>
            <td>${(_a = p.id) !== null && _a !== void 0 ? _a : "-"}</td>
            <td>
              <img src="${img}" style="width:56px;height:56px;object-fit:cover;border-radius:6px;">
            </td>
            <td>${(_b = p.nombre) !== null && _b !== void 0 ? _b : ""}</td>
            <td>${((_c = p.descripcion) !== null && _c !== void 0 ? _c : "").substring(0, 60)}</td>
            <td>$${Number((_d = p.precio) !== null && _d !== void 0 ? _d : 0).toFixed(2)}</td>
            <td>${(_e = p.stock) !== null && _e !== void 0 ? _e : 0}</td>
            <td>${(_g = (_f = p.categoria) === null || _f === void 0 ? void 0 : _f.nombre) !== null && _g !== void 0 ? _g : "—"}</td>
            <td>${p.disponible ? "✔" : "✘"}</td>
            <td>
              <button class="btn-edit-product" data-id="${p.id}">Editar</button>
              <button class="btn-delete-product" data-id="${p.id}">Eliminar</button>
            </td>
          </tr>
        `;
        })
            .join("");
        attachProductEvents();
    }
    catch (err) {
        console.error("Error cargando productos:", err);
        tbody.innerHTML = `<tr><td colspan="9">Error cargando productos</td></tr>`;
    }
}
/* =========================================
   EVENTS (AISLADOS)
========================================= */
function attachProductEvents() {
    const tbody = document.getElementById("products-tbody");
    const form = document.getElementById("prod-form");
    const previewImg = document.getElementById("prod-preview");
    if (!tbody)
        return;
    // EDITAR
    tbody.querySelectorAll(".btn-edit-product").forEach((btn) => {
        btn.addEventListener("click", async () => {
            var _a, _b, _c, _d, _e, _f, _g, _h;
            const id = Number(btn.getAttribute("data-id"));
            if (!id)
                return;
            try {
                const prod = await apiGet(`/productos/${id}`);
                document.getElementById("prod-id").value = (_a = prod.id) !== null && _a !== void 0 ? _a : "";
                document.getElementById("prod-nombre").value = (_b = prod.nombre) !== null && _b !== void 0 ? _b : "";
                document.getElementById("prod-precio").value = (_c = prod.precio) !== null && _c !== void 0 ? _c : 0;
                document.getElementById("prod-stock").value = (_d = prod.stock) !== null && _d !== void 0 ? _d : 0;
                document.getElementById("prod-descripcion").value =
                    (_e = prod.descripcion) !== null && _e !== void 0 ? _e : "";
                document.getElementById("prod-categoria").value =
                    (_h = (_g = (_f = prod.categoria) === null || _f === void 0 ? void 0 : _f.id) === null || _g === void 0 ? void 0 : _g.toString()) !== null && _h !== void 0 ? _h : "";
                document.getElementById("prod-disponible").checked =
                    !!prod.disponible;
                if (previewImg) {
                    previewImg.src = prod.urlImagen
                        ? getProductImageUrl(prod.urlImagen)
                        : "/src/assets/food/default.jpeg";
                }
                form === null || form === void 0 ? void 0 : form.scrollIntoView({ behavior: "smooth" });
            }
            catch (err) {
                console.error("Error editando producto:", err);
                alert("No se pudo cargar el producto.");
            }
        });
    });
    // ELIMINAR
    tbody.querySelectorAll(".btn-delete-product").forEach((btn) => {
        btn.addEventListener("click", async () => {
            const id = Number(btn.getAttribute("data-id"));
            if (!id)
                return;
            if (!confirm("¿Eliminar producto?"))
                return;
            try {
                await apiDelete(`/productos/${id}`);
                await loadProducts();
            }
            catch (err) {
                console.error("Error eliminando producto:", err);
                alert(err.message || "No se pudo eliminar el producto.");
            }
        });
    });
}
/* =========================================
   SAVE PRODUCT
========================================= */
export async function saveProduct(e) {
    var _a;
    e.preventDefault();
    const form = document.getElementById("prod-form");
    const fileInput = document.getElementById("prod-file");
    const previewImg = document.getElementById("prod-preview");
    if (!form)
        return;
    try {
        const idStr = document.getElementById("prod-id").value;
        const id = idStr ? Number(idStr) : undefined;
        const producto = {
            id,
            nombre: document.getElementById("prod-nombre").value,
            precio: Number(document.getElementById("prod-precio").value),
            stock: Number(document.getElementById("prod-stock").value),
            descripcion: document.getElementById("prod-descripcion").value,
            disponible: document.getElementById("prod-disponible").checked,
            categoria: {
                id: Number(document.getElementById("prod-categoria").value),
            },
        };
        const fd = new FormData();
        fd.append("nombre", producto.nombre);
        fd.append("precio", String(producto.precio));
        fd.append("stock", String(producto.stock));
        fd.append("descripcion", producto.descripcion);
        fd.append("disponible", String(producto.disponible));
        fd.append("categoriaId", String(producto.categoria.id));
        if ((_a = fileInput === null || fileInput === void 0 ? void 0 : fileInput.files) === null || _a === void 0 ? void 0 : _a.length) {
            fd.append("imagen", fileInput.files[0]);
        }
        if (id) {
            await apiPut(`/productos/${id}`, fd, true);
        }
        else {
            await apiPost(`/productos`, fd, true);
        }
        form.reset();
        if (previewImg)
            previewImg.src = "/src/assets/food/default.jpeg";
        await loadProducts();
    }
    catch (err) {
        console.error("Error guardando producto:", err);
        alert("Error guardando producto.");
    }
}
