package com.event.services;

import com.event.entities.Event;
import com.event.entities.EventRepository;
import com.event.entities.User;
import com.event.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * [Ref] http://docs.spring.io/spring-data/rest/docs/1.1.x/reference/html/paging-chapter.html
 * [Ref] http://ankushs92.github.io/tutorial/2016/05/03/pagination-with-spring-boot.html
 */
@Service
public class EventServiceImpl implements EventService {
    private SequenceGeneratorService seqGenerator;

    private EventRepository eventRepository;

    private UserRepository userRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, SequenceGeneratorService seqGenerator) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.seqGenerator = seqGenerator;
    }

    public Page<Event> listEvents(Pageable pageable) {
        return listEvents("", pageable);
    }

    public Page<Event> listEvents(String searchTerm, Pageable pageable) {
        if (null != searchTerm && !searchTerm.isEmpty()) {
            return eventRepository.findCustomByRegExTitleOrDescription(searchTerm, pageable);
        }
        return eventRepository.findAll(pageable);
    }

    public List<Event> listEventsByDate(Date startDate, Date endDate) {
        return eventRepository.findCustomByDate(startDate, endDate);
    }

    public Event getEvent(long id) {
        return eventRepository.findOne(id);
    }

    public Event createEvent(Event event) {
        event.setId(event.getId() == 0 ? seqGenerator.getNextSequence("event") : event.getId());
        event.setStart(resetTime(event.getStart()));
        event.setEnd(resetTime(event.getEnd()));
        event.setCreatedDate(new Date());
        return eventRepository.save(event);
    }

    /**
     * Reset datetime to the start time of the day
     */
    private Date resetTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();

    }

    public int updateEvent(long id, Event event) {
        return eventRepository.updateEvent(id, event);
    }

    public void deleteEvent(long id) {
        eventRepository.delete(id);
    }

    public void registerEvent(long eventId, long userId) throws GeneralSecurityException {
        Event event = eventRepository.findOne(eventId);
        if (null == event) {
            throw new GeneralSecurityException("Event NOT found.");
        }

        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new GeneralSecurityException("User NOT found.");
        }

        List<Long> registeredUsers = event.getUsers();
        if (null == registeredUsers) {
            registeredUsers = new ArrayList<>();
        }
        registeredUsers.add(userId);
        eventRepository.updateEventRsvp(
                eventId, registeredUsers
        );
    }

    public void deregisterEvent(long eventId, long userId) throws GeneralSecurityException {
        Event event = eventRepository.findOne(eventId);
        if (null == event) {
            throw new GeneralSecurityException("Event NOT found.");
        }

        List<Long> registeredUser = event.getUsers();
        registeredUser.remove(userId);

        eventRepository.updateEventRsvp(
                eventId, registeredUser
        );
    }
}
