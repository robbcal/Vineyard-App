package com.example.vineyard_2;

/**
 * Created by chiz on 12/22/16.
 */

public class Recipes {
    private String title;
    private String url;
    private String image_url;
    private String id;

    public Recipes() {
    }

    public Recipes(String title, String url, String image_url, String id){
        super();
        this.title = title;
        this.url = url;
        this.image_url = image_url;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        url = url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage(String image_url) {
        image_url = image_url;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        id = id;
    }
}