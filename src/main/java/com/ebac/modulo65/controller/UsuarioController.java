package com.ebac.modulo65.controller;

import com.ebac.modulo65.dto.Usuario;
import com.ebac.modulo65.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseWrapper<List<Usuario>> obtenerUsuarios() {
        log.info("Obteniendo todos los usuarios");
        List<Usuario> usuarioList = usuarioService.obtenerUsuarios();
        log.info("Se encontraron {} usuarios", usuarioList.size());
        ResponseEntity<List<Usuario>> responseEntity = ResponseEntity.ok(usuarioList);

        return new ResponseWrapper<>(true, "Listado de usuarios", responseEntity);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseWrapper<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Obteniendo usuario por id {}", id);
        Optional<Usuario> usuarioOptional = usuarioService.obtenerUsuarioPorId(id);

        if (usuarioOptional.isEmpty()) {
            log.warn("No se encontró el usuario con id {}", id);
            ResponseEntity<Usuario> noEncontrado = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, "El usuario " + id + " no existe", noEncontrado);
        }

        ResponseEntity<Usuario> responseEntity = ResponseEntity.ok(usuarioOptional.get());
        return new ResponseWrapper<>(true, "Informacion del usuario " + id, responseEntity);
    }

    @PostMapping("/usuarios")
    public ResponseWrapper<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        log.info("Creando usuario {}", usuario.getNombre());
        try {
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            log.info("Usuario creado con id {}", usuarioCreado.getIdUsuario());
            ResponseEntity<Usuario> responseEntity = ResponseEntity.created(new URI("http://localhost/usuarios")).body(usuarioCreado);
            return new ResponseWrapper<>(true, "Usuario creado exitosamente", responseEntity);
        } catch (Exception e) {
            log.error("No se pudo crear el usuario: {}", e.getMessage());
            ResponseEntity<Usuario> responseEntity = ResponseEntity.badRequest().build();
            return new ResponseWrapper<>(false, e.getMessage(), responseEntity);
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseWrapper<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        log.info("Actualizando usuario con id {}", id);
        Optional<Usuario> usuarioOptional = usuarioService.obtenerUsuarioPorId(id);

        if (usuarioOptional.isEmpty()) {
            log.warn("No se puede actualizar: el usuario {} no existe", id);
            ResponseEntity<Usuario> responseEntity = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, "El usuario indicado no existe", responseEntity);
        }

        usuarioActualizado.setIdUsuario(usuarioOptional.get().getIdUsuario());

        try {
            usuarioService.actualizarUsuario(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            log.error("No se pudo actualizar el usuario {}: {}", id, e.getMessage());
            ResponseEntity<Usuario> responseEntity = ResponseEntity.badRequest().build();
            return new ResponseWrapper<>(false, e.getMessage(), responseEntity);
        }

        log.info("Usuario {} actualizado correctamente", id);
        ResponseEntity<Usuario> responseEntity = ResponseEntity.ok(usuarioActualizado);
        return new ResponseWrapper<>(true, "Usuario actualizado correctamente", responseEntity);
    }


    @DeleteMapping("/usuarios/{id}")
    public ResponseWrapper<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("Eliminando usuario con id {}", id);
        try {
            usuarioService.eliminarUsuario(id);
        } catch (IllegalArgumentException e) {
            ResponseEntity<Void> noEncontrado = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, e.getMessage(), noEncontrado);
        }
        log.info("Usuario {} eliminado", id);

        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        return new ResponseWrapper<>(true, "Usuario eliminado correctamente", responseEntity);
    }
}