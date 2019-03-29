package com.andrejkgames.brainacademyproject.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.andrejkgames.brainacademyproject.database.Product;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class Groups implements Parent<Product>, Parcelable {
    private String groupName;
    private List<Product> mProducts;

    public Groups(String groupName, List<Product> mProducts) {
        this.groupName = groupName;
        this.mProducts = mProducts;
    }

    public Groups(String groupName, Product mProduct) {
        this.groupName = groupName;
        mProducts = new ArrayList<>();
        mProducts.add(mProduct);
    }

    public void addProduct(Product product){
        mProducts.add(product);
    }

    @Override
    public List<Product> getChildList() {
        return mProducts;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getGroupName() {
        return groupName;
    }

    protected Groups(Parcel in) {
        groupName = in.readString();
        if (in.readByte() == 0x01) {
            mProducts = new ArrayList<Product>();
            in.readList(mProducts, Product.class.getClassLoader());
        } else {
            mProducts = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        if (mProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Groups> CREATOR = new Parcelable.Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
        }
    };
}