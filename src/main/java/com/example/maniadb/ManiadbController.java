package com.example.maniadb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ManiadbController {
    private final RestTemplate restTemplate;
    @GetMapping("/albums")
    public ResponseEntity<?> getAlbums() throws JsonProcessingException {
        URI uri = UriComponentsBuilder.fromUriString("http://www.maniadb.com")
                .path("api/search/kanye/")
                .queryParam("sr", "album")
                .queryParam("display", 50)
                .queryParam("v", 0.5)
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        RequestEntity<Void> request = RequestEntity.get(uri).build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
//        System.out.println(response);

        String xml = response.getBody();
        JSONObject jsonObject = XML.toJSONObject(xml);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Object json = objectMapper.readValue(jsonObject.toString(), Object.class);
        String result = objectMapper.writeValueAsString(json);
        return ResponseEntity.ok(result);
    }
}
