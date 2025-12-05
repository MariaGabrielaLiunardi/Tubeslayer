package com.Tubeslayer.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // ⬅️ IMPORT INI
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors; // ⬅️ IMPORT INI

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
                
        // 1. Dapatkan HttpSession
        HttpSession session = request.getSession();

        // 2. Ambil Username/Email
        String username = authentication.getName(); 

        // 3. Ambil dan Proses Roles
        Set<String> rolesWithPrefix = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        // Konversi roles menjadi set tanpa prefix "ROLE_" untuk disimpan di session
        Set<String> rolesWithoutPrefix = rolesWithPrefix.stream()
            .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
            .collect(Collectors.toSet());
        
        // Kita asumsikan hanya ada satu peran utama untuk redirect, ambil saja yang pertama
        String primaryRole = rolesWithoutPrefix.isEmpty() ? null : rolesWithoutPrefix.iterator().next();

        // 4. SET LOGIC SESSION DI SINI
        session.setAttribute("username", username); 
        session.setAttribute("role", primaryRole); // Simpan role tanpa prefix (ADMIN, DOSEN, dll.)
        
        System.out.println("Roles user setelah login: " + rolesWithPrefix);
        System.out.println("Session di-set: username=" + username + ", role=" + primaryRole);

        // 5. Logic Redirect Berbasis Peran (TETAP SAMA)
        if (rolesWithPrefix.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (rolesWithPrefix.contains("ROLE_DOSEN")) {
            response.sendRedirect("/dosen/dashboard");
        } else if (rolesWithPrefix.contains("ROLE_MAHASISWA")) {
            response.sendRedirect("/mahasiswa/dashboard");
        } else{
            response.sendRedirect("/");
        }
    }
}