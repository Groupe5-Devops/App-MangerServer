package server_fullstack.demo.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("key", "value");

        Response response = Response.builder()
                .timeStamp(now)
                .statusCode(200)
                .status(HttpStatus.OK)
                .reason("OK")
                .message("Success")
                .developerMessage("All systems go")
                .data(dataMap)
                .build();

        assertEquals(now, response.getTimeStamp(), "Timestamp should match");
        assertEquals(200, response.getStatusCode(), "Status code should be 200");
        assertEquals(HttpStatus.OK, response.getStatus(), "Status should be OK");
        assertEquals("OK", response.getReason(), "Reason should be OK");
        assertEquals("Success", response.getMessage(), "Message should be Success");
        assertEquals("All systems go", response.getDeveloperMessage(), "Developer message should be All systems go");
        assertEquals(dataMap, response.getData(), "Data map should match");
    }

    @Test
    void testJsonSerializationWithNulls() throws Exception {
        Response response = Response.builder()
                .statusCode(404)
                .status(HttpStatus.NOT_FOUND)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(response);

        assertFalse(jsonString.contains("\"reason\":"), "JSON should not contain reason");
        assertFalse(jsonString.contains("\"message\":"), "JSON should not contain message");
        assertFalse(jsonString.contains("\"developerMessage\":"), "JSON should not contain developerMessage");
        assertFalse(jsonString.contains("\"data\":"), "JSON should not contain data");
    }
}