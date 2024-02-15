package com.example.aut2_07;

public class Item {
    private int id;
    private String title;
    private String description;
    private String imagePath;

    // Constructor, getters y setters a continuación...
    public Item(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = this.imagePath;
    }

    // Getters y Setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
