package com.eduplan.eduplan.controller;

import java.util.List;

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

import com.eduplan.eduplan.model.Tarea;
import com.eduplan.eduplan.service.TareaService;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @Autowired
    private TareaService service;

    // Listar tareas de un alumno
    @GetMapping("/alumno/{alumnoId}")
    public List<Tarea> listarPorAlumno(@PathVariable Integer alumnoId) {
        return service.listarPorAlumno(alumnoId);
    }
    
    // Listar tareas de un curso
    @GetMapping("/curso/{cursoId}")
    public List<Tarea> listarPorCurso(@PathVariable Long cursoId) {
        return service.listarPorCurso(cursoId);
    }

    // Crear tarea
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Tarea tarea, @RequestParam Integer alumnoId) {
        try {
            return ResponseEntity.ok(service.crear(tarea, alumnoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Editar tarea
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Tarea tarea, @RequestParam Integer alumnoId) {
        tarea.setId(id);
        try {
            return ResponseEntity.ok(service.editar(tarea, alumnoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Eliminar tarea
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam Integer alumnoId) {
        try {
            service.eliminar(id, alumnoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}