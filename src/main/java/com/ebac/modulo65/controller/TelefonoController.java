package com.ebac.modulo65.controller;

import com.ebac.modulo65.dto.Telefono;
import com.ebac.modulo65.service.TelefonoService;
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
public class TelefonoController {

    @Autowired
    TelefonoService telefonoService;

    @GetMapping("/telefonos")
    public ResponseWrapper<List<Telefono>> obtenerTelefonos() {
        log.info("Obteniendo todos los telefonos");
        List<Telefono> telefonos = telefonoService.obtenerTelefonos();
        log.info("Se encontraron {} telefonos", telefonos.size());
        ResponseEntity<List<Telefono>> responseEntity = ResponseEntity.ok(telefonos);

        return new ResponseWrapper<>(true, "Listado de telefonos", responseEntity);
    }

    @GetMapping("/telefonos/{id}")
    public ResponseWrapper<Telefono> obtenerTelefonoPorId(@PathVariable Long id) {
        log.info("Obteniendo telefono por id {}", id);
        Optional<Telefono> telefonoOptional = telefonoService.obtenerTelefonoPorId(id);

        if (telefonoOptional.isEmpty()) {
            log.warn("No se encontró el telefono con id {}", id);
            ResponseEntity<Telefono> noEncontrado = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, "El telefono " + id + " no existe", noEncontrado);
        }

        ResponseEntity<Telefono> responseEntity = ResponseEntity.ok(telefonoOptional.get());
        return new ResponseWrapper<>(true, "Informacion del telefono " + id, responseEntity);
    }

    @PostMapping("/telefonos")
    public ResponseWrapper<Telefono> crearTelefono(@RequestBody Telefono telefono) {
        log.info("Creando telefono {}", telefono.getNumero());
        try {
            Telefono telefonoCreado = telefonoService.crearTelefono(telefono);
            log.info("Telefono creado con id {}", telefonoCreado.getIdTelefono());
            ResponseEntity<Telefono> responseEntity = ResponseEntity.created(new URI("http://localhost/telefonos")).body(telefonoCreado);
            return new ResponseWrapper<>(true, "Telefono creado exitosamente", responseEntity);
        } catch (Exception e) {
            log.error("No se pudo crear el telefono: {}", e.getMessage());
            ResponseEntity<Telefono> responseEntity = ResponseEntity.badRequest().build();
            return new ResponseWrapper<>(false, e.getMessage(), responseEntity);
        }
    }

    @PutMapping("/telefonos/{id}")
    public ResponseWrapper<Telefono> actualizarTelefono(@PathVariable Long id, @RequestBody Telefono telefonoActualizado) {
        log.info("Actualizando telefono con id {}", id);
        Optional<Telefono> telefonoOptional = telefonoService.obtenerTelefonoPorId(id);

        if (telefonoOptional.isEmpty()) {
            log.warn("No se puede actualizar: el telefono {} no existe", id);
            ResponseEntity<Telefono> responseEntity = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, "El telefono indicado no existe", responseEntity);
        }

        telefonoActualizado.setIdTelefono(telefonoOptional.get().getIdTelefono());
        // Se conserva el usuario dueño del telefono; si no, quedaría sin usuario
        telefonoActualizado.setUsuario(telefonoOptional.get().getUsuario());

        try {
            telefonoService.actualizarTelefono(telefonoActualizado);
        } catch (IllegalArgumentException e) {
            log.error("No se pudo actualizar el telefono {}: {}", id, e.getMessage());
            ResponseEntity<Telefono> responseEntity = ResponseEntity.badRequest().build();
            return new ResponseWrapper<>(false, e.getMessage(), responseEntity);
        }

        log.info("Telefono {} actualizado correctamente", id);
        ResponseEntity<Telefono> responseEntity = ResponseEntity.ok(telefonoActualizado);
        return new ResponseWrapper<>(true, "Telefono actualizado correctamente", responseEntity);
    }

    @DeleteMapping("/telefonos/{id}")
    public ResponseWrapper<Void> eliminarTelefono(@PathVariable Long id) {
        log.info("Eliminando telefono con id {}", id);
        try {
            telefonoService.eliminarTelefono(id);
        } catch (IllegalArgumentException e) {
            ResponseEntity<Void> noEncontrado = ResponseEntity.notFound().build();
            return new ResponseWrapper<>(false, e.getMessage(), noEncontrado);
        }
        log.info("Telefono {} eliminado", id);

        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        return new ResponseWrapper<>(true, "Telefono eliminado correctamente", responseEntity);
    }
}