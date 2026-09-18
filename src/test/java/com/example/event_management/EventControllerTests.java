package com.example.event_management;
import com.example.event_management.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------

    @Test
    void createEvent_shouldReturn201() throws Exception {

        String json = """
                {
                    "name": "Create Test Event",
                    "description": "Testing event creation",
                    "startDateTime": "2026-10-05T10:00:00",
                    "endDateTime": "2026-10-05T12:00:00",
                    "venue": "Lab 1",
                    "capacity": 50,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Create Test Event"))
                .andExpect(jsonPath("$.capacity").value(50));
    }


    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    @Test
    void createEvent_withMissingName_shouldReturn400() throws Exception {

        String json = """
                {
                    "description": "Testing validation",
                    "startDateTime": "2026-10-05T10:00:00",
                    "endDateTime": "2026-10-05T12:00:00",
                    "venue": "Lab 1",
                    "capacity": 50,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }


    @Test
    void createEvent_withNegativeCapacity_shouldReturn400() throws Exception {

        String json = """
                {
                    "name": "Invalid Capacity Event",
                    "description": "Testing negative capacity",
                    "startDateTime": "2026-10-05T10:00:00",
                    "endDateTime": "2026-10-05T12:00:00",
                    "venue": "Lab 1",
                    "capacity": -10,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void createEvent_withPastDate_shouldReturn400() throws Exception {

        String json = """
                {
                    "name": "Past Event",
                    "description": "Testing past event",
                    "startDateTime": "2025-01-01T10:00:00",
                    "endDateTime": "2025-01-01T12:00:00",
                    "venue": "Lab 1",
                    "capacity": 50,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void createEvent_withInvalidDateRange_shouldReturn400() throws Exception {

        String json = """
                {
                    "name": "Invalid Date Event",
                    "description": "End is before start",
                    "startDateTime": "2026-10-05T12:00:00",
                    "endDateTime": "2026-10-05T10:00:00",
                    "venue": "Lab 1",
                    "capacity": 50,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    // ---------------------------------------------------------
    // GET
    // ---------------------------------------------------------

    @Test
    void getEventById_withExistingEvent_shouldReturn200() throws Exception {

        Long id = createEventAndGetId(
                "Get By ID Test",
                "Testing get by ID"
        );

        mockMvc.perform(
                        get("/events/" + id)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Get By ID Test"));
    }


    @Test
    void getEventById_withNonexistentId_shouldReturn404() throws Exception {

        mockMvc.perform(
                        get("/events/999999999")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    @Test
    void updateEvent_shouldReturn200() throws Exception {

        Long id = createEventAndGetId(
                "Update Test Event",
                "Original description"
        );

        String json = """
                {
                    "name": "Updated Event",
                    "description": "Updated description",
                    "startDateTime": "2026-10-06T10:00:00",
                    "endDateTime": "2026-10-06T12:00:00",
                    "venue": "Updated Lab",
                    "capacity": 100,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        put("/events/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event"))
                .andExpect(jsonPath("$.capacity").value(100));
    }


    @Test
    void updateEvent_withNonexistentId_shouldReturn404() throws Exception {

        String json = """
                {
                    "name": "Updated Event",
                    "description": "Updated description",
                    "startDateTime": "2026-10-06T10:00:00",
                    "endDateTime": "2026-10-06T12:00:00",
                    "venue": "Lab 1",
                    "capacity": 100,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        put("/events/999999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    // ---------------------------------------------------------
    // AUDIT TESTING
    // ---------------------------------------------------------
    @Test
    void createEvent_shouldCreateAuditLog() throws Exception {

        Long eventId = createEventAndGetId(
                "Audit Test Event",
                "Testing audit logging"
        );

        boolean auditExists = auditLogRepository
                .findAll()
                .stream()
                .anyMatch(log ->
                        log.getEventId().equals(eventId)
                                && log.getAction().equals("CREATE")
                );

        org.junit.jupiter.api.Assertions.assertTrue(auditExists);
    }


    // ---------------------------------------------------------
    // BUSINESS RULES
    // ---------------------------------------------------------

    @Test
    void createDuplicateEvent_shouldReturn409() throws Exception {

        String json = """
                {
                    "name": "Duplicate Test Event",
                    "description": "First event",
                    "startDateTime": "2026-10-10T10:00:00",
                    "endDateTime": "2026-10-10T12:00:00",
                    "venue": "Lab 5",
                    "capacity": 50,
                    "status": "UPCOMING"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isConflict());
    }




    @Test
    void updateCancelledEvent_shouldReturn400() throws Exception {

        Long id = createCancelledEventAndGetId();

        String json = """
                {
                    "name": "Trying To Update Cancelled",
                    "description": "Should not update",
                    "startDateTime": "2026-11-01T10:00:00",
                    "endDateTime": "2026-11-01T12:00:00",
                    "venue": "Cancelled Venue",
                    "capacity": 50,
                    "status": "CANCELLED"
                }
                """;

        mockMvc.perform(
                        put("/events/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    @Test
    void deleteUpcomingEvent_shouldReturn204() throws Exception {

        Long id = createEventAndGetId(
                "Delete Test Event",
                "Testing deletion"
        );

        mockMvc.perform(
                        delete("/events/" + id)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    // ---------------------------------------------------------
    // SEARCH
    // ---------------------------------------------------------

    @Test
    void searchEvents_shouldReturnMatchingEvent() throws Exception {

        createEventAndGetId(
                "UniqueSearchEvent",
                "This event is used for search testing"
        );

        mockMvc.perform(
                        get("/events")
                                .param("search", "UniqueSearchEvent")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("UniqueSearchEvent"));
    }


    // ---------------------------------------------------------
    // FILTER
    // ---------------------------------------------------------

    @Test
    void filterEventsByVenue_shouldReturnMatchingEvent() throws Exception {

        createEventAndGetId(
                "Venue Filter Event",
                "Testing venue filtering"
        );

        mockMvc.perform(
                        get("/events")
                                .param("venue", "Test Venue")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].venue")
                        .value("Test Venue"));
    }


    // ---------------------------------------------------------
    // PAGINATION
    // ---------------------------------------------------------

    @Test
    void pagination_shouldRespectLimit() throws Exception {

        createEventAndGetId(
                "Pagination Event 1",
                "Testing pagination"
        );

        createEventAndGetId(
                "Pagination Event 2",
                "Testing pagination"
        );

        createEventAndGetId(
                "Pagination Event 3",
                "Testing pagination"
        );

        mockMvc.perform(
                        get("/events")
                                .param("search", "Pagination Event")
                                .param("page", "1")
                                .param("limit", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }


    // ---------------------------------------------------------
    // SORTING
    // ---------------------------------------------------------

    @Test
    void sortingByName_shouldSortAscending() throws Exception {

        createEventAndGetId(
                "AAA Sorting Event",
                "Testing sorting"
        );

        createEventAndGetId(
                "ZZZ Sorting Event",
                "Testing sorting"
        );

        mockMvc.perform(
                        get("/events")
                                .param("search", "Sorting Event")
                                .param("sort", "name")
                                .param("order", "asc")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("AAA Sorting Event"))
                .andExpect(jsonPath("$.content[1].name")
                        .value("ZZZ Sorting Event"));
    }


    // ---------------------------------------------------------
    // HELPER METHODS
    // ---------------------------------------------------------

    private Long createEventAndGetId(
            String name,
            String description) throws Exception {

        String json = """
                {
                    "name": "%s",
                    "description": "%s",
                    "startDateTime": "2026-10-25T10:00:00",
                    "endDateTime": "2026-10-25T12:00:00",
                    "venue": "Test Venue",
                    "capacity": 50,
                    "status": "UPCOMING"
                }
                """.formatted(name, description);

        MvcResult result = mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse().getContentAsString()
                );

        return response.get("id").asLong();
    }


    private Long createCancelledEventAndGetId() throws Exception {

        String json = """
                {
                    "name": "Cancelled Helper Event",
                    "description": "Testing cancelled event rules",
                    "startDateTime": "2026-11-01T10:00:00",
                    "endDateTime": "2026-11-01T12:00:00",
                    "venue": "Cancelled Venue",
                    "capacity": 50,
                    "status": "CANCELLED"
                }
                """;

        MvcResult result = mockMvc.perform(
                        post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse().getContentAsString()
                );

        return response.get("id").asLong();
    }
}
