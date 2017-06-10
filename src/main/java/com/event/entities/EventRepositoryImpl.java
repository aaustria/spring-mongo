package com.event.entities;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * Event repository custom implementation.
 */
public class EventRepositoryImpl implements EventRepositoryCustom {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public int updateEvent(
            long id, Event event
    ) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("title", event.getTitle());
        update.set("description", event.getDescription());
        update.set("start", event.getStart());
        update.set("end", event.getEnd());
        update.set("startTime", event.getStartTime());
        update.set("endTime", event.getEndTime());
        update.set("contact", event.getContact());
        update.set("location", event.getLocation());
        update.set("active", event.isActive());

        update.set("updatedDate", new Date());

        WriteResult result = mongoTemplate.updateFirst(query, update, Event.class);
        if (result != null) {
            return result.getN();
        }

        return 0;
    }

    @Override
    public int updateEventRsvp(long id, List<Long> userIds) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("users", userIds);
        update.set("updatedDate", new Date());

        WriteResult result = mongoTemplate.updateFirst(query, update, Event.class);
        if (result != null) {
            return result.getN();
        }

        return 0;
    }
}
