package com.wysong.wordcount.web;

import com.wysong.wordcount.model.WordCount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WordControllerTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void testPost() {
        ResponseEntity<String> response = template.postForEntity(base.toString() + "word/beef", createBody("beef"), String.class);
        String expected = createResposeString("beef", 1);
        assertThat(response.getBody(), equalTo(expected));
    }

    @Test
    public void testPut() throws Exception {
        createTestingData();
        String endpoint = base.toString() + "words";
        ResponseEntity<String> response = template.getForEntity(endpoint, String.class);
        String myString = new String("Stupid");
//        assertThat(response.getBody().size(), equalTo(10));
    }

    private void createTestingData() {
        String endpoint = base.toString() + "word/";
        template.put(endpoint + "beef", createBody("beef"));
        template.put(endpoint + "beef", createBody("beef"));
        template.put(endpoint + "beef", createBody("beef"));
        template.put(endpoint + "chicken", createBody("chicken"));
    }

    private Map<String, String> createBody(String word) {
        Map<String, String> map = new HashMap<>();
        map.put("word", word);
        return map;
    }

    private String createResposeString(String word, int count) {
        StringBuilder sb = new StringBuilder()
                .append("{\"word\":\"")
                .append(word)
                .append("\",\"count\":")
                .append(count)
                .append("}");
/*
        StringBuilder sb = new StringBuilder()
                .append("{\"")
                .append(word)
                .append("\": ")
                .append(count)
                .append("}");
*/
        return sb.toString();
    }

}
