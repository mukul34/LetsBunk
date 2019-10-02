package com.bunker.letsbunk.Fragment;


import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bunker.letsbunk.Database.SharedPref;
import com.bunker.letsbunk.R;


public class MessageFragment extends Fragment {

    SharedPref sharedPref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_message, container, false);
        sharedPref=new SharedPref(getActivity());
        ImageView imageView=view.findViewById(R.id.messageImage);
        if(sharedPref.loadNightMode())
        {
            imageView.setImageResource(R.drawable.darkmode_msg);
            getContext().setTheme(R.style.darkTheme);

        }else
            getContext().setTheme(R.style.AppTheme);

        DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return view;
    }
}
