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

import com.eduplan.eduplan.model.Evaluacion;
import com.eduplan.eduplan.service.EvaluacionService;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService service;

    // Listar evaluaciones por curso
    @GetMapping("/curso/{cursoId}")
    public List<Evaluacion> listarPorCurso(@PathVariable Long cursoId) {
        return service.listarPorCurso(cursoId);
    }
    
    // Listar evaluaciones de un docente
    @GetMapping("/docente/{docenteId}")
    public List<Evaluacion> listarPorDocente(@PathVariable Integer docenteId) {
        return service.listarPorDocente(docenteId);
    }
    
    // Listar evaluaciones que ve un alumno
    @GetMapping("/alumno/{alumnoId}")
    public List<Evaluacion> listarPorAlumno(@PathVariable Integer alumnoId) {
        return service.listarPorAlumno(alumnoId);
    }

    // Crear evaluación
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Evaluacion eval, @RequestParam Integer docenteId) {
        try {
            return ResponseEntity.ok(service.crear(eval, docenteId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Editar evaluación
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Evaluacion eval, @RequestParam Integer docenteId) {
        eval.setId(id);
        try {
            return ResponseEntity.ok(service.editar(eval, docenteId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Eliminar evaluación
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam Integer docenteId) {
        try {
            service.eliminar(id, docenteId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}