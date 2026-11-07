package com.ebac.modulo65.service;

import com.ebac.modulo65.dto.Telefono;
import com.ebac.modulo65.dto.Usuario;
import com.ebac.modulo65.repository.TelefonoRepository;
import com.ebac.modulo65.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TelefonoService {

    @Autowired
    TelefonoRepository telefonoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public Telefono crearTelefono(Telefono telefono) throws Exception {
        validarNumero(telefono);

        if (telefono.getIdUsuario() == null) {
            log.error("Telefono rechazado: no trae idUsuario");
            throw new IllegalArgumentException("Debe indicar el idUsuario al que pertenece el telefono");
        }

        Usuario usuario = usuarioRepository.findById(telefono.getIdUsuario())
                .orElseThrow(() -> {
                    log.error("Telefono rechazado: el usuario {} no existe", telefono.getIdUsuario());
                    return new IllegalArgumentException(
                            "El usuario " + telefono.getIdUsuario() + " no existe, no se puede guardar el telefono");
                });

        telefono.setUsuario(usuario);
        log.info("Guardando telefono {} del usuario {}", telefono.getNumero(), usuario.getIdUsuario());
        return telefonoRepository.save(telefono);
    }

    public Optional<Telefono> obtenerTelefonoPorId(Long idTelefono) {
        log.info("Buscando telefono {} en la base de datos", idTelefono);
        return telefonoRepository.findById(idTelefono);
    }

    public List<Telefono> obtenerTelefonos() {
        log.info("Consultando todos los telefonos en la base de datos");
        return telefonoRepository.findAll();
    }

    public void actualizarTelefono(Telefono telefono) {
        validarNumero(telefono);
        log.info("Guardando cambios del telefono {}", telefono.getIdTelefono());
        telefonoRepository.save(telefono);
    }

    public void eliminarTelefono(Long id) {
        if (!telefonoRepository.existsById(id)) {
            log.warn("No se puede eliminar: el telefono {} no existe", id);
            throw new IllegalArgumentException("El telefono " + id + " no existe");
        }
        log.info("Borrando telefono {} de la base de datos", id);
        telefonoRepository.deleteById(id);
    }

    private void validarNumero(Telefono telefono) {
        if (telefono.getNumero() == null || telefono.getNumero().isBlank()) {
            log.error("Telefono rechazado: el numero viene vacio");
            throw new IllegalArgumentException("El numero de telefono es obligatorio");
        }
        if (telefono.getNumero().length() > 15) {
            log.error("Telefono rechazado por longitud: {}", telefono.getNumero());
            throw new IllegalArgumentException("Telefono invalido");
        }
    }
}