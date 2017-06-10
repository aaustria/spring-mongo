package com.event.entities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class EventTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization_singleEvent() throws Exception {
        Event event = generateEvents(1, 1).get(0);


        String json = mapper.writeValueAsString(event);
        assertEquals(event, mapper.readValue(json, Event.class));
    }

    @Test
    public void testSerialization_multipleEvents() throws Exception {
        List<Event> events = generateEvents(10, 1);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(events);
        TypeReference<List<Event>> type = new TypeReference<List<Event>>() {
        };
        assertEquals(events, mapper.readValue(json, type));
    }

    public static List<Event> generateEvents(int size, int startId, Date... dates) {
        List<Event> events = new ArrayList<>();
        for (int i=0; i<size; i++) {
            Event event = new Event();
            event.setId((long)  startId++);
            event.setTitle("Annual Sales Event at the Mall " + Math.random());
            event.setActive(true);
            event.setContact(new Event.Contact(
                    "Mr. Z" + 1, "314-144-3121", "z" + 1 + "@helloworld.com"
            ));
            event.setStart(dates.length > 0 ? dates[0] : new Date());
            event.setStartTime(720);
            event.setEndTime(1290);
            event.setEnd(dates.length > 1 ? dates[1] : new Date());
            event.setLocation(new Event.Location(
                    "1000 Wilshire Blvd.","","Los Angeles","CA", "90025"
            ));
            event.setCreatedDate(new Date());
            events.add(event);
        }

        return events;
    }

    @Test
    public void testFormatTime() throws Exception {
        Event event = new Event();

        event.setStartTime(0);
        assertEquals("00:00", event.getFormattedStartTime());

        event.setStartTime(270);
        assertEquals("04:30", event.getFormattedStartTime());

        event.setStartTime(1271);
        assertEquals("21:11", event.getFormattedStartTime());

        event.setStartTime(1439);
        assertEquals("23:59", event.getFormattedStartTime());

        boolean success = false;
        try {
            event.setStartTime(-1);
            success = true;
        } catch (Exception e) {
        }
        assertFalse(success);

        success = false;
        try {
            event.setStartTime(1440);
            success = true;
        } catch (Exception e) {
        }
        assertFalse(success);

        success = false;
        try {
            String eventJsonStr = "{ \"startTime\" : 1440 }";
            assertEquals(event, mapper.readValue(eventJsonStr, Event.class));
        } catch (Exception e) {
        }
        assertFalse(success);
    }
}