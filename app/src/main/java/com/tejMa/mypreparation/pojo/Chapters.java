package com.tejMa.mypreparation.pojo;

public class Chapters {
    private String name;
    private int type;
    private int isNew;
    private int image;
    private String description;

    public Chapters() {
    }

    public Chapters(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Chapters(String name, int type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Chapters(String name, String description, int isNew) {
        this.name = name;
        this.isNew = isNew;
        this.description = description;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int isNew() {
        return isNew;
    }

    public void setNew(int aNew) {
        isNew = aNew;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
