package com.event.controllers;

import com.event.entities.Event;
import com.event.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * [Ref] https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-first-application.html#getting-started-first-application-annotations
 * [Ref] https://spring.io/guides/gs/rest-service/
 * [Ref] http://www.javainuse.com/spring/SpringBootUsingPagination
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/api")
public class EventController {
    private int pageSize;

    EventService eventService;

    @Autowired
    public EventController(EventService eventService, PaginationSettings pageSettings) {
        this.eventService = eventService;
        this.pageSize = Integer.parseInt(pageSettings.getSize());
    }

    @RequestMapping(value="/events", method= RequestMethod.GET)
    ResponseEntity<Page<List<Event>>> listEvents(
            @RequestParam(value="t", required=false) String title,
            @RequestParam(value="page", required=false) String page
    ) {
        Pageable pageable = new PageRequest(
                null == page || page.isEmpty() ? 0 : Integer.parseInt(page), pageSize
        );
        Page<Event> events = eventService.listEvents(title, pageable);
        return new ResponseEntity(events, HttpStatus.OK);
    }

    @RequestMapping(value="/events/s/{start}/e/{end}", method= RequestMethod.GET)
    ResponseEntity<List<Event>> listEventsByDate(
            @PathVariable(value="start") String start,
            @PathVariable(value="end") String end
    ) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date startDate = formatter.parse(start);
            Date endDate = formatter.parse(end);

            return new ResponseEntity(eventService.listEventsByDate(startDate, endDate), HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/event/{id}", method=RequestMethod.GET)
    ResponseEntity<Event> getEventById(@PathVariable(value="id", required = true) long id) {
        Event event = eventService.getEvent(id);
        return null == event
                ? new ResponseEntity(HttpStatus.NOT_FOUND)
                : new ResponseEntity(event, HttpStatus.OK);
    }

    @RequestMapping(value="/event", method=RequestMethod.POST)
    ResponseEntity createEvent(@RequestBody Event event) {
        try {
            eventService.createEvent(event);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value="/event/{id}", method=RequestMethod.PUT)
    ResponseEntity updateEvent(
        @PathVariable(value="id", required = true) long id,
        @RequestBody Event event
    ) {
        eventService.updateEvent(id, event);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/event/{id}", method=RequestMethod.DELETE)
    ResponseEntity deleteEvent(@PathVariable(value="id", required = true) int id) {
        eventService.deleteEvent(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/event/register/{id}/{userId}", method=RequestMethod.PUT)
    ResponseEntity registerEvent(
            @PathVariable(value="id", required = true) long eventId,
            @PathVariable(value="userId", required = true) long userId
    ) {
        try {
            eventService.registerEvent(eventId, userId);
        }
        catch (GeneralSecurityException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/event/deregister/{id}/{userId}", method=RequestMethod.PUT)
    ResponseEntity deregisterEvent(
            @PathVariable(value="id", required = true) long eventId,
            @PathVariable(value="userId", required = true) long userId
    ) {
        try {
            eventService.deregisterEvent(eventId, userId);
        }
        catch (GeneralSecurityException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
