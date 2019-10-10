package com.bunker.letsbunk.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bunker.letsbunk.Database.SharedPref;
import com.bunker.letsbunk.R;


public class SettingFragment extends Fragment
{
    private EditText per_criteria;
    private Button setCriteria;
    private int criteria=75;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView nCrit;
    private String nCriteria;
    private Switch aSwitch;
    private SharedPref sharedPref;

    public  static final String name ="attendance";
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState)
    {

        final View v=inflater.inflate(R.layout.setting_fragment,container,false);

        sharedPref=new SharedPref(getActivity());

        loadTheme();

        per_criteria=v.findViewById(R.id.perCriteria);
        setCriteria=v.findViewById(R.id.setButton);
        nCrit=v.findViewById(R.id.newCriteria);
        aSwitch=v.findViewById(R.id.dark_mode_switch);
        try
        {
            pref=getActivity().getSharedPreferences(name, Context.MODE_PRIVATE);
            editor=pref.edit();

            nCriteria=pref.getString("show_criteria",null);
            if (nCriteria!=null)
            nCrit.setText("Set Criteria is "+nCriteria+"%");


        }catch (Exception e)
        {
            Log.e("",""+e.getMessage());
        }
        setCriteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (per_criteria.getText().toString().isEmpty())
                {
                    return;
                }else {
                    criteria = Integer.parseInt(per_criteria.getText().toString());
                    nCriteria = per_criteria.getText().toString();
                    editor.putInt("criteria_key", criteria);
                    editor.putString("show_criteria", nCriteria);
                    editor.apply();
                    per_criteria.setText("");
                    nCrit.setText("You set the criteria to be " + nCriteria + "%");

                    Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(sharedPref.loadNightMode())
            aSwitch.setChecked(true);


      aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              if (b)
              {

                  sharedPref.setNightModeState(true);
                  loadTheme();
                  restartApp();
              }
              else
              {
                  sharedPref.setNightModeState(false);
                  loadTheme();
                  restartApp();
              }
          }
      });
      return v;
    }

    public void loadTheme() {
        if (sharedPref.loadNightMode()) {
            getActivity().setTheme(R.style.darkTheme);
        } else
            getActivity().setTheme(R.style.AppTheme);

    }
    public void restartApp()
    {
     getActivity().recreate();
    }
}