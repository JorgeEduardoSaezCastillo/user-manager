package com.evaluacion.controller;

import com.evaluacion.dto.PhoneDTO;
import com.evaluacion.dto.UserRequestDTO;
import com.evaluacion.entity.User;
import com.evaluacion.exception.GlobalExceptionHandler;
import com.evaluacion.service.UserService;
import com.evaluacion.validation.ValidationGetUser;
import com.evaluacion.validation.ValidationUser;
import com.evaluacion.validation.ValidationUserDelete;
import com.evaluacion.validation.ValidationUserPatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private ValidationUser validationUser;

    @Mock
    private ValidationUserPatch validationUserPatch;

    @Mock
    private ValidationUserDelete validationUserDelete;

    @Mock
    private ValidationGetUser validationGetUser;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // capturar las excepciones
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCrearUsuario_Success() throws Exception {

        UserRequestDTO requestDTO = new UserRequestDTO(
                "Pedro Picapiedra",
                "pedro@picapiedra.org",
                "Password1",
                Collections.singletonList(new PhoneDTO("987654321", "2", "56"))
        );
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        // Respuesta simulada del servicio
        User usuario = new User();
        UUID userId = UUID.randomUUID();
        usuario.setId(userId);
        usuario.setName("Pedro Picapiedra");
        usuario.setEmail("pedro@picapiedra.org");
        usuario.setPassword("Password1");
        usuario.setPhones(Collections.emptyList());
        LocalDateTime now = LocalDateTime.now();
        usuario.setCreated(now);
        usuario.setModified(now);
        usuario.setLastLogin(now);
        usuario.setToken("fake-jwt-token");
        usuario.setActive(true);

        when(userService.createUser(any())).thenReturn(usuario);

        // Ejecutar petición POST y validar la respuesta
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Pedro Picapiedra"))
                .andExpect(jsonPath("$.email").value("pedro@picapiedra.org"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    public void testObtenerUsuarioPorId_Success() throws Exception {
        UUID userId = UUID.randomUUID();
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
        usuario.setToken("fake-jwt-token");
        usuario.setActive(true);

        when(userService.getUser(eq(userId))).thenReturn(usuario);

        mockMvc.perform(get("/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Pedro Picapiedra"))
                .andExpect(jsonPath("$.email").value("pedro@picapiedra.org"));
    }

    @Test
    public void testReemplazarUsuario_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Pedro Picapiedra Updated",
                "pedro.updated@picapiedra.org",
                "Password1",
                Collections.singletonList(new PhoneDTO("123123123", "2", "56"))
        );
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        User usuario = new User();
        usuario.setId(userId);
        usuario.setName("Pedro Picapiedra Updated");
        usuario.setEmail("pedro.updated@picapiedra.org");
        usuario.setPassword("Password1");
        usuario.setPhones(Collections.emptyList());
        LocalDateTime now = LocalDateTime.now();
        usuario.setCreated(now);
        usuario.setModified(now);
        usuario.setLastLogin(now);
        usuario.setActive(true);

        when(userService.updateUser(eq(userId), any())).thenReturn(usuario);

        mockMvc.perform(put("/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Pedro Picapiedra Updated"))
                .andExpect(jsonPath("$.email").value("pedro.updated@picapiedra.org"));
    }

    @Test
    public void testActualizarParcialmenteUsuario_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        // En actualización parcial, se envían algunos campos, agregamos una lista vacía para telefonos para evitar null
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setName("Pedro Picapiedra Partial");
        requestDTO.setPhones(Collections.emptyList()); // Evita que telefonos sea null
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        User usuario = new User();
        usuario.setId(userId);
        usuario.setName("Pedro Picapiedra Partial");
        usuario.setEmail("pedro@picapiedra.org");
        usuario.setPassword("Password1");
        usuario.setPhones(Collections.emptyList());
        usuario.setCreated(LocalDateTime.now());
        usuario.setModified(LocalDateTime.now());
        usuario.setLastLogin(LocalDateTime.now());
        usuario.setActive(true);

        when(userService.partiallyUpdateUser(eq(userId), any())).thenReturn(usuario);

        mockMvc.perform(patch("/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Pedro Picapiedra Partial"));
    }


    @Test
    public void testEliminarUsuario_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        validationUserDelete.validate(userId);

        // Para DELETE se espera status 204
        mockMvc.perform(delete("/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
