package com.eduplan.eduplan.service;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduplan.eduplan.dto.EstudianteRequest;
import com.eduplan.eduplan.model.Asignatura;
import com.eduplan.eduplan.model.Curso;
import com.eduplan.eduplan.model.CursoAlumno;
import com.eduplan.eduplan.model.CursoAsignaturaDocente;
import com.eduplan.eduplan.model.Rol;
import com.eduplan.eduplan.model.Usuario;
import com.eduplan.eduplan.repository.AsignaturaRepository;
import com.eduplan.eduplan.repository.CursoAlumnoRepository;
import com.eduplan.eduplan.repository.CursoAsignaturaDocenteRepository;
import com.eduplan.eduplan.repository.CursoRepository;
import com.eduplan.eduplan.repository.UsuarioRepository;

@Service
public class DirectorService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private CursoAlumnoRepository cursoAlumnoRepo;

    @Autowired
    private CursoAsignaturaDocenteRepository cadRepo;

    @Autowired
    private AsignaturaRepository asignaturaRepo;

    // Constantes para los roles
    private static final int ROL_DIRECTOR = 1;
    private static final int ROL_DOCENTE = 2;
    private static final int ROL_ALUMNO = 3;

    // ============ VALIDACIONES ============

    private void validarDirector(Integer directorId) {
        Usuario director = usuarioRepo.findById(directorId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (director.getRol().getId() != ROL_DIRECTOR) {
            throw new RuntimeException("No tienes permisos de Director");
        }
    }


    private void validarCorreoUnico(String correo, Integer excludeId) {
        List<Usuario> usuarios = usuarioRepo.findAll();
        boolean correoExiste = usuarios.stream()
            .filter(u -> !u.getId().equals(excludeId))
            .anyMatch(u -> u.getCorreoElectronico().equalsIgnoreCase(correo));
        
        if (correoExiste) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }
    }

    // ============ ESTUDIANTES ============

    /**
     * Crea un nuevo estudiante y lo asigna a un curso
     */
    @Transactional
    public Usuario crearEstudiante(EstudianteRequest request) {
        // Validar que el correo no exista
        validarCorreoUnico(request.getCorreoElectronico(), null);

        // Validar campos obligatorios
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (request.getCorreoElectronico() == null || request.getCorreoElectronico().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        if (request.getContrasena() == null || request.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        // Crear usuario estudiante
        Usuario alumno = new Usuario();
        alumno.setNombre(request.getNombre());
        alumno.setApellidoPaterno(request.getApellidoPaterno() != null ? request.getApellidoPaterno() : "");
        alumno.setApellidoMaterno(request.getApellidoMaterno() != null ? request.getApellidoMaterno() : "");
        alumno.setCorreoElectronico(request.getCorreoElectronico());
        alumno.setContrasena(request.getContrasena()); // TODO: Encriptar con BCrypt

        // Asignar rol de alumno
        Rol rolAlumno = new Rol();
        rolAlumno.setId(ROL_ALUMNO);
        alumno.setRol(rolAlumno);

        // Guardar el alumno
        alumno = usuarioRepo.save(alumno);

        // Asignar curso si se especificó
        if (request.getCursoId() != null) {
            Integer anioEscolar = request.getAnioEscolar() != null ? 
                request.getAnioEscolar() : Year.now().getValue();
            asignarCursoAlumno(alumno.getId(), request.getCursoId(), anioEscolar);
        }

        return alumno;
    }

    /**
     * Actualiza los datos de un estudiante existente
     */
    @Transactional
    public Usuario actualizarEstudiante(EstudianteRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio para actualizar");
        }

        Usuario alumno = usuarioRepo.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Verificar que sea un alumno
        if (alumno.getRol().getId() != ROL_ALUMNO) {
            throw new RuntimeException("El usuario no es un estudiante");
        }

        // Validar correo único (excluyendo el ID actual)
        if (request.getCorreoElectronico() != null && 
            !request.getCorreoElectronico().equals(alumno.getCorreoElectronico())) {
            validarCorreoUnico(request.getCorreoElectronico(), alumno.getId());
        }

        // Actualizar datos básicos
        if (request.getNombre() != null) {
            alumno.setNombre(request.getNombre());
        }
        if (request.getApellidoPaterno() != null) {
            alumno.setApellidoPaterno(request.getApellidoPaterno());
        }
        if (request.getApellidoMaterno() != null) {
            alumno.setApellidoMaterno(request.getApellidoMaterno());
        }
        if (request.getCorreoElectronico() != null) {
            alumno.setCorreoElectronico(request.getCorreoElectronico());
        }
        if (request.getContrasena() != null && !request.getContrasena().trim().isEmpty()) {
            alumno.setContrasena(request.getContrasena()); // TODO: Encriptar con BCrypt
        }

        alumno = usuarioRepo.save(alumno);

        // Actualizar asignación de curso si se especificó
        if (request.getCursoId() != null) {
            Integer anioEscolar = request.getAnioEscolar() != null ? 
                request.getAnioEscolar() : Year.now().getValue();
            
            // Eliminar asignaciones anteriores del mismo año
            List<CursoAlumno> asignacionesExistentes = cursoAlumnoRepo.findByAlumnoId(alumno.getId());
            List<CursoAlumno> asignacionesDelAnio = asignacionesExistentes.stream()
                .filter(ca -> ca.getAnioEscolar().equals(anioEscolar))
                .collect(Collectors.toList());
            
            cursoAlumnoRepo.deleteAll(asignacionesDelAnio);

            // Crear nueva asignación
            asignarCursoAlumno(alumno.getId(), request.getCursoId(), anioEscolar);
        }

        return alumno;
    }

    /**
     * Asigna un alumno a un curso específico
     */
    private void asignarCursoAlumno(Integer alumnoId, Long cursoId, Integer anioEscolar) {
        Curso curso = cursoRepo.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Usuario alumno = usuarioRepo.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        CursoAlumno ca = new CursoAlumno();
        ca.setCurso(curso);
        ca.setAlumno(alumno);
        ca.setAnioEscolar(anioEscolar);

        cursoAlumnoRepo.save(ca);
    }

    /**
     * Lista todos los estudiantes del sistema
     */
    public List<Usuario> listarEstudiantes() {
        return usuarioRepo.findAll().stream()
            .filter(u -> u.getRol() != null && u.getRol().getId() == ROL_ALUMNO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un estudiante por su ID
     */
    public Usuario obtenerEstudiante(Integer id) {
        Usuario alumno = usuarioRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        if (alumno.getRol().getId() != ROL_ALUMNO) {
            throw new RuntimeException("El usuario no es un estudiante");
        }
        
        return alumno;
    }

    /**
     * Elimina un estudiante del sistema
     */
    @Transactional
    public void eliminarEstudiante(Integer id) {
        Usuario alumno = usuarioRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        if (alumno.getRol().getId() != ROL_ALUMNO) {
            throw new RuntimeException("El usuario no es un estudiante");
        }

        // Al eliminar el usuario, las relaciones en cascada se eliminarán automáticamente
        usuarioRepo.deleteById(id);
    }

    // ============ DOCENTES ============

    /**
     * Crea un nuevo docente
     */
    @Transactional
    public Usuario crearDocente(String nombre, String apellidoPaterno, String apellidoMaterno, 
                                 String correoElectronico, String contrasena) {
        // Validar campos obligatorios
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (correoElectronico == null || correoElectronico.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        // Validar que el correo no exista
        validarCorreoUnico(correoElectronico, null);

        // Crear docente
        Usuario docente = new Usuario();
        docente.setNombre(nombre);
        docente.setApellidoPaterno(apellidoPaterno != null ? apellidoPaterno : "");
        docente.setApellidoMaterno(apellidoMaterno != null ? apellidoMaterno : "");
        docente.setCorreoElectronico(correoElectronico);
        docente.setContrasena(contrasena); // TODO: Encriptar con BCrypt

        // Asignar rol de docente
        Rol rolDocente = new Rol();
        rolDocente.setId(ROL_DOCENTE);
        docente.setRol(rolDocente);

        return usuarioRepo.save(docente);
    }

    /**
     * Actualiza los datos de un docente existente
     */
    @Transactional
    public Usuario actualizarDocente(Integer id, String nombre, String apellidoPaterno, 
                                      String apellidoMaterno, String correoElectronico, String contrasena) {
        Usuario docente = usuarioRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        // Verificar que sea un docente
        if (docente.getRol().getId() != ROL_DOCENTE) {
            throw new RuntimeException("El usuario no es un docente");
        }

        // Validar correo único (excluyendo el ID actual)
        if (correoElectronico != null && !correoElectronico.equals(docente.getCorreoElectronico())) {
            validarCorreoUnico(correoElectronico, docente.getId());
        }

        // Actualizar datos
        if (nombre != null) {
            docente.setNombre(nombre);
        }
        if (apellidoPaterno != null) {
            docente.setApellidoPaterno(apellidoPaterno);
        }
        if (apellidoMaterno != null) {
            docente.setApellidoMaterno(apellidoMaterno);
        }
        if (correoElectronico != null) {
            docente.setCorreoElectronico(correoElectronico);
        }
        if (contrasena != null && !contrasena.trim().isEmpty()) {
            docente.setContrasena(contrasena); // TODO: Encriptar con BCrypt
        }

        return usuarioRepo.save(docente);
    }

    /**
     * Lista todos los docentes del sistema
     */
    public List<Usuario> listarDocentes() {
        return usuarioRepo.findAll().stream()
            .filter(u -> u.getRol() != null && u.getRol().getId() == ROL_DOCENTE)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un docente por su ID
     */
    public Usuario obtenerDocente(Integer id) {
        Usuario docente = usuarioRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        
        if (docente.getRol().getId() != ROL_DOCENTE) {
            throw new RuntimeException("El usuario no es un docente");
        }
        
        return docente;
    }

    /**
     * Elimina un docente del sistema
     */
    @Transactional
    public void eliminarDocente(Integer id) {
        Usuario docente = usuarioRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        if (docente.getRol().getId() != ROL_DOCENTE) {
            throw new RuntimeException("El usuario no es un docente");
        }

        // Al eliminar el usuario, las relaciones en cascada se eliminarán automáticamente
        usuarioRepo.deleteById(id);
    }

    // ============ ASIGNACIONES DOCENTE ============

    /**
     * Asigna un docente a un curso y asignatura específica
     */
    @Transactional
    public CursoAsignaturaDocente asignarDocenteACursoAsignatura(Integer docenteId, Long cursoId, 
                                                                  Long asignaturaId, Integer anioEscolar) {
        // Validar que el usuario sea docente
        Usuario docente = usuarioRepo.findById(docenteId)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        if (docente.getRol().getId() != ROL_DOCENTE) {
            throw new RuntimeException("El usuario no es un docente");
        }

        // Validar que exista el curso
        Curso curso = cursoRepo.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Validar que exista la asignatura
        Asignatura asignatura = asignaturaRepo.findById(asignaturaId)
            .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));

        // Crear la asignación
        CursoAsignaturaDocente cad = new CursoAsignaturaDocente();
        cad.setDocente(docente);
        cad.setCurso(curso);
        cad.setAsignatura(asignatura);
        cad.setAnioEscolar(anioEscolar != null ? anioEscolar : Year.now().getValue());

        return cadRepo.save(cad);
    }

    /**
     * Elimina una asignación de docente a curso-asignatura
     */
    @Transactional
    public void eliminarAsignacionDocente(Long asignacionId) {
        if (!cadRepo.existsById(asignacionId)) {
            throw new RuntimeException("Asignación no encontrada");
        }
        cadRepo.deleteById(asignacionId);
    }

    /**
     * Lista todas las asignaciones de un docente específico
     */
    public List<CursoAsignaturaDocente> listarAsignacionesDocente(Integer docenteId) {
        // Validar que el docente exista
        Usuario docente = usuarioRepo.findById(docenteId)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        if (docente.getRol().getId() != ROL_DOCENTE) {
            throw new RuntimeException("El usuario no es un docente");
        }

        return cadRepo.findByDocenteId(docenteId);
    }

    /**
     * Lista todas las asignaciones del sistema
     */
    public List<CursoAsignaturaDocente> listarTodasLasAsignaciones() {
        return cadRepo.findAll();
    }
}