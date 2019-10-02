package com.bunker.letsbunk.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref
{
    SharedPreferences sharedPreferences;
    public SharedPref(Context context)
    {
        sharedPreferences=context.getSharedPreferences("filename",Context.MODE_PRIVATE);

    }

    public void setNightModeState(Boolean state)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("NightMode",state);
        editor.apply();
    }

    public Boolean loadNightMode()
    {
        return sharedPreferences.getBoolean("NightMode",false);
    }
}
