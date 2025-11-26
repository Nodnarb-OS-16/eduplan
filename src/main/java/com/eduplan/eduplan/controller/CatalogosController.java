package com.eduplan.eduplan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduplan.eduplan.repository.AsignaturaRepository;
import com.eduplan.eduplan.repository.CursoRepository;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogosController {

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private AsignaturaRepository asignaturaRepo;

    @GetMapping("/cursos")
    public ResponseEntity<?> listarCursos() {
        return ResponseEntity.ok(cursoRepo.findAll());
    }

    @GetMapping("/asignaturas")
    public ResponseEntity<?> listarAsignaturas() {
        return ResponseEntity.ok(asignaturaRepo.findAll());
    }
}