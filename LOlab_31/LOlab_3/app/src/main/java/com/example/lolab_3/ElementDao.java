package com.example.lolab_3;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ElementDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Element element);




    @Query("DELETE FROM table_name")
    void deleteAll();

    @Query("SELECT * FROM table_name ORDER BY manufacturer ASC, model ASC")
    LiveData<List<Element>> getAllElements();

    @Delete
    void delete(Element element);

    @Update
    void update(Element element);

//    @Query("SELECT * FROM table_name WHERE id = :id LIMIT 1")
//    Element getElementById(long id);

    @Query("SELECT * FROM table_name WHERE id = :id LIMIT 1")
    LiveData<Element> getElementById(long id);


}