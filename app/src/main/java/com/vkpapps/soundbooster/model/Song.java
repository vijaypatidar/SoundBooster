package com.vkpapps.soundbooster.model;

public class Song {
    private String title, path, artist;

    public Song(String title, String path, String artist) {
        this.title = title;
        this.path = path;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
