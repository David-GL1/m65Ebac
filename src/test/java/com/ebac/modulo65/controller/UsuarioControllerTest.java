package com.ebac.modulo65.controller;

import com.ebac.modulo65.dto.Usuario;
import com.ebac.modulo65.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    UsuarioService usuarioService;

    @InjectMocks
    UsuarioController usuarioController;

    @Test
    void obtenerUsuarios() {
        int usuarios = 5;
        List<Usuario> usuariosListExpected = crearUsuarios(usuarios);

        when(usuarioService.obtenerUsuarios()).thenReturn(usuariosListExpected);

        ResponseWrapper<List<Usuario>> wrapper = usuarioController.obtenerUsuarios();
        List<Usuario> usuariosListActual = wrapper.getResponseEntity().getBody();

        assertTrue(wrapper.isSuccess());
        assertEquals(200, wrapper.getResponseEntity().getStatusCode().value());
        assertEquals(usuarios, usuariosListActual.size());
        assertEquals(usuariosListExpected, usuariosListActual);
    }

    @Test
    void obtenerUsuariosCuandoNoExisten() {
        when(usuarioService.obtenerUsuarios()).thenReturn(List.of());

        ResponseWrapper<List<Usuario>> wrapper = usuarioController.obtenerUsuarios();

        assertTrue(wrapper.getResponseEntity().getBody().isEmpty());
        verify(usuarioService, times(1)).obtenerUsuarios();
    }

    @Test
    void obtenerUsuarioPorId() {
        long idUsuario = 1;
        Optional<Usuario> usuarioExpected = Optional.of(crearUsuarios(1).get(0));

        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(usuarioExpected);

        ResponseWrapper<Usuario> wrapper = usuarioController.obtenerUsuarioPorId(idUsuario);
        Usuario usuarioActual = wrapper.getResponseEntity().getBody();

        assertEquals(200, wrapper.getResponseEntity().getStatusCode().value());
        assertNotNull(usuarioActual);
        assertEquals("Nombre1", usuarioActual.getNombre());
    }

    @Test
    void obtenerUsuarioPorIdCuandoNoExiste() {
        long idUsuario = 1;

        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(Optional.empty());

        ResponseWrapper<Usuario> wrapper = usuarioController.obtenerUsuarioPorId(idUsuario);

        assertEquals(404, wrapper.getResponseEntity().getStatusCode().value());
        assertNull(wrapper.getResponseEntity().getBody());
    }

    @Test
    void crearUsuario() throws Exception {
        Usuario usuarioExpected = crearUsuarios(1).get(0);

        when(usuarioService.crearUsuario(usuarioExpected)).thenReturn(usuarioExpected);

        ResponseWrapper<Usuario> wrapper = usuarioController.crearUsuario(usuarioExpected);
        Usuario usuarioActual = wrapper.getResponseEntity().getBody();

        assertTrue(wrapper.isSuccess());
        assertEquals(201, wrapper.getResponseEntity().getStatusCode().value());
        assertNotNull(usuarioActual);
        assertEquals(usuarioExpected, usuarioActual);
    }

    @Test
    void actualizarUsuario() {
        int idUsuario = 5;
        String nombreActualizado = "Beatriz";
        int edadActualizada = 25;

        Usuario usuarioAntiguo = new Usuario();
        usuarioAntiguo.setIdUsuario(idUsuario);
        usuarioAntiguo.setNombre("Julieta");
        usuarioAntiguo.setEdad(28);

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre(nombreActualizado);
        usuarioActualizado.setEdad(edadActualizada);

        when(usuarioService.obtenerUsuarioPorId((long) idUsuario)).thenReturn(Optional.of(usuarioAntiguo));
        doNothing().when(usuarioService).actualizarUsuario(usuarioActualizado);

        ResponseWrapper<Usuario> wrapper = usuarioController.actualizarUsuario((long) idUsuario, usuarioActualizado);
        Usuario usuarioActual = wrapper.getResponseEntity().getBody();

        assertTrue(wrapper.isSuccess());
        assertEquals(200, wrapper.getResponseEntity().getStatusCode().value());
        assertNotNull(usuarioActual);
        assertEquals(idUsuario, usuarioActual.getIdUsuario());
        assertEquals(nombreActualizado, usuarioActual.getNombre());
        assertEquals(edadActualizada, usuarioActual.getEdad());
    }

    @Test
    void actualizarUsuarioCuandoElUsuarioNoExiste() {
        long idUsuario = 5;

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre("Beatriz");
        usuarioActualizado.setEdad(25);

        when(usuarioService.obtenerUsuarioPorId(idUsuario)).thenReturn(Optional.empty());

        ResponseWrapper<Usuario> wrapper = usuarioController.actualizarUsuario(idUsuario, usuarioActualizado);

        assertFalse(wrapper.isSuccess());
        assertEquals(404, wrapper.getResponseEntity().getStatusCode().value());
        assertNull(wrapper.getResponseEntity().getBody());
        verify(usuarioService, never()).actualizarUsuario(usuarioActualizado);
    }

    @Test
    void eliminarUsuario() {
        long idUsuario = 1;

        doNothing().when(usuarioService).eliminarUsuario(idUsuario);

        ResponseWrapper<Void> wrapper = usuarioController.eliminarUsuario(idUsuario);

        assertTrue(wrapper.isSuccess());
        assertEquals(204, wrapper.getResponseEntity().getStatusCode().value());
        verify(usuarioService, atLeastOnce()).eliminarUsuario(idUsuario);
    }

    @Test
    void crearUsuarioCuandoSeaMenorA18() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Nombre");
        usuario.setEdad(10);

        doThrow(new Exception("No se permiten usuarios menores de 18 años"))
                .when(usuarioService).crearUsuario(usuario);

        ResponseWrapper<Usuario> wrapper = usuarioController.crearUsuario(usuario);

        assertFalse(wrapper.isSuccess());
        assertEquals("No se permiten usuarios menores de 18 años", wrapper.getMessage());
        assertEquals(400, wrapper.getResponseEntity().getStatusCode().value());
        assertNull(wrapper.getResponseEntity().getBody());
    }

    private List<Usuario> crearUsuarios(int elementos) {
        return IntStream.range(1, elementos + 1)
                .mapToObj(i -> {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(i);
                    usuario.setNombre("Nombre" + i);
                    usuario.setEdad(15 + i);
                    return usuario;
                }).collect(Collectors.toList());
    }
}