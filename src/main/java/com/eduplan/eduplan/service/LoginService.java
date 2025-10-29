package com.eduplan.eduplan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduplan.eduplan.dto.LoginRequest;
import com.eduplan.eduplan.dto.LoginResponse;
import com.eduplan.eduplan.model.Usuario;
import com.eduplan.eduplan.repository.UsuarioRepository;

@Service
public class LoginService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository
            .findByCorreoElectronicoAndContrasena(
                request.getCorreoElectronico(),
                request.getContrasena()
            )
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        return new LoginResponse(usuario);
    }
}