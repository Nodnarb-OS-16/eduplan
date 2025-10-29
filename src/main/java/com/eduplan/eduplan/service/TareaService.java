package com.eduplan.eduplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduplan.eduplan.model.Tarea;
import com.eduplan.eduplan.model.Usuario;
import com.eduplan.eduplan.repository.TareaRepository;
import com.eduplan.eduplan.repository.UsuarioRepository;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepo;
    
    @Autowired
    private UsuarioRepository usuarioRepo;

    // Listar tareas de un alumno
    public List<Tarea> listarPorAlumno(Integer alumnoId) {
        return tareaRepo.findByAlumnoId(alumnoId);
    }
    
    // Listar tareas de un curso
    public List<Tarea> listarPorCurso(Long cursoId) {
        return tareaRepo.findByCursoId(cursoId);
    }

    // Crear tarea (solo alumno)
    public Tarea crear(Tarea tarea, Integer alumnoId) {
        // Validar que el usuario es alumno
        Usuario alumno = usuarioRepo.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!"Alumno".equals(alumno.getRol().getRol())) {
            throw new RuntimeException("Solo los alumnos pueden crear tareas");
        }
        
        tarea.setAlumno(alumno);
        return tareaRepo.save(tarea);
    }

    // Editar tarea (solo el dueño)
    public Tarea editar(Tarea tarea, Integer alumnoId) {
        Tarea existente = tareaRepo.findById(tarea.getId())
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        // Validar que el alumno es el dueño
        if (!existente.getAlumno().getId().equals(alumnoId)) {
            throw new RuntimeException("No tienes permiso para editar esta tarea");
        }
        
        // Actualizar campos
        existente.setTitulo(tarea.getTitulo());
        existente.setDescripcion(tarea.getDescripcion());
        existente.setFecha(tarea.getFecha());
        existente.setHora(tarea.getHora());
        existente.setCurso(tarea.getCurso());
        
        return tareaRepo.save(existente);
    }

    // Eliminar tarea (solo el dueño)
    public void eliminar(Long id, Integer alumnoId) {
        Tarea tarea = tareaRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        if (!tarea.getAlumno().getId().equals(alumnoId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta tarea");
        }
        
        tareaRepo.deleteById(id);
    }
}