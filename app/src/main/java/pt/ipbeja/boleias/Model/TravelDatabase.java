package pt.ipbeja.boleias.Model;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pt.ipbeja.boleias.Model.dao.travelDao;

@Database(entities = {Travel.class}, version = 1, exportSchema = false)
public abstract class TravelDatabase extends RoomDatabase {

    private static TravelDatabase instance = null;

    public static TravelDatabase getInstance(Context context) {

        context = context.getApplicationContext();

        if(instance == null) {
            // criar instancia

            instance = Room.databaseBuilder(context, TravelDatabase.class, "travels_db")
                    .allowMainThreadQueries() // debug
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract travelDao traveldao();

}
