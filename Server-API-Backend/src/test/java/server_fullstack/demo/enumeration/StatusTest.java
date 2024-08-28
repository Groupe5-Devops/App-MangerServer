package server_fullstack.demo.enumeration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testGetStatus() {
        // Test SERVER_UP
        Status statusUp = Status.SERVER_UP;
        assertEquals("SERVER_UP", statusUp.getStatus(), "Expected status to be SERVER_UP");

        // Test SERVER_DOWN
        Status statusDown = Status.SERVER_DOWN;
        assertEquals("SERVER_DOWN", statusDown.getStatus(), "Expected status to be SERVER_DOWN");
    }

    @Test
    void testEnumValues() {
        // Test the values of the enum
        Status[] statuses = Status.values();
        assertArrayEquals(new Status[]{Status.SERVER_UP, Status.SERVER_DOWN}, statuses, "Enum values do not match");
    }

    @Test
    void testEnumValueOf() {
        // Test valueOf method
        assertEquals(Status.SERVER_UP, Status.valueOf("SERVER_UP"), "Expected Status.SERVER_UP");
        assertEquals(Status.SERVER_DOWN, Status.valueOf("SERVER_DOWN"), "Expected Status.SERVER_DOWN");
    }
}