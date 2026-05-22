package com.foodstore.htmeleros.auth.config;

import com.foodstore.htmeleros.auth.util.Sha256Util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LegacyAwarePasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;

        // BCrypt hashes always start with $2a$ / $2b$ / $2y$
        if (encodedPassword.startsWith("$2")) {
            return bcrypt.matches(rawPassword, encodedPassword);
        }

        // Legacy SHA-256 fallback
        return Sha256Util.hash(rawPassword.toString()).equals(encodedPassword);
    }
}
