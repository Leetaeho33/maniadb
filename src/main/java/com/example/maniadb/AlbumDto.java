package com.example.maniadb;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
public class AlbumDto {

    private String name;
    private String release;
    private String image;
    private String artist;
    private List<String> songs;

    @Builder
    public AlbumDto(String name, String release, String image, String artist) {
        this.name = name;
        this.release = release;
        this.image = image;
        this.artist = artist;
        this.songs = new ArrayList<>();
    }
}
