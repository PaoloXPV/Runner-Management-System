package com.ElVacio.runner.controller;

import com.ElVacio.runner.entity.Usuario;
import com.ElVacio.runner.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORTANTE
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Registration form
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // User registration
    @PostMapping("/registrarse")
    public String registrarUsuario(Usuario usuario) {
        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);

        usuarioRepository.save(usuario);
        return "redirect:/login";
    }

    // Delete authenticated user account
    @PostMapping("/eliminar-cuenta")
    public String eliminarCuenta(Principal principal, HttpServletRequest request) {
        // Get authenticated username
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario != null) {
            // Remove user from database
            usuarioRepository.delete(usuario);
        }

        // Force logout
        try {
            request.logout();
        } catch (Exception ignored) {
        }

        return "redirect:/login?eliminado";
    }
}