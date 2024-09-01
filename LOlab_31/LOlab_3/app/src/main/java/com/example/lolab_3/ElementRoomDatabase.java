package com.example.lolab_3;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Element.class}, version = 1, exportSchema = false)
public abstract class ElementRoomDatabase extends RoomDatabase {
    public abstract ElementDao elementDao();

    private static volatile ElementRoomDatabase INSTANCE;

    static ElementRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ElementRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ElementRoomDatabase.class, "database_name")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                ElementDao dao = INSTANCE.elementDao();
                dao.deleteAll();

                // Add a few sample phones
                dao.insert(new Element("Samsung1", "Galaxy S21", "Android 11", "https://www.samsung.com"));
                dao.insert(new Element("Google", "Pixel 6", "Android 12", "https://www.google.com"));
                dao.insert(new Element("OnePlus", "9 Pro", "Android 11", "https://www.oneplus.com"));

                dao.insert(new Element("Apple", "iPhone 13", "iOS 15", "https://www.apple.com"));
                dao.insert(new Element("Xiaomi", "Mi 12", "Android 11", "https://www.mi.com"));
            });
        }
    };
}