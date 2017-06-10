package com.event.controllers;

import com.event.EventApplication;
import com.event.entities.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventApplication.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@WebAppConfiguration
public class EventControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private ObjectMapper mapper = new ObjectMapper();

    private TypeReference<List<Event>> eventListType = new TypeReference<List<Event>>() {
    };

    private TypeReference<PageImpl<Event>> eventPageType = new TypeReference<PageImpl<Event>>() {
    };

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
        this.eventRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void testRegister() throws Exception {
        Event event1 = EventTest.generateEvents(1, 1).get(0);
        createNewEvent(event1);

        mockMvc.perform(put(String.format("/api/event/register/%d/%d", event1.getId(), 80000l)))
                .andExpect(status().is4xxClientError());

        User user = userRepository.insert(new User(80000l, "admin", "salty", "passwordHash", "admin@witty.com"));

        mockMvc.perform(put(String.format("/api/event/register/%d/%d", event1.getId(), user.getId())))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get(String.format("/api/event/%d", event1.getId())))
                .andExpect(status().isOk())
                .andReturn();

        Event actualEvent = mapper.readValue(result.getResponse().getContentAsString(), Event.class);
        assertEquals(user.getId(), actualEvent.getUsers().get(0).longValue());

        mockMvc.perform(put(String.format("/api/event/deregister/%d/%d", event1.getId(), user.getId())))
                .andExpect(status().isOk());

        result = mockMvc.perform(get(String.format("/api/event/%d", event1.getId())))
                .andExpect(status().isOk())
                .andReturn();

        actualEvent = mapper.readValue(result.getResponse().getContentAsString(), Event.class);
        assertTrue(actualEvent.getUsers().isEmpty());

    }

    @Test
    public void testCreateAndUpdateAndDeleteEvent() throws Exception {

        mockMvc.perform(get("/api/events?t=Annual Sales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.totalPages", is(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(0)))
                .andExpect(jsonPath("$.size", is(25)));

        Event event = EventTest.generateEvents(1, 1).get(0);
        createNewEvent(event);

        MvcResult result = mockMvc.perform(get("/api/events?t=Annual Sales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.numberOfElements", is(1)))
                .andExpect(jsonPath("$.size", is(25)))
                .andReturn();

        List<Event> searchEvents = extractEvents(result.getResponse().getContentAsString());

        assertEquals(1, searchEvents.size());

        Event actualEvent = searchEvents.get(0);
        assertEquals(event.isActive(), actualEvent.isActive());
        assertEquals(event.getTitle(), actualEvent.getTitle());
        assertEquals(event.getContact(), actualEvent.getContact());
        assertEquals(event.getUsers(), actualEvent.getUsers());
        assertNull(actualEvent.getDescription());
        assertNotNull(actualEvent.getCreatedDate());
        assertNull(actualEvent.getUpdatedDate());

        String newDescription = "New description to be updated.";
        event.setDescription(newDescription);
        mockMvc.perform(put("/api/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(event)))
                .andExpect(status().isOk());

        result = mockMvc.perform(get("/api/events?t=Annual Sales"))
                .andExpect(status().isOk())
                .andReturn();

        searchEvents = extractEvents(result.getResponse().getContentAsString());
        assertEquals(1, searchEvents.size());
        actualEvent = searchEvents.get(0);
        assertTrue(actualEvent.isActive());
        assertEquals(event.getTitle(), actualEvent.getTitle());
        assertEquals(newDescription, actualEvent.getDescription());
        assertNotNull(actualEvent.getCreatedDate());
        assertNotNull(actualEvent.getUpdatedDate());

        mockMvc.perform(delete("/api/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(event)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/event/" + event.getId()))
                .andExpect(status().isNotFound());
    }

    private List<Event> extractEvents(String eventPageResultJson) throws JSONException, IOException {
        final JSONObject obj = new JSONObject(eventPageResultJson);
        final JSONArray eventData = obj.getJSONArray("content");
        return mapper.readValue(eventData.toString(), eventListType);
    }

    @Test
    public void testSearchByDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 0, 1);
        Date start = calendar.getTime();
        calendar.set(2017, 2, 1);
        Date end = calendar.getTime();

        Event event1 = EventTest.generateEvents(1, 1, start, end).get(0);


        calendar.set(2017, 2, 1);
        start = calendar.getTime();
        calendar.set(2017, 3, 1);
        end = calendar.getTime();
        Event event2 = EventTest.generateEvents(1, 2, start, end).get(0);
        createNewEvent(event1, event2);

        MvcResult result = mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andReturn();

        List<Event> actualEvents = extractEvents(result.getResponse().getContentAsString());
        assertEquals(2, actualEvents.size());

        String startStr = "01-01-2017";
        String endStr = "02-01-2017";
        result = mockMvc.perform(get("/api/events/s/" + startStr + "/e/" + endStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andReturn();

        actualEvents = mapper.readValue(result.getResponse().getContentAsString(), eventListType);
        assertEquals(1, actualEvents.size());

        startStr = "03-01-2017";
        endStr = "03-01-2017";
        result = mockMvc.perform(get("/api/events/s/" + startStr + "/e/" + endStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andReturn();

        actualEvents = mapper.readValue(result.getResponse().getContentAsString(), eventListType);
        assertEquals(2, actualEvents.size());
    }

    @Test
    public void testPagination() throws Exception {
        int totalEvents = 95;
        List<Event> events = EventTest.generateEvents(totalEvents, 1);
        createNewEvent(events);

        mockMvc.perform(get("/api/events?page=0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.totalPages", is(4)))
                .andExpect(jsonPath("$.totalElements", is(totalEvents)))
                .andExpect(jsonPath("$.numberOfElements", is(25)))
                .andExpect(jsonPath("$.size", is(25)));

        mockMvc.perform(get("/api/events?page=3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.totalPages", is(4)))
                .andExpect(jsonPath("$.totalElements", is(totalEvents)))
                .andExpect(jsonPath("$.numberOfElements", is(20)))
                .andExpect(jsonPath("$.size", is(25)));

        mockMvc.perform(get("/api/events?page=4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.totalPages", is(4)))
                .andExpect(jsonPath("$.totalElements", is(totalEvents)))
                .andExpect(jsonPath("$.numberOfElements", is(0)))
                .andExpect(jsonPath("$.size", is(25)));
    }

    private void createNewEvent(List<Event> events) throws Exception {
        for (Event event : events) {
            String json = mapper.writeValueAsString(event);
            mockMvc.perform(post("/api/event")
                    .contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isCreated());
        }
    }

    private void createNewEvent(Event... event) throws Exception {
        createNewEvent(Arrays.asList(event));
    }
}