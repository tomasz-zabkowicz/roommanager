package pl.beusable.roommanager.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseRestIT {

  private static final List<HttpStatusCode> DEFAULT_ACCEPTED_STATUS_CODES = Arrays.asList(HttpStatus.CREATED, HttpStatus.OK);

  @Autowired
  protected TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;

  protected void delete(String uriString) {
    exchange(uriString, DELETE, null, Arrays.asList(HttpStatus.OK, HttpStatus.NO_CONTENT));
  }

  private Object exchange(String uriString, HttpMethod method, Object payload, List<HttpStatusCode> acceptedStatusCodes) {
    final URI uri = UriComponentsBuilder.fromHttpUrl(getRootURL() + "/" + uriString).build().encode().toUri();
    final HttpHeaders requestHeaders = new HttpHeaders();
    if (method.equals(POST) || method.equals(PUT) || method.equals(PATCH)) {
      requestHeaders.add("Content-Type", "application/json");
    }
    final ResponseEntity<Object> responseEntity = restTemplate.exchange(uri, method, new HttpEntity<>(payload, requestHeaders),
        Object.class);

    assertTrue(acceptedStatusCodes.contains(responseEntity.getStatusCode()),
        "Received '" + responseEntity.getStatusCode() + "' while expected one of: " + acceptedStatusCodes.stream()
            .map(sc -> sc.value() + "")
            .collect(Collectors.joining(",")));
    return responseEntity.getBody();
  }

  protected Object get(String uriString) {
    return exchange(uriString, GET, null, DEFAULT_ACCEPTED_STATUS_CODES);
  }

  protected Object get(String uriString, HttpStatus acceptedStatusCode) {
    return exchange(uriString, GET, null, Collections.singletonList(acceptedStatusCode));
  }

  protected String getRootURL() {
    return "http://localhost:" + port + "/";
  }

  protected Object patch(String uriString, Object payload) {
    return exchange(uriString, PATCH, payload, DEFAULT_ACCEPTED_STATUS_CODES);
  }

  protected Object patch(String uriString, Object payload, HttpStatus acceptedStatusCode) {
    return exchange(uriString, PATCH, payload, Collections.singletonList(acceptedStatusCode));
  }

  protected Object post(String uriString, Object payload) {
    return exchange(uriString, POST, payload, DEFAULT_ACCEPTED_STATUS_CODES);
  }

  protected Object post(String uriString, Object payload, HttpStatus acceptedStatusCode) {
    return exchange(uriString, POST, payload, Collections.singletonList(acceptedStatusCode));
  }

  protected Object put(String uriString, Object payload) {
    return exchange(uriString, PUT, payload, DEFAULT_ACCEPTED_STATUS_CODES);
  }

  protected Object put(String uriString, Object payload, HttpStatus acceptedStatusCode) {
    return exchange(uriString, PUT, payload, Collections.singletonList(acceptedStatusCode));
  }

  protected String toJSONString(Object object) throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
  }
}
