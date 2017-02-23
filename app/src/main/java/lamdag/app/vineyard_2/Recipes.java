package lamdag.app.vineyard_2;

public class Recipes {
    private String title;
    private String url;
    private String image_url;
    private String id;
    private String description;
    private String timeStamp;

    public Recipes() {
    }

    public Recipes(String title, String url, String image_url, String id, String description, String timeStamp){
        super();
        this.title = title;
        this.url = url;
        this.image_url = image_url;
        this.id = id;
        this.description = description;
        this.timeStamp = timeStamp;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    public String getDate() {
        return timeStamp;
    }

    public void setDate(String description) {
        timeStamp = timeStamp;
    }
}
