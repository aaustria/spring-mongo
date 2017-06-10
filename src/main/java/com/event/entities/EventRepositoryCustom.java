package com.event.entities;

import java.util.List;

/**
 * [Ref] https://www.mkyong.com/spring-data/spring-data-add-custom-method-to-repository/
 */
public interface EventRepositoryCustom {

     int updateEvent(
             long id,
             Event event
     );

     int updateEventRsvp(long id, List<Long> userIds);
}
