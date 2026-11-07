package com.ebac.modulo65.service;

import com.ebac.modulo65.dto.Usuario;
import com.ebac.modulo65.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) throws Exception {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            log.error("Usuario rechazado: el nombre viene vacio");
            throw new Exception("El nombre del usuario es obligatorio");
        }
        if (usuario.getEdad() >= 18) {
            log.info("Guardando usuario {} en la base de datos", usuario.getNombre());
            return usuarioRepository.save(usuario);
        }
        log.error("Usuario rechazado por edad: {}", usuario.getEdad());
        throw new Exception("No se permiten usuarios menores de 18 años");
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long idUsuario) {
        log.info("Buscando usuario {} en la base de datos", idUsuario);
        return usuarioRepository.findById(idUsuario);
    }

    public List<Usuario> obtenerUsuarios() {
        log.info("Consultando todos los usuarios en la base de datos");
        return usuarioRepository.findAll();
    }

    public void actualizarUsuario(Usuario usuario) {
        if (usuario.getEdad() < 18) {
            log.error("Actualizacion rechazada por edad: {}", usuario.getEdad());
            throw new IllegalArgumentException("No se permiten usuarios menores de 18 años");
        }
        log.info("Guardando cambios del usuario {}", usuario.getIdUsuario());
        usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            log.warn("No se puede eliminar: el usuario {} no existe", id);
            throw new IllegalArgumentException("El usuario " + id + " no existe");
        }
        log.info("Borrando usuario {} de la base de datos", id);
        usuarioRepository.deleteById(id);
    }
}