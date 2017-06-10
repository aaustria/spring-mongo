package com.event.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * [Ref] https://www.mkyong.com/spring-boot/spring-boot-spring-data-mongodb-example/
 * [Ref] https://docs.mongodb.com/manual/reference/operator/query/elemMatch/#op._S_elemMatch
 */
public interface EventRepository extends MongoRepository<Event, Long>, EventRepositoryCustom {

    @Query("{ $or: [ { title: { $regex: ?0 } }, { description: { $regex: ?0 } } ] })")
    Page<Event> findCustomByRegExTitleOrDescription(String searchTerm, Pageable pageable);

    @Query("{users:  ?0 }")
    List<Event> findCustomByUsers(long userId);

    @Query("{ $and : [" +
            "  {" +
            "   $or: [" +
            "    {start: { $gte: ?0} }, " +
            "    {end: { $gte: ?0} }" +
            "   ]" +
            "  }," +
            "  {" +
            "   $or: [" +
            "    {start: { $lte: ?1} }, " +
            "    {end: { $lte: ?1} }" +
            "   ]" +
            "  }" +
            "] }")
    List<Event> findCustomByDate(Date start, Date end);

}