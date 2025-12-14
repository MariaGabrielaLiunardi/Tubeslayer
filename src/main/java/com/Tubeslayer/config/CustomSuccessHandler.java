package com.Tubeslayer.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors; 

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
                
        HttpSession session = request.getSession();

        String username = authentication.getName(); 

        Set<String> rolesWithPrefix = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        Set<String> rolesWithoutPrefix = rolesWithPrefix.stream()
            .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
            .collect(Collectors.toSet());
        
        String primaryRole = rolesWithoutPrefix.isEmpty() ? null : rolesWithoutPrefix.iterator().next();

        session.setAttribute("username", username); 
        session.setAttribute("role", primaryRole); 
        
        System.out.println("Roles user setelah login: " + rolesWithPrefix);
        System.out.println("Session di-set: username=" + username + ", role=" + primaryRole);

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