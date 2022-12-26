package dev.pamparampam.myapplication.radiowezel;

public class Item {
    private String thumbnail;
    private String title;
    private String author;
    private String id;
    private String length;
    public Item()
    {
        super();
    }
    public Item(String thumbnail, String title, String id, String author, String length) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.author = author;
        this.id = id;
        this.length = length;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
