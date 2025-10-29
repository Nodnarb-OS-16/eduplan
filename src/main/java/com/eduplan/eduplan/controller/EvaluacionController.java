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
import org.springframework.web.bind.annotation.RestController;

import com.eduplan.eduplan.model.Evaluacion;
import com.eduplan.eduplan.service.EvaluacionService;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService service;

    @GetMapping("/curso/{id}")
    public List<Evaluacion> listar(@PathVariable Long id) {
        return service.listarPorCurso(id);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Evaluacion eval) {
        try {
            return ResponseEntity.ok(service.crear(eval));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Evaluacion eval) {
        eval.setId(id);
        return ResponseEntity.ok(service.editar(eval));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
        
    }
}