package pt.ipbeja.boleias.Model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pt.ipbeja.boleias.Model.Travel;

@Dao
public interface travelDao {

    @Insert
    long insert(Travel travel);

    @Insert
    long [] insertAll(List<Travel> travels);

    @Update
    int update(Travel travel);

    @Delete
    int delete(Travel travel);

    @Query("DELETE from travels where nameUser = :name")
    int deleteTable(String name);

    @Query("SELECT * from travels where nameUser = :name")
    List<Travel> getListOfTravelsUser(String name);

    @Query("SELECT * from travels")
    List<Travel> getListOfTravels();

    @Query("SELECT * from travels WHERE id = :travelId")
    Travel getPost(long travelId);

}
