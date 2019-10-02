package com.bunker.letsbunk.Database;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao
{

    @Insert
    long addAttendance(Data data);

    @Query("UPDATE MYDATA SET attended_lecture=:nAttendedLecture," +
            "total_lecture=:nTotalLectures WHERE id=:id")
    void updateAttendance(int id, int nAttendedLecture, int nTotalLectures);

    @Query("DELETE FROM MYDATA WHERE id=:id")
    void deleteAttendance(int id);

    @Query("SELECT subject_name FROM myData")
    List<String> getAllNames();

    @Query("SELECT * FROM myData")
    List<Data> getAllrecord();

    @Query("UPDATE MYDATA SET answer=:ans WHERE id=:id")
    void addAnswer(int id, String ans);

    @Query("SELECT id FROM myData WHERE subject_name=:name")
    Cursor getItemID(String name);

    @Query("UPDATE MYDATA SET percentage=:percentage WHERE id=:id")
    void addPercentage(int id,double percentage);
}
