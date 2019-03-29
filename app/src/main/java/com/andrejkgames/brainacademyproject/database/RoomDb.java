package com.andrejkgames.brainacademyproject.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Product.class}, version = 1)
public abstract class RoomDb extends RoomDatabase {
    public abstract ProductsTableDAO getTableDAO();
}
