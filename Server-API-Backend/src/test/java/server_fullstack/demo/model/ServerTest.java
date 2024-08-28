package server_fullstack.demo.model;

import org.springframework.boot.test.context.SpringBootTest;
import server_fullstack.demo.enumeration.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import server_fullstack.demo.repo.ServerRepo;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServerTest {

    @Autowired
    private ServerRepo serverRepository;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testServerObjectCreation() {
        Server server = new Server(1L, "192.168.1.1", "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_UP);

        assertEquals(1L, server.getId());
        assertEquals("192.168.1.1", server.getIpAddr());
        assertEquals("ServerName", server.getName());
        assertEquals("16GB", server.getMemory());
        assertEquals("TypeA", server.getType());
        assertEquals("http://image.url", server.getImageUrl());
        assertEquals(Status.SERVER_UP, server.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        Server server = new Server();

        assertNull(server.getId());
        assertNull(server.getIpAddr());
        assertNull(server.getName());
        assertNull(server.getMemory());
        assertNull(server.getType());
        assertNull(server.getImageUrl());
        assertNull(server.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        Server server = new Server(1L, "192.168.1.1", "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_DOWN);

        assertEquals(1L, server.getId());
        assertEquals("192.168.1.1", server.getIpAddr());
        assertEquals("ServerName", server.getName());
        assertEquals("16GB", server.getMemory());
        assertEquals("TypeA", server.getType());
        assertEquals("http://image.url", server.getImageUrl());
        assertEquals(Status.SERVER_DOWN, server.getStatus());
    }

    @Test
    void testIpAddrNotEmptyValidation() {
        Server server = new Server(1L, "", "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_UP);

        Set<ConstraintViolation<Server>> violations = validator.validate(server);

        assertFalse(violations.isEmpty(), "IP Address should not be empty");
        assertEquals("IP Address cannot be empty or null", violations.iterator().next().getMessage());
    }

    @Test
    void testIpAddrNotNullValidation() {
        Server server = new Server(1L, null, "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_UP);

        Set<ConstraintViolation<Server>> violations = validator.validate(server);

        assertFalse(violations.isEmpty(), "IP Address should not be null");
        assertEquals("IP Address cannot be empty or null", violations.iterator().next().getMessage());
    }

    @Test
    void testUniqueIpAddrConstraint() {
        Server server1 = new Server(null, "192.168.1.1", "Server1", "8GB", "TypeA", "http://image1.url", Status.SERVER_UP);
        serverRepository.save(server1);

        Server server2 = new Server(null, "192.168.1.1", "Server2", "16GB", "TypeB", "http://image2.url", Status.SERVER_DOWN);

        assertThrows(Exception.class, () -> {
            serverRepository.save(server2);
        });
    }

    @Test
    void testSetterMethods() {
        Server server = new Server();

        server.setId(1L);
        server.setIpAddr("192.168.1.1");
        server.setName("ServerName");
        server.setMemory("16GB");
        server.setType("TypeA");
        server.setImageUrl("http://image.url");
        server.setStatus(Status.SERVER_DOWN);

        assertEquals(1L, server.getId());
        assertEquals("192.168.1.1", server.getIpAddr());
        assertEquals("ServerName", server.getName());
        assertEquals("16GB", server.getMemory());
        assertEquals("TypeA", server.getType());
        assertEquals("http://image.url", server.getImageUrl());
        assertEquals(Status.SERVER_DOWN, server.getStatus());
    }

    @Test
    void testToString() {
        Server server = new Server(1L, "192.168.1.1", "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_UP);
        String serverString = server.toString();

        assertTrue(serverString.contains("192.168.1.1"));
        assertTrue(serverString.contains("ServerName"));
        assertTrue(serverString.contains("16GB"));
        assertTrue(serverString.contains("TypeA"));
        assertTrue(serverString.contains("http://image.url"));
        assertTrue(serverString.contains("SERVER_UP"));
    }
}