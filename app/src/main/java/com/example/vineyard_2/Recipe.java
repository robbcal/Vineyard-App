package com.example.vineyard_2;


public class Recipe {
    private String title;
    private String url;
    private String image_url;
    private String description;

    public Recipe() {
    }

    public Recipe(String title, String url, String image_url) {
        this.title = title;
        this.url = url;
        this.image_url = image_url;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImage_url() {
        return image_url;
    }

}
