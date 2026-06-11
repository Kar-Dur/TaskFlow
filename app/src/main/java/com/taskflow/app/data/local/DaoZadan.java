package com.taskflow.app.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoZadan {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long wstaw(WpisZadania wpis);

    @Update
    void zaktualizuj(WpisZadania wpis);

    @Delete
    void usun(WpisZadania wpis);

    @Query("SELECT * FROM zadania ORDER BY dataDodania DESC")
    LiveData<List<WpisZadania>> wszystkie();

    @Query("SELECT * FROM zadania WHERE id = :id")
    WpisZadania znajdzPoId(long id);

    @Query("DELETE FROM zadania WHERE id = :id")
    void usunPoId(long id);

    @Query("SELECT * FROM zadania WHERE ukonczone = 0 ORDER BY dataDodania DESC")
    LiveData<List<WpisZadania>> aktywne();
}
