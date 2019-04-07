package com.andrejkgames.brainacademyproject.list;

import com.andrejkgames.brainacademyproject.database.Product;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class Groups implements Parent<Product> {
    private String groupName;
    private List<Product> mProducts;

    public Groups(String groupName, Product mProduct) {
        this.groupName = groupName;
        mProducts = new ArrayList<>();
        mProducts.add(mProduct);
    }

    public void addProduct(Product product){
        mProducts.add(product);
    }

    public void removeProduct(int index){
        mProducts.remove(index);
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
}