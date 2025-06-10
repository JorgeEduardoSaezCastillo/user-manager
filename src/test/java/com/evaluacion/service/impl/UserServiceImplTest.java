package com.evaluacion.service.impl;

import com.evaluacion.config.JwtUtil;
import com.evaluacion.dto.PhoneDTO;
import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.Phone;
import com.evaluacion.entity.User;
import com.evaluacion.exception.ValidationException;
import com.evaluacion.mapper.UserMapper;
import com.evaluacion.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl usuarioService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreatedUser_Success() {
        UserRequestDTO dto = new UserRequestDTO(
                "Pedro Picapiedra",
                "pedro@picapiedra.org",
                "Password1",
                Collections.singletonList(new PhoneDTO("987654321", "2", "56"))
        );

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        User usuarioSinToken = UserMapper.toEntity(dto);
        UUID generatedId = UUID.randomUUID();
        usuarioSinToken.setId(generatedId);
        usuarioSinToken.setCreated(LocalDateTime.now());
        usuarioSinToken.setModified(LocalDateTime.now());
        usuarioSinToken.setLastLogin(LocalDateTime.now());

        when(jwtUtil.generarToken(eq(generatedId))).thenReturn("token-value");

        when(userRepository.save(any(User.class)))
                .thenReturn(usuarioSinToken)
                .thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = usuarioService.createUser(dto);

        verify(userRepository).existsByEmail(dto.getEmail());
        verify(userRepository, times(2)).save(any(User.class));
        verify(jwtUtil).generarToken(eq(generatedId));

        assertEquals("Pedro Picapiedra", resultado.getName());
        assertEquals("pedro@picapiedra.org", resultado.getEmail());
        assertEquals("token-value", resultado.getToken());
        assertTrue(resultado.isActive());
        assertNotNull(resultado.getModified());
        assertNotNull(resultado.getLastLogin());
    }

    @Test
    void testCreatedUser_DuplicateCorreo() {
        UserRequestDTO dto = new UserRequestDTO(
                "Pedro Picapiedra",
                "pedro@picapiedra.org",
                "Password1",
                Collections.singletonList(new PhoneDTO("987654321", "3", "57"))
        );

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            usuarioService.createUser(dto);
        });
        assertEquals("El correo ya esta registrado", exception.getMessage());
    }

    @Test
    void testGetUser_Success() {
        UUID userId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        User usuario = new User();
        usuario.setId(userId);
        usuario.setName("Pedro Picapiedra");
        usuario.setEmail("pedro@picapiedra.org");
        usuario.setPassword("Password1");
        usuario.setPhones(Collections.emptyList());
        LocalDateTime now = LocalDateTime.now();
        usuario.setCreated(now);
        usuario.setModified(now);
        usuario.setLastLogin(now);
        usuario.setToken("token-value");
        usuario.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(userRepository.save(any(User.class))).thenReturn(usuario);

        User resultado = usuarioService.getUser(userId);

        verify(userRepository, atLeastOnce()).findById(userId);
        assertEquals("Pedro Picapiedra", resultado.getName());
        assertEquals("pedro@picapiedra.org", resultado.getEmail());
    }

    @Test
    void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        User existente = new User();
        existente.setId(userId);
        existente.setName("Nombre Viejo");
        existente.setEmail("viejo@ejemplo.com");
        existente.setPassword("OldPass");
        existente.setPhones(new java.util.ArrayList<>(Collections.singletonList(new Phone("1111111", "1", "57", existente))));
        existente.setCreated(LocalDateTime.now().minusDays(2));

        UserRequestDTO dto = new UserRequestDTO(
                "Pedro Picapiedra Updated",
                "pedro.updated@picapiedra.org",
                "Password1",
                Collections.singletonList(new PhoneDTO("123123123", "3", "57"))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existente));
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = usuarioService.updateUser(userId, dto);

        assertEquals("Pedro Picapiedra Updated", resultado.getName());
        assertEquals("pedro.updated@picapiedra.org", resultado.getEmail());
        assertEquals(1, resultado.getPhones().size());
        assertEquals("123123123", resultado.getPhones().get(0).getNumber());
    }

    @Test
    void testUpdateUser_DuplicateCorreo() {
        UUID userId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        User existente = new User();
        existente.setId(userId);
        existente.setEmail("original@mail.com");

        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("otro@mail.com");
        dto.setName("Nuevo");
        dto.setPassword("NuevaPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existente));
        when(userRepository.existsByEmail("otro@mail.com")).thenReturn(true);

        assertThrows(ValidationException.class, () -> usuarioService.updateUser(userId, dto));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Nombre");
        dto.setEmail("email@dominio.com");
        dto.setPassword("pass");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> usuarioService.updateUser(userId, dto));
    }

    @Test
    void testUpdateUser_PhonesNull() {
        UUID userId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        User existente = new User();
        existente.setId(userId);
        existente.setEmail("original@mail.com");

        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Nombre");
        dto.setEmail("nuevo@mail.com");
        dto.setPassword("pass");
        dto.setPhones(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existente));
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = usuarioService.updateUser(userId, dto);

        assertEquals("nuevo@mail.com", resultado.getEmail());
        assertEquals("Nombre", resultado.getName());
    }

    @Test
    void testDeleteUser_Success() {
        UUID userId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        User existente = new User();
        existente.setId(userId);
        existente.setName("Pedro Picapiedra");
        existente.setEmail("pedro@picapiedra.org");
        existente.setPassword("Password1");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existente));

        usuarioService.deleteUser(userId);

        verify(userRepository).delete(existente);
    }
}
