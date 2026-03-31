import { register } from '../../../utils/auth';
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('register-form');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const passInput = document.getElementById('password');
    const msg = document.getElementById('msg');
    if (!form || !nameInput || !emailInput || !passInput)
        return;
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (msg)
            msg.textContent = "";
        const nombre = nameInput.value.trim();
        const apellido = ""; // backend lo admite vacío
        const email = emailInput.value.trim();
        const contrasenia = passInput.value.trim();
        /* =====================================
           🧠 VALIDACIONES BÁSICAS
        ===================================== */
        if (!nombre || !email || !contrasenia) {
            if (msg) {
                msg.textContent = 'Completá todos los campos obligatorios.';
                msg.style.color = '#c0392b';
            }
            return;
        }
        if (!email.includes("@")) {
            if (msg) {
                msg.textContent = 'Ingresá un correo válido.';
                msg.style.color = '#c0392b';
            }
            return;
        }
        if (contrasenia.length < 4) {
            if (msg) {
                msg.textContent = 'La contraseña debe tener al menos 4 caracteres.';
                msg.style.color = '#c0392b';
            }
            return;
        }
        try {
            await register(nombre, apellido, email, contrasenia);
            if (msg) {
                msg.textContent = 'Registro exitoso. Redirigiendo al login...';
                msg.style.color = '#27ae60';
            }
            form.reset();
            setTimeout(() => {
                window.location.href = '/src/pages/auth/login/login.html';
            }, 800);
        }
        catch (err) {
            if (msg) {
                msg.textContent =
                    (err === null || err === void 0 ? void 0 : err.message) || 'Error en el registro. Intentá nuevamente.';
                msg.style.color = '#c0392b';
            }
        }
    });
});
