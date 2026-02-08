package com.ElVacio.runner.controller;

import com.ElVacio.runner.entity.SesionRunning;
import com.ElVacio.runner.entity.Usuario;
import com.ElVacio.runner.repository.RunningRepository;
import com.ElVacio.runner.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse; // Para exportar
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;

@Controller
public class RunningController {

    private final RunningRepository runningRepository;
    private final UsuarioRepository usuarioRepository;

    public RunningController(RunningRepository runningRepository, UsuarioRepository usuarioRepository) {
        this.runningRepository = runningRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Dashboard with user metrics
    @GetMapping("/")
    public String verPaginaPrincipal(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username);

        // Handle invalid session (user removed from database)
        if (usuarioLogueado == null) {
            return "redirect:/logout";
        }

        List<SesionRunning> sesiones = runningRepository.findByUsuario(usuarioLogueado);

        // Metrics calculation
        double totalKm = 0;
        for (SesionRunning s : sesiones) {
            totalKm += s.getDistancia();
        }

        double promedio = sesiones.isEmpty() ? 0 : totalKm / sesiones.size();

        model.addAttribute("sesiones", sesiones);
        model.addAttribute("nombreUsuario", usuarioLogueado.getNombre());
        model.addAttribute("totalKm", String.format("%.2f", totalKm));
        model.addAttribute("totalSesiones", sesiones.size());
        model.addAttribute("promedio", String.format("%.2f", promedio));

        return "home";
    }

    // Create session form
    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {
        model.addAttribute("sesion", new SesionRunning());
        return "form";
    }

    // Load session for editing
    @GetMapping("/editar/{id}")
    public String editarSesion(@PathVariable Long id, Model model, Principal principal) {
        var sesionOpt = runningRepository.findById(id);

        if (sesionOpt.isPresent()) {
            SesionRunning sesion = sesionOpt.get();

            // Authorization check
            if (sesion.getUsuario().getUsername().equals(principal.getName())) {
                model.addAttribute("sesion", sesion);
                return "editar";
            }
        }
        return "redirect:/";
    }

    // Save or update session
    @PostMapping("/guardar")
    public String guardarSesion(SesionRunning sesion, Principal principal) {
        String username = principal.getName();
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username);

        sesion.setUsuario(usuarioLogueado);
        runningRepository.save(sesion);

        return "redirect:/";
    }

    // Delete session
    @GetMapping("/borrar/{id}")
    public String borrarSesion(@PathVariable Long id, Principal principal) {
        var sesionOpt = runningRepository.findById(id);

        if (sesionOpt.isPresent() &&
                sesionOpt.get().getUsuario().getUsername().equals(principal.getName())) {
            runningRepository.deleteById(id);
        }
        return "redirect:/";
    }

    // Export sessions to CSV
    @GetMapping("/exportar")
    public void exportarCSV(HttpServletResponse response, Principal principal) throws IOException {
        // Configuramos el archivo para descarga con codificación UTF-8
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8"); // Importante para tildes
        response.setHeader("Content-Disposition", "attachment; filename=\"mis_corridas.csv\"");

        // Obtenemos los datos
        String username = principal.getName();
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username);
        List<SesionRunning> sesiones = runningRepository.findByUsuario(usuarioLogueado);

        // Escribimos el archivo
        PrintWriter writer = response.getWriter();

        // TRUCO PRO 1: BOM (Byte Order Mark)
        // Esto obliga a Excel a reconocer las tildes (á, é, ñ) correctamente.
        writer.write('\uFEFF');

        // TRUCO PRO 2: Usar PUNTO Y COMA (;)
        // Excel en español separa columnas con ";" porque la "," la usa para decimales.
        writer.println("Fecha;Distancia (km);Tiempo;Comentario"); // Encabezados con ;

        for (SesionRunning s : sesiones) {
            writer.println(
                    s.getFecha() + ";" +
                            s.getDistancia() + ";" +
                            s.getTiempo() + ";" +
                            // Reemplazamos posibles puntos y coma en el comentario para no romper la tabla
                            (s.getDescripcion() != null ? s.getDescripcion().replace(";", ",") : "")
            );
        }
    }
}