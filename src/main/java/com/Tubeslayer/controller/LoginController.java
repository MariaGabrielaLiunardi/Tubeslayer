// package com.Tubeslayer.controller;

// import com.Tubeslayer.service.AuthService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;

// import com.Tubeslayer.entity.User;
// import java.util.List;

// import jakarta.servlet.http.HttpSession;

// @Controller
// public class LoginController {
//     @Autowired
//     private AuthService authService;

//     @GetMapping("/login")
//     public String loginView(HttpSession session) {
//         if (session.getAttribute("user") != null) {
//             // Jika sudah login, langsung ke dashboard
//             return "redirect:/dashboard";
//         }
//         return "login"; 
//     }

//     @PostMapping("/login")
//     public String login(String email, String password,
//                         HttpSession session, Model model) {
//         User user = authService.login(email, password);

//         if (user != null) {

//             UsernamePasswordAuthenticationToken authToken =
//                     new UsernamePasswordAuthenticationToken(
//                             user.getEmail(),
//                             user.getPassword(),
//                             List.of(() -> "ROLE_" + user.getRole())
//                     );

//             SecurityContextHolder.getContext().setAuthentication(authToken);

//             session.setAttribute("SPRING_SECURITY_CONTEXT",
//                     SecurityContextHolder.getContext());

//             return "redirect:/dashboard";
//         } else {
//             model.addAttribute("error", "Username atau password salah");
//             return "login";
//         }
//     }

//     @GetMapping("/dashboard")
//     public String dashboard(HttpSession session) {
//         if (session.getAttribute("user") == null) {
//             // Jika belum login, redirect ke login
//             return "redirect:/login";
//         }
//         return "dashboard"; 
//     }

//     // Logout: hapus session
//     @GetMapping("/logout")
//     public String logout(HttpSession session) {
//         session.invalidate();
//         return "redirect:/";
//     }
// }
