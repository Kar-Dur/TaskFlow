package com.taskflow.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WpisZadania.class}, version = 1, exportSchema = false)
public abstract class BazaDanych extends RoomDatabase {

    private static volatile BazaDanych instancja;

    public abstract DaoZadan daoZadan();

    public static BazaDanych pobierzInstancje(Context context) {
        if (instancja == null) {
            synchronized (BazaDanych.class) {
                if (instancja == null) {
                    instancja = Room.databaseBuilder(
                            context.getApplicationContext(),
                            BazaDanych.class,
                            "taskflow_baza"
                    ).build();
                }
            }
        }
        return instancja;
    }
}
