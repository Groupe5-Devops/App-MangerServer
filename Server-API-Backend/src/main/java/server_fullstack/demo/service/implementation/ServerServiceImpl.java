package server_fullstack.demo.service.implementation;

import static java.lang.Boolean.TRUE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server_fullstack.demo.enumeration.Status;
import server_fullstack.demo.model.Server;
import server_fullstack.demo.repo.ServerRepo;
import server_fullstack.demo.service.ServerService;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {

    private final ServerRepo serverRepo;
    
    @Override
    public Server create(Server server) {
        log.info("Saving new server {} to the database", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddr) throws IOException {
        log.info("Pinging server IP: {}", ipAddr);
        Server server = serverRepo.findByIpAddr(ipAddr);
        InetAddress address = InetAddress.getByName(ipAddr);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        serverRepo.save(server);  // save the updated status to db
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepo.findAll(PageRequest.of(0, limit)).toList();  // findAll returns a page so we turn that into a list with toList() method
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by ıd: {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server: {}", server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by ID: {}", id);
        serverRepo.deleteById(id);
        return TRUE; // True is actually Boolean.TRUE but we static import that so we can use that directly as TRUE.
    }

    private String setServerImageUrl() {
        String[] imageNames = { "server1.png","server2.png","server3.png","server4.png" };
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image/" + imageNames[new Random().nextInt(4)]).toUriString();
        // We gave 4 in here so the choice will be between our 4 photo and not cause an error.
    }

    public void shutdownWindowsServer(String ipAddr, Long id) throws IOException, InterruptedException {
        Server server = serverRepo.findByIpAddrAndId(ipAddr, id);

        if (server == null) {
            throw new IllegalArgumentException("Server with id " + id + " not found.");
        }

        String ipAddress = server.getIpAddr(); // Assuming Server has a method getIpAddress()

        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address for server with id " + id + " is not available.");
        }

        executeCommand("shutdown", "/s", "/f", "/t", "0", "/m", "\\" + ipAddress);
    }

    private void executeCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed with exit code " + exitCode);
        }
    }
}
 
// @RequiredArgsConstrucor: lumbok is gonna create a constructor 
// and then its gonna put the "private final ServerRepo serverRepo;" field
// inside of the constructor. And it's going to be our dependency injection.
// So instead of creating the constructor ourselves lombok is going to do it for us with this annotation (@RequiredArgsConstrucor)

// @Slf4j: This is gonna create a logger for us so we can log things to the console.

// @Service: This is a spring annotation that tells spring that this is a service class.

// @Transactional: This is a spring annotation that tells spring that this class is going to be transactional.