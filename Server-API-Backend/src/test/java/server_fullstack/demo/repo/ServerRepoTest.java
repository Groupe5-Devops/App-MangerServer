package server_fullstack.demo.repo;

import org.springframework.boot.test.context.SpringBootTest;
import server_fullstack.demo.model.Server;
import server_fullstack.demo.enumeration.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServerRepoTest {

    @Autowired
    private ServerRepo serverRepo;

    private Server server;

    @BeforeEach
    void setUp() {
        serverRepo.deleteAll(); // Clean up the repository before each test
        server = new Server(null, "192.168.1.1", "ServerName", "16GB", "TypeA", "http://image.url", Status.SERVER_UP);
        serverRepo.save(server);
    }

    @Test
    void testFindByIpAddr() {
        Server foundServer = serverRepo.findByIpAddr("192.168.1.1");

        assertNotNull(foundServer);
        assertEquals("ServerName", foundServer.getName());
    }

    @Test
    void testFindByIpAddr_NotFound() {
        Server foundServer = serverRepo.findByIpAddr("192.168.1.2");

        assertNull(foundServer);
    }

    @Test
    void testFindByIpAddrAndId() {
        Server foundServer = serverRepo.findByIpAddrAndId("192.168.1.1", server.getId());

        assertNotNull(foundServer);
        assertEquals("ServerName", foundServer.getName());
    }

    @Test
    void testFindByIpAddrAndId_NotFound() {
        Server foundServer = serverRepo.findByIpAddrAndId("192.168.1.2", server.getId());

        assertNull(foundServer);
    }

    @Test
    void testSaveUniqueIpAddrConstraint() {
        Server newServer = new Server(null, "192.168.1.1", "AnotherServer", "8GB", "TypeB", "http://image2.url", Status.SERVER_DOWN);

        assertThrows(DataIntegrityViolationException.class, () -> {
            serverRepo.save(newServer);
        });
    }

    @Test
    void testSaveAndDeleteServer() {
        Server newServer = new Server(null, "192.168.1.2", "AnotherServer", "8GB", "TypeB", "http://image2.url", Status.SERVER_UP);
        serverRepo.save(newServer);

        Optional<Server> foundServer = serverRepo.findById(newServer.getId());
        assertTrue(foundServer.isPresent());

        serverRepo.delete(newServer);
        foundServer = serverRepo.findById(newServer.getId());
        assertFalse(foundServer.isPresent());
    }
}