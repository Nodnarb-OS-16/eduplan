package com.eduplan.eduplan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduplan.eduplan.dto.EstudianteRequest;
import com.eduplan.eduplan.model.Configuracion;
import com.eduplan.eduplan.model.CursoAsignaturaDocente;
import com.eduplan.eduplan.model.Usuario;
import com.eduplan.eduplan.repository.UsuarioRepository;
import com.eduplan.eduplan.service.ConfiguracionService;
import com.eduplan.eduplan.service.DirectorService;

@RestController
@RequestMapping("/api/director")
public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @Autowired
    private ConfiguracionService configuracionService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    // Middleware para validar que el usuario es Director
    private void validarDirector(Integer directorId) {
        Usuario director = usuarioRepo.findById(directorId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!"Director".equals(director.getRol().getRol())) {
            throw new RuntimeException("No tienes permisos de Director");
        }
    }

    // ============ ESTUDIANTES ============

    @GetMapping("/estudiantes")
    public ResponseEntity<?> listarEstudiantes(@RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            return ResponseEntity.ok(directorService.listarEstudiantes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/estudiantes")
    public ResponseEntity<?> crearEstudiante(@RequestBody EstudianteRequest request, 
                                              @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            Usuario estudiante = directorService.crearEstudiante(request);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/estudiantes/{id}")
    public ResponseEntity<?> actualizarEstudiante(@PathVariable Integer id, 
                                                   @RequestBody EstudianteRequest request,
                                                   @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            request.setId(id);
            Usuario estudiante = directorService.actualizarEstudiante(request);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/estudiantes/{id}")
    public ResponseEntity<?> eliminarEstudiante(@PathVariable Integer id, 
                                                 @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            directorService.eliminarEstudiante(id);
            return ResponseEntity.ok().body(Map.of("mensaje", "Estudiante eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============ DOCENTES ============

    @GetMapping("/docentes")
    public ResponseEntity<?> listarDocentes(@RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            return ResponseEntity.ok(directorService.listarDocentes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/docentes")
    public ResponseEntity<?> crearDocente(@RequestBody Map<String, String> request,
                                          @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            Usuario docente = directorService.crearDocente(
                request.get("nombre"),
                request.get("apellidoPaterno"),
                request.get("apellidoMaterno"),
                request.get("correoElectronico"),
                request.get("contrasena")
            );
            return ResponseEntity.ok(docente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/docentes/{id}")
    public ResponseEntity<?> actualizarDocente(@PathVariable Integer id,
                                                @RequestBody Map<String, String> request,
                                                @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            Usuario docente = directorService.actualizarDocente(
                id,
                request.get("nombre"),
                request.get("apellidoPaterno"),
                request.get("apellidoMaterno"),
                request.get("correoElectronico"),
                request.get("contrasena")
            );
            return ResponseEntity.ok(docente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/docentes/{id}")
    public ResponseEntity<?> eliminarDocente(@PathVariable Integer id,
                                             @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            directorService.eliminarDocente(id);
            return ResponseEntity.ok().body(Map.of("mensaje", "Docente eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============ ASIGNACIONES DOCENTE ============

    @GetMapping("/docentes/{docenteId}/asignaciones")
    public ResponseEntity<?> listarAsignacionesDocente(@PathVariable Integer docenteId,
                                                        @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            List<CursoAsignaturaDocente> asignaciones = directorService.listarAsignacionesDocente(docenteId);
            return ResponseEntity.ok(asignaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/asignaciones")
    public ResponseEntity<?> listarTodasAsignaciones(@RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            List<CursoAsignaturaDocente> asignaciones = directorService.listarTodasLasAsignaciones();
            return ResponseEntity.ok(asignaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/asignaciones")
    public ResponseEntity<?> asignarDocenteACursoAsignatura(
            @RequestBody Map<String, Object> request,
            @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            
            Integer docenteId = (Integer) request.get("docenteId");
            Long cursoId = Long.valueOf(request.get("cursoId").toString());
            Long asignaturaId = Long.valueOf(request.get("asignaturaId").toString());
            Integer anioEscolar = request.get("anioEscolar") != null ? 
                (Integer) request.get("anioEscolar") : 
                java.time.Year.now().getValue();

            CursoAsignaturaDocente cad = directorService.asignarDocenteACursoAsignatura(
                docenteId, cursoId, asignaturaId, anioEscolar
            );
            return ResponseEntity.ok(cad);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/asignaciones/{asignacionId}")
    public ResponseEntity<?> eliminarAsignacionDocente(@PathVariable Long asignacionId,
                                                        @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            directorService.eliminarAsignacionDocente(asignacionId);
            return ResponseEntity.ok().body(Map.of("mensaje", "Asignación eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============ CONFIGURACIÓN ============

    @GetMapping("/configuracion")
    public ResponseEntity<?> obtenerConfiguracion(@RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            return ResponseEntity.ok(configuracionService.obtenerTodas());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PutMapping("/configuracion/max-evaluaciones")
    public ResponseEntity<?> actualizarMaxEvaluaciones(
            @RequestBody Map<String, Integer> request,
            @RequestParam Integer directorId) {
        try {
            validarDirector(directorId);
            Integer nuevoValor = request.get("maxEvaluacionesPorDia");
            
            if (nuevoValor == null || nuevoValor < 1 || nuevoValor > 10) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El valor debe estar entre 1 y 10"));
            }

            Configuracion config = configuracionService.actualizarMaxEvaluaciones(nuevoValor, directorId);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Configuración actualizada exitosamente",
                "configuracion", config
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}