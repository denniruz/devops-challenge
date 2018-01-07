package com.wysong.wordcount.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WordCountAppTest {

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
        String expected = createResponseString("beef", 1);
        assertThat(response.getBody(), equalTo(expected));
    }

    @Test
    public void testPut() {
        createTestingData();
        String endpoint = base.toString() + "words";
        ResponseEntity<String> response = template.getForEntity(endpoint + "/beef", String.class);
        String expected = createResponseString("beef", 3);
        assertThat(response.getBody(), equalTo(expected));

        response = template.getForEntity(endpoint, String.class);
        Map<String, Integer> expectedMap = new LinkedHashMap<>();
        expectedMap.put("beef", 3);
        expectedMap.put("chicken", 1);
        assertThat(response.getBody(), equalTo(createResponseString(expectedMap)));
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

    private String createResponseString(String word, int count) {
/*
        StringBuilder sb = new StringBuilder()
                .append("{\"word\":\"")
                .append(word)
                .append("\",\"count\":")
                .append(count)
                .append("}");
*/
        StringBuilder sb = new StringBuilder()
                .append("{\"")
                .append(word)
                .append("\":")
                .append(count)
                .append("}");
        return sb.toString();
    }

    private String createResponseString(Map<String, Integer> wordCounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String s : wordCounts.keySet()) {
            sb.append("\"").append(s).append("\":").append(wordCounts.get(s)).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("}");
        return sb.toString();
    }

}
