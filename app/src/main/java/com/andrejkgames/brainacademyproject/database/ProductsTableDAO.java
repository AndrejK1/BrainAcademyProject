package com.andrejkgames.brainacademyproject.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProductsTableDAO {

    @Insert
    void addItems(Product newProduct);

    @Update
    void updateItem(Product product);

    @Delete
    void deleteItem(Product product);

    @Query("SELECT * FROM Product")
    List<Product> getAllItems();

    @Query("SELECT * FROM Product WHERE isBought = 0")
    List<Product> getNotBoughtItems();

    @Query("SELECT * FROM Product WHERE isBought = 1")
    List<Product> getBoughtItems();

    @Query("SELECT * FROM Product WHERE itemID = :id")
    Product getItemByID(long id);

    @Query("SELECT * FROM Product WHERE groupName = :groupName")
    Product getItemByGroup(String groupName);

    @Query("DELETE FROM Product WHERE groupName = :groupName")
    void deleteGroup(String groupName);

    @Query("DELETE FROM Product WHERE isBought = 1")
    void deleteBought();

    @Query("DELETE FROM Product")
    void deleteAllProducts();
}
