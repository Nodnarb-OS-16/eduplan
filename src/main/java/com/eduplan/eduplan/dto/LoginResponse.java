package com.eduplan.eduplan.dto;

import com.eduplan.eduplan.model.Usuario;

public class LoginResponse {
    private final Integer id;
    private final String nombre;
    private final String correoElectronico;
    private final String rol;

    public LoginResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correoElectronico = usuario.getCorreoElectronico();
        this.rol = usuario.getRol().getRol();
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public String getRol() {
        return rol;
    }
}
