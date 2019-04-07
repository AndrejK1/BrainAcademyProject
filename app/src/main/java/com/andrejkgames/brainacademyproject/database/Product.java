package com.andrejkgames.brainacademyproject.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "Product")
public class Product {

    @PrimaryKey(autoGenerate = true)
    private long itemID;
    private String title;
    private String groupName;
    private String description;
    private double price;
    private String dateAdded;
    private String dateBought;
    private boolean isBought;

    public Product(String title, String groupName, String description, double price) {
        dateAdded = new SimpleDateFormat("dd.MM.yyyy HH:mm",
                Locale.getDefault()).format(new Date());
        isBought = false;
        dateBought = "Not bought yet";
        this.title = title;
        this.groupName = groupName;
        this.description = description;
        this.price = price;
    }

    public static class ProductBuilder {
        private String title;
        private String groupName;
        private String description = "";
        private double price = 0;

        public ProductBuilder(String title, String groupName) {
            this.title = title;
            this.groupName = groupName;
        }

        public Product buildProduct(){
            return new Product(title, groupName, description, price);
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(double price) {
            this.price = price;
            return this;
        }
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateBought() {
        return dateBought;
    }

    public void setDateBought(String dateBought) {
        this.dateBought = dateBought;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }

}
