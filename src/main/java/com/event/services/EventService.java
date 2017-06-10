package com.event.services;

import com.event.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

public interface EventService {
    Page<Event> listEvents(Pageable pageable);

    Page<Event> listEvents(String searchTerm, Pageable pageable);

    List<Event> listEventsByDate(Date startDate, Date endDate);

    Event getEvent(long id);

    Event createEvent(Event event);

    int updateEvent(long id, Event event);

    void deleteEvent(long id);

    void registerEvent(long eventId, long userId) throws GeneralSecurityException;

    void deregisterEvent(long eventId, long userId) throws GeneralSecurityException;
}
