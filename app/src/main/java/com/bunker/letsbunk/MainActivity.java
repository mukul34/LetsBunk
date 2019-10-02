package com.bunker.letsbunk;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bunker.letsbunk.Database.SharedPref;
import com.bunker.letsbunk.Fragment.HomeFragment;
import com.bunker.letsbunk.Fragment.MessageFragment;
import com.bunker.letsbunk.Fragment.SettingFragment;
import com.google.android.material.navigation.NavigationView;



import hotchemi.android.rate.AppRate;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    ImageView imageView;
    Fragment fragment=null;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPref=new SharedPref(this);
        if(sharedPref.loadNightMode())
        {
            setTheme(R.style.darkTheme);
        }else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(1) // default 1
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View headerLayout = navigationView.getHeaderView(0);
        imageView=headerLayout.findViewById(R.id.message_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_id,
                        new MessageFragment()).commit();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_id,
                    new HomeFragment()).commit();
            fragment=new HomeFragment();
            navigationView.setCheckedItem(R.id.nav_Home);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            if(fragment instanceof HomeFragment)
            {
                super.onBackPressed();
            }
            else{
                navigationView.setCheckedItem(R.id.nav_Home);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_id,
                        new HomeFragment()).commit();
                fragment=new HomeFragment();
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_rate:
             {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + this.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" +
                                    this.getPackageName())));
                }
            }
            break;

            case R.id.nav_Setting: {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_id,
                        new SettingFragment()).commit();
                fragment=new SettingFragment();

            }
            break;

            case R.id.nav_Home: {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_id,
                        new HomeFragment()).commit();
                fragment=new HomeFragment();
            }
            break;
            case R.id.nav_share:{
                //lets add the sharing option to the floating action button

                Intent a = new Intent(Intent.ACTION_SEND);

                //this is to get the app link in the playstore without launching your app.
                final String appPackageName = getApplicationContext().getPackageName();
                String strAppLink = "";

                try
                {
                    strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                }
                // this is the sharing part
                a.setType("text/link");
                String shareBody = "Hey! Download this awesome app" +
                        "\n"+""+strAppLink;
                String shareSub = "Let's Bunk";
                a.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                a.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(a, "Share Using"));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}