package com.andrejkgames.brainacademyproject.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
@Entity(tableName = "Product")
public class Product implements Parcelable {

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
        dateAdded = new SimpleDateFormat().format(new Date());
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

    protected Product(Parcel in) {
        title = in.readString();
        groupName = in.readString();
        description = in.readString();
        price = in.readDouble();
        dateAdded = in.readString();
        dateBought = in.readString();
        isBought = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(groupName);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(dateAdded);
        dest.writeString(dateBought);
        dest.writeByte((byte) (isBought ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
