package com.bunker.letsbunk.Fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bunker.letsbunk.Database.Data;
import com.bunker.letsbunk.Database.Database;

import com.bunker.letsbunk.MyAdapter;
import com.bunker.letsbunk.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton add;
    MyAdapter adapter;

    Button submitButton;

    Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        /*recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));*/

        add = v.findViewById(R.id.add_Field);


        database = Room.databaseBuilder(getContext(), Database.class, "attendance_record")
                .allowMainThreadQueries().build();

        setData();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performCheck();
            }
        });
        return v;
    }

    private void setData() {
        ArrayList<Data> arrayList = new ArrayList<>(database.getDataDao().getAllrecord());
        adapter = new MyAdapter(getActivity(), arrayList);
       // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        recyclerView.setAdapter(adapter);
    }

    private void performCheck() {
        final EditText attended_dialog, total_dialog, subName_dialog;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog);

        attended_dialog = dialog.findViewById(R.id.att);
        total_dialog = dialog.findViewById(R.id.tot);
        subName_dialog = dialog.findViewById(R.id.name);

        submitButton = dialog.findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int attended = Integer.parseInt(attended_dialog.getText().toString());
                int total = Integer.parseInt(total_dialog.getText().toString());
                String name = subName_dialog.getText().toString();
                List<String> nList = database.getDataDao().getAllNames();

                try {
                    boolean flag = false;
                    for (String nListSubName : nList) {
                        if (nListSubName.equalsIgnoreCase(name)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        subName_dialog.setError("Change subject name");
                    }
                    if (subName_dialog.getText().toString().isEmpty()) {
                             subName_dialog.setError("Enter subject name");
                    }
                    if (attended <= total) {
                        Data data = new Data(name, attended, total);
                        long value = database.getDataDao().addAttendance(data);
                        if (value > 0) {
                            Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
                        }
                        setData();
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                    else
                        attended_dialog.setError("Not more than total lectures");
                } catch (Exception e) {
                    if (attended_dialog.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Try Again",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (total_dialog.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Try Again",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (subName_dialog.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Try Again",
                                Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getActivity(), "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}