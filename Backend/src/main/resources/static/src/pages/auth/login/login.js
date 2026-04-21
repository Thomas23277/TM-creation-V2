import { login, loginWithGoogle } from '../../../utils/auth';
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const msg = document.getElementById('msg');
    const googleBtn = document.getElementById("googleLoginBtn");
    /* =====================================
       🔵 LOGIN CON GOOGLE
    ===================================== */
    if (googleBtn) {
        googleBtn.addEventListener("click", () => {
            loginWithGoogle();
        });
    }
    if (!form)
        return;
    /* =====================================
       🔐 LOGIN NORMAL
    ===================================== */
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (msg) {
            msg.textContent = '';
        }
        const emailInput = document.getElementById('email');
        const passwordInput = document.getElementById('password');
        const email = emailInput === null || emailInput === void 0 ? void 0 : emailInput.value.trim();
        const contrasenia = passwordInput === null || passwordInput === void 0 ? void 0 : passwordInput.value.trim();
        if (!email || !contrasenia) {
            if (msg) {
                msg.textContent = 'Debes ingresar tu correo y contraseña.';
                msg.style.color = '#c0392b';
            }
            return;
        }
        try {
            // 🔒 Desactivar botón mientras procesa
            const submitBtn = form.querySelector("button[type='submit']");
            if (submitBtn)
                submitBtn.disabled = true;
            const usuario = await login(email, contrasenia);
            if (!usuario || !usuario.rol) {
                throw new Error("Respuesta inválida del servidor");
            }
            if (msg) {
                msg.textContent = 'Inicio de sesión correcto. Redirigiendo...';
                msg.style.color = '#27ae60';
            }
            const rol = usuario.rol.toUpperCase();
            // 🚀 Redirección inmediata (ya esperamos el login)
            if (rol === "ADMIN") {
                window.location.href = "/src/pages/admin/adminHome/adminHome.html";
            }
            else {
                window.location.href = "/src/pages/store/home/home.html";
            }
        }
        catch (err) {
            if (msg) {
                if (err.message === "UNAUTHORIZED") {
                    msg.textContent = "Correo o contraseña incorrectos.";
                }
                else {
                    msg.textContent =
                        (err === null || err === void 0 ? void 0 : err.message) || 'Error al iniciar sesión. Revisa tus datos.';
                }
                msg.style.color = '#c0392b';
            }
        }
    });
});
