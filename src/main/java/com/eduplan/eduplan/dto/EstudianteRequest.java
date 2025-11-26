package com.eduplan.eduplan.dto;

public class EstudianteRequest {
    private Integer id; // null para crear, con valor para actualizar
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correoElectronico;
    private String contrasena;
    private Long cursoId;
    private Integer anioEscolar;

    // Constructores
    public EstudianteRequest() {
    }

    public EstudianteRequest(String nombre, String apellidoPaterno, String apellidoMaterno, 
                            String correoElectronico, String contrasena, Long cursoId, Integer anioEscolar) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
        this.cursoId = cursoId;
        this.anioEscolar = anioEscolar;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Integer getAnioEscolar() {
        return anioEscolar;
    }

    public void setAnioEscolar(Integer anioEscolar) {
        this.anioEscolar = anioEscolar;
    }

    @Override
    public String toString() {
        return "EstudianteRequest{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", cursoId=" + cursoId +
                ", anioEscolar=" + anioEscolar +
                '}';
    }
}