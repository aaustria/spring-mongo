package com.event.services;

import com.event.entities.Event;
import com.event.entities.EventRepository;
import com.event.entities.User;
import com.event.entities.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * [Ref] http://www.lucassaldanha.com/unit-and-integration-tests-in-spring-boot/
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class EventServiceTest {

    private EventServiceImpl eventService;
    private EventRepository eventRepositoryMock;
    private UserRepository userRepositoryMock;
    private SequenceGeneratorService seqGeneratorMock;

    @Before
    public void setUp() throws Exception {
        eventRepositoryMock = Mockito.mock(EventRepository.class);
        userRepositoryMock = Mockito.mock(UserRepository.class);
        seqGeneratorMock = Mockito.mock(SequenceGeneratorService.class);
        eventService = new EventServiceImpl(eventRepositoryMock, userRepositoryMock, seqGeneratorMock);
    }

    @Test
    public void testResetDateTime() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 14);
        Date start = calendar.getTime();
        calendar.set(2017, 1, 20);
        Date end = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

        Event event = new Event();
        event.setStart(calendar.getTime());
        event.setEnd(calendar.getTime());

        assertNotEquals("00:00:00.000", formatter.format(start));
        assertNotEquals("00:00:00.000", formatter.format(end));

        Mockito.when(eventRepositoryMock.save(event)).thenReturn(event);

        Event createdEvent = eventService.createEvent(event);

        assertEquals("00:00:00.000", formatter.format(createdEvent.getStart()));
        assertEquals("00:00:00.000", formatter.format(createdEvent.getEnd()));
    }

    @Test
    public void testEnrollEvent() throws Exception {
        Event event = new Event();
        event.setTitle("New Event");
        event.setActive(true);
        event.setId(1000l);

        User user = new User(50000l, "cdd", null, null, "cdd@interviewassignment.com");

        List<Long> users = new ArrayList<>();
        event.setUsers(users);

        Mockito.when(eventRepositoryMock.findOne(event.getId())).thenReturn(event);
        Mockito.when(eventRepositoryMock.updateEventRsvp(event.getId(), users)).thenReturn(1);
        Mockito.when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        eventService.registerEvent(event.getId(), user.getId());
        assertEquals(1, event.getUsers().size());

        eventService.deregisterEvent(event.getId(), 90000l);
        assertEquals(1, event.getUsers().size());

        eventService.deregisterEvent(event.getId(), user.getId());
        assertTrue(event.getUsers().isEmpty());
    }
}