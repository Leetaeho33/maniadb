package com.example.maniadb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.XML;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class ManiadbControllerTwo {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping("/albums")
    public ResponseEntity<?> getAlbums(String q) throws JsonProcessingException {

        RequestEntity<Void> request = getRequest(q);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        // maniaDB는 xml로 전달해 주기 때문에 xml을 json으로 파싱
        String json = xmlToJson(response.getBody());
        System.out.println(json);

        JsonNode jsonNode = objectMapper.readTree(json).get("rss").get("channel").get("item");
        List<AlbumDto> res = new ArrayList<>();

        for (JsonNode node : jsonNode) {
            // 앨범 정보들
            AlbumDto dto = getAlbumDto(node);

            // 앨범에 속한 곡 정보들
            if (node.get("maniadb:albumtrack").get("major_tracks").get("song") != null) {
                JsonNode songNode = node.get("maniadb:albumtrack").get("major_tracks").get("song");
                for (JsonNode song : songNode) {
                    if (song.get("name") != null) {
                        String songName = song.get("name").asText().replaceAll("&nbsp;", "");
                        System.out.println("SongName = " + songName);
                        dto.getSongs().add(songName);
                    }
                }
            }

            res.add(dto);
        }
        GloRes<List<AlbumDto>> gloRes = new GloRes<>("성공", 100, res);
        return ResponseEntity.ok(res);
    }

    private RequestEntity<Void> getRequest(String q) {
        URI uri = UriComponentsBuilder.fromUriString("http://www.maniadb.com")
            .path("api/search/"+ q +"/")
            .queryParam("sr", "album")
            .queryParam("display", 50)
            .queryParam("v", 0.5)
            .encode()
            .build()
            .toUri();

        RequestEntity<Void> request = RequestEntity.get(uri).build();
        return request;
    }

    private String xmlToJson(String xml) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object json = objectMapper.readValue(XML.toJSONObject(xml).toString(), Object.class);
        return objectMapper.writeValueAsString(json);
    }

    private AlbumDto getAlbumDto(JsonNode node) {
        String image = node.get("image").asText();
        String release = node.get("release").asText();
        String artist = node.get("maniadb:albumartists").asText();
        String name = node.get("title_short").get("content").asText();

        AlbumDto dto = AlbumDto.builder()
            .image(image)
            .release(release)
            .name(name)
            .artist(artist)
            .build();
        return dto;
    }
}
