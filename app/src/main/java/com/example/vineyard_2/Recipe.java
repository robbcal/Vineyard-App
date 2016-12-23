package com.example.vineyard_2;

/**
 * Created by chiz on 12/22/16.
 */

public class Recipe {
    private String title;
    private String url;
    private String image_url;

    public Recipe() {
    }

    public Recipe(String title, String url, String image_url) {
        this.title = title;
        this.url = url;
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImage_url() {
        return image_url;
    }

}
