package com.bunker.letsbunk.Database;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Data.class},version = 1,exportSchema = false)
public abstract class Database extends RoomDatabase
{

    public abstract DataDao getDataDao();
}
