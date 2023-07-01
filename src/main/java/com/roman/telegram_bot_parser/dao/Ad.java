package com.roman.telegram_bot_parser.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Объявление
 */
@Entity(name = "adsDataTable")
public class Ad {

    @Id
    private long id;
    private String link;
    private String price;
    private String date;

    public Ad() {
    }

    public Ad(long id, String link, String price, String date) {
        this.id = id;
        this.link = link;
        this.price = price;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", price='" + price + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
