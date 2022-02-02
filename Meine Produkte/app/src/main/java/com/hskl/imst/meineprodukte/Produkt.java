package com.hskl.imst.meineprodukte;

public class Produkt {

    private long ID;
    private String title;
    private String content;
    private String verfallsdatum;
    private byte[] image;

    Produkt() {}
    Produkt(String title, String content, String verfallsdatum, byte[] image) {
        this.title = title;
        this.content = content;
        this.verfallsdatum = verfallsdatum;
        this.image = image;
    }

    Produkt(long id, String title, String content, String verfallsdatum, byte[] image) {
        this.ID = id;
        this.title = title;
        this.content = content;
        this.verfallsdatum = verfallsdatum;
        this.image = image;
    }


    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getVerfallsdatum() {
        return verfallsdatum;
    }
    public void setVerfallsdatum(String verfallsdatum) { this.verfallsdatum = verfallsdatum; }

    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }



}
