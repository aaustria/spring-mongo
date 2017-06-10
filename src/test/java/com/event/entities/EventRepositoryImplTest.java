package com.event.entities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
public class EventRepositoryImplTest {

    @Autowired
    EventRepository eventRepository;

    static final List<Long> USER_IDS = Arrays.asList(150500l, 150001l, 150001l);
    static final int CURRENT_YEAR = 2017;
    static final int[] START_MONTHS = new int[] { 1, 7, 0, 0, 8, 5, 0, 10 };
    static final int[] END_MONTHS = new int[] { 2, 8, 9, 2, 10, 6, 0, 11 };

    static final int QTY = START_MONTHS.length;

    @Before
    public void init() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        for (int i = 0; i < QTY; i++) {
            Event event = new Event();
            event.setId(i+1l);
            event.setTitle("Event number " + (i+1));
            event.setActive(true);
            event.setDescription("The event number ");
            event.setContact(new Event.Contact(
                    "Mr. Z" + 1, "314-144-3121", "z" + 1 + "@helloworld.com"
            ));
            event.setStart(getDate(START_MONTHS[i]));
            event.setEnd(getDate(END_MONTHS[i]));

            event.setStartTime(720);
            event.setEndTime(1290);
            event.setLocation(new Event.Location(
                    "Wilshire Blvd,", "", "Los Angeles", "CA", "90024"
            ));
            event.setCreatedDate(new Date());
            event.setUsers(Collections.EMPTY_LIST);
            if (i % 3 == 0) {
                event.setUsers(USER_IDS);
            }
            eventRepository.save(event);
        }
    }

    private Date getDate(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, CURRENT_YEAR);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @After
    public void tearDown() throws Exception {
        eventRepository.deleteAll();
    }

    @Test
    public void testCreateAndDeleteEvent() throws Exception {
        Event event = generateEvent();
        eventRepository.save(event);

        assertEquals(event, eventRepository.findOne(event.getId()));

        List<Event> list = eventRepository.findAll();
        assertEquals(QTY + 1, list.size());

        eventRepository.delete(event);
        assertNull(eventRepository.findOne(event.getId()));

        list = eventRepository.findAll();
        assertEquals(QTY, list.size());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        long id = 1l;
        String newTitle = "Event title is Update";
        Event origEvent = eventRepository.findOne(id);
        origEvent.setTitle(newTitle);

        int n = eventRepository.updateEvent(id, origEvent);
        assertEquals(1, n);

        Event updatedEvent = eventRepository.findOne(id);
        assertEquals(newTitle, updatedEvent.getTitle());
    }

    @Test
    public void testSearchEvent_byDate() throws Exception {

        // Search by date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.set(CURRENT_YEAR, 1, 1);
        Date startDate = calendar.getTime();
        calendar.set(CURRENT_YEAR, 8, 1);
        Date endDate = calendar.getTime();
        List<Event> eventByDate = eventRepository.findCustomByDate(startDate, endDate);
        assertEquals(QTY - 2, eventByDate.size());

        // Test exact date
        calendar.set(CURRENT_YEAR, 11, 1);
        startDate = calendar.getTime();
        calendar.set(CURRENT_YEAR, 11, 1);
        endDate = calendar.getTime();
        eventByDate = eventRepository.findCustomByDate(startDate, endDate);
        assertEquals(1, eventByDate.size());

        // Test same date
        calendar.set(CURRENT_YEAR, 0, 1);
        startDate = calendar.getTime();
        eventByDate = eventRepository.findCustomByDate(startDate, startDate);
        assertEquals(3, eventByDate.size());

        // Test out of range
        calendar.set(CURRENT_YEAR, 11, 2);
        startDate = calendar.getTime();
        calendar.set(CURRENT_YEAR, 11, 15);
        endDate = calendar.getTime();
        eventByDate = eventRepository.findCustomByDate(startDate, endDate);
        assertEquals(0, eventByDate.size());
    }

    @Test
    public void testSearchEvent_byTitle() throws Exception {

        // Search by title
        eventRepository.save(generateEvent());
        Page<Event> eventByText = eventRepository.findCustomByRegExTitleOrDescription(
                "Event number 1",
                new PageRequest(0, QTY)
        );
        assertEquals("Expect 2 events id=(10,100, 10000000)", 2, eventByText.getTotalElements());
    }

    @Test
    public void testSearchEvent_byRegisteredUser() throws Exception {

        // Search by users
        List<Event> eventByUsers = eventRepository.findCustomByUsers(USER_IDS.get(0));
        assertEquals(3, eventByUsers.size());
    }

    @Test
    public void testPagination() throws Exception {
        Pageable pageable = new PageRequest(1,2);
        Page<Event> events = eventRepository.findAll(pageable);
        assertEquals(QTY/2, events.getTotalPages());
        assertEquals(QTY, events.getTotalElements());
        assertEquals(3, events.getContent().get(0).getId());
        assertEquals(4, events.getContent().get(1).getId());

        pageable = new PageRequest(0,QTY , new Sort(Sort.Direction.DESC, "_id"));
        events = eventRepository.findAll(pageable);
        assertEquals(1, events.getTotalPages());
        assertEquals(QTY, events.getSize());
        assertEquals(QTY, events.getTotalElements());
        assertEquals(QTY, events.getContent().get(0).getId());
    }


    private Event generateEvent() {
        Event.Contact contact = new Event.Contact("", "", "");
        contact.setEmailAddress("admin@eventconcert.com");
        contact.setName("Admin Smith");

        Event event = new Event();
        event.setId(10000l);
        event.setActive(true);
        event.setTitle("DG Concerto");
        event.setDescription("Event number 10000000");
        event.setContact(contact);
        event.setStart(new Date());
        event.setStartTime(600);
        event.setEndTime(1000);
        event.setCreatedDate(new Date());
        return event;
    }
}