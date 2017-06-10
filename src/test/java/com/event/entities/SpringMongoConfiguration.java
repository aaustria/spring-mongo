package com.event.entities;

import com.github.fakemongo.Fongo;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoRepositories
public class SpringMongoConfiguration {

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        Fongo fongo = new Fongo("event");
        return new MongoTemplate(fongo.getMongo(), "event");
    }

}