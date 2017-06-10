package com.event.controllers;

import com.event.EventApplication;
import com.event.entities.SpringMongoConfiguration;
import com.event.entities.User;
import com.event.entities.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventApplication.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@WebAppConfiguration
public class UserControllerTest {
        private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                MediaType.APPLICATION_JSON.getSubtype(),
                Charset.forName("utf8"));

        private MockMvc mockMvc;

        private HttpMessageConverter mappingJackson2HttpMessageConverter;

        private ObjectMapper mapper = new ObjectMapper();

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        void setConverters(HttpMessageConverter<?>[] converters) {

            this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                    .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                    .findAny()
                    .orElse(null);

            assertNotNull("the JSON message converter must not be null",
                    this.mappingJackson2HttpMessageConverter);
        }

        @Before
        public void setup() throws Exception {
            this.mockMvc = webAppContextSetup(webApplicationContext).build();
        }

        @After
        public void tearDown() throws Exception {
            this.userRepository.deleteAll();
        }

        @Test
        public void testUser() throws Exception {
            User user = new User(15000l, "useraccount", "salt","password", "email@addr.com");
            String json = mapper.writeValueAsString(user);
            mockMvc.perform(post("/api/user/register")
                    .contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isCreated());

            MvcResult result = mockMvc.perform(get("/api/user/" + user.getUsername()))
                    .andExpect(status().isOk())
                    .andReturn();

            User actualUser = mapper.readValue(result.getResponse().getContentAsString(), User.class);
            assertEquals(user, actualUser);
        }

    }
