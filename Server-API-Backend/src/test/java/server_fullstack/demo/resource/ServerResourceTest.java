package server_fullstack.demo.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server_fullstack.demo.enumeration.Status;
import server_fullstack.demo.model.Server;
import server_fullstack.demo.service.implementation.ServerServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ServerResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServerServiceImpl serverService;

    private Server server;

    @BeforeEach
    void setUp() {
        server = new Server(1L, "192.168.1.1", "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_UP);
    }

    @Test
    void testGetServers() throws Exception {
        when(serverService.list(30)).thenReturn(List.of(server));

        mockMvc.perform(get("/server/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Servers retrieved")))
                .andExpect(jsonPath("$.data.servers[0].ipAddr", is("192.168.1.1")));

        verify(serverService, times(1)).list(30);
    }

    @Test
    void testPingServer() throws Exception {
        when(serverService.ping("192.168.1.1")).thenReturn(server);

        mockMvc.perform(get("/server/ping/192.168.1.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Ping Success")))
                .andExpect(jsonPath("$.data.server.ipAddr", is("192.168.1.1")));

        verify(serverService, times(1)).ping("192.168.1.1");
    }

    @Test
    void testSaveServer() throws Exception {
        when(serverService.create(any(Server.class))).thenReturn(server);

        String serverJson = "{\"ipAddr\":\"192.168.1.1\",\"name\":\"ServerName\",\"memory\":\"16GB\",\"type\":\"TypeA\",\"imageUrl\":\"http://image.url\",\"status\":\"SERVER_UP\"}";

        mockMvc.perform(post("/server/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serverJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Server Created")))
                .andExpect(jsonPath("$.data.server.ipAddr", is("192.168.1.1")));

        verify(serverService, times(1)).create(any(Server.class));
    }

    @Test
    void testGetServer() throws Exception {
        when(serverService.get(1L)).thenReturn(server);

        mockMvc.perform(get("/server/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Server retrieved")))
                .andExpect(jsonPath("$.data.server.ipAddr", is("192.168.1.1")));

        verify(serverService, times(1)).get(1L);
    }

    @Test
    void testDeleteServer() throws Exception {
        when(serverService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/server/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Server deleted")))
                .andExpect(jsonPath("$.data.deleted", is(true)));

        verify(serverService, times(1)).delete(1L);
    }

    @Test
    void testGetServerImage() throws Exception {
        byte[] imageBytes = new byte[]{1, 2, 3};

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(imageBytes);

            mockMvc.perform(get("/server/image/server1.png"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_PNG))
                    .andExpect(content().bytes(imageBytes));
        }
    }

    @Test
    void testResetServer() throws Exception {
        doNothing().when(serverService).shutdownWindowsServer("192.168.1.1", 1L);

        mockMvc.perform(post("/server/1/reset/192.168.1.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Server reset successfully"));

        verify(serverService, times(1)).shutdownWindowsServer("192.168.1.1", 1L);
    }

}