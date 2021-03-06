package com.bunker.letsbunk;

import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bunker.letsbunk.Database.Data;
import com.bunker.letsbunk.Database.Database;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    private Activity activity;
    private ArrayList<Data> data;
    private LayoutInflater inflater;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int criteria=0;
    private Database database;
    public  static final String name ="attendance";
    private DecimalFormat precision = new DecimalFormat("0.00");

    public MyAdapter(Activity activity,ArrayList<Data> data)
    {
        this.activity = activity;
        this.data = data;

        inflater=LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater LI=activity.getLayoutInflater();
        View vw=LI.inflate(R.layout.subject_info,null);
        return new ViewHolder(vw);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {


        holder.sub_name.setText(data.get(position).getName());
        holder.Attended.setText(""+data.get(position).getAttended());
        holder.Total.setText(""+data.get(position).getTotal());
        holder.shw_text.setText(""+data.get(position).getAttended()+"/"+data.get(position).getTotal());

        final int new_attended=Integer.parseInt(holder.Attended.getText().toString());
        final int new_total=Integer.parseInt(holder.Total.getText().toString());

        final double percentage=calPercentage(holder,new_attended,new_total);
        holder.percentage.setText(precision.format(percentage)+"%");

        pref=activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor=pref.edit();

        try
        {
           criteria =pref.getInt("criteria_key",75);
        }catch (Exception e)
        {
            Log.e("\nCRITERIA\n","\n"+e.getMessage());
        }

        calculate(holder,new_attended,new_total,criteria);

        final Data infoData = data.get(position);


        database= Room.databaseBuilder(activity, Database.class,"attendance_record")
                .allowMainThreadQueries().build();
        final String sName=holder.sub_name.getText().toString();
        holder.add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addUpdate(holder,sName,new_attended,new_total,criteria);

            }
        });

        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                subUpdate(holder,sName,new_attended,new_total,criteria);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                deleteAtt(holder,sName,infoData);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performEdit(holder,sName);
            }
        });

    }

    private void deleteAtt(ViewHolder holder, String sName,Data infoData)
    {
        removeItem(infoData);
        int id= itemId(sName);
        database.getDataDao().deleteAttendance(id);
    }


    private  void subUpdate(ViewHolder holder,String sName,int new_attended,
                            int new_total,int criteria)
    {
        int id= itemId(sName);


        new_attended=Integer.parseInt(holder.Attended.getText().toString());
        new_total=Integer.parseInt(holder.Total.getText().toString())+1;

        set_and_update(holder,id,new_attended,new_total);

    }
    private void addUpdate(ViewHolder holder, String sName,int new_attended
                ,int new_total,int criteria)
    {
        int id= itemId(sName);

        new_attended=Integer.parseInt(holder.Attended.getText().toString())+1;
        new_total=Integer.parseInt(holder.Total.getText().toString())+1;

        set_and_update(holder,id,new_attended,new_total);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView Attended,Total;
        LinearLayout linearLayout;
        FloatingActionButton add,subtract;
        TextView answer,percentage;
        TextView sub_name;
        TextView shw_text;
        ImageView delete,edit;
        public ViewHolder(View itemView)
        {
            super(itemView);
            sub_name=itemView.findViewById(R.id.subject_name);
            linearLayout=itemView.findViewById(R.id.layout);
            Attended =itemView.findViewById(R.id.attended_textView);
            Total =itemView.findViewById(R.id.total_textView);
            add=itemView.findViewById(R.id.add_button);
            subtract=itemView.findViewById(R.id.subtract_button);
            answer=itemView.findViewById(R.id.answer);
            shw_text=itemView.findViewById(R.id.show_text);
            delete=itemView.findViewById(R.id.delete);
            percentage=itemView.findViewById(R.id.percentage);
            edit=itemView.findViewById(R.id.edit_button);
        }
    }

    private int itemId(String sName)
    {
        Cursor data=database.getDataDao().getItemID(sName);
        int itemID=-1;
        while (data.moveToNext())
        {
            itemID=data.getInt(0);
        }

        if (itemID>-1)
        {
            Log.e("ITEM ID \n","You clicked on "+itemID);
        }
        else
        {
            Log.e("ITEM ID \n","No ID is related to this item");
        }

        return itemID;

    }

    private void removeItem(Data infoData)
    {

        int currPosition = data.indexOf(infoData);
        data.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    private int calculate(ViewHolder holder,int new_attended,int new_total,int criteria)
    {
        float divide= (float) (criteria*0.01);
        float per_age= (new_attended/divide);

        int nLect=(int)per_age-new_total;
        if (nLect>0)
        {
            holder.answer.setText("You can bunk "+nLect+" lectures");
            holder.answer.setTextColor(activity.getResources().getColor(R.color.answerColor));
        }else {
            holder.answer.setText("You can not bunk lecture");
            holder.answer.setTextColor(activity.getResources().getColor(R.color.answer));
        }
        return nLect;
    }

    private double calPercentage(ViewHolder holder,int new_attended,int new_total)
    {
        double percentage=(double) new_attended/(new_total*0.01);
        holder.percentage.setText(precision.format(percentage)+"%");

        return  percentage;
    }

    private void performEdit(final ViewHolder holder, String sName)
    {
        final int id= itemId(sName);
        final Dialog dialog=new Dialog(activity);
        dialog.setContentView(R.layout.edit_dialog);
        final EditText attended_dialog = dialog.findViewById(R.id.att);
        final EditText total_dialog = dialog.findViewById(R.id.tot);

        Button submitButton = dialog.findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                int attended = Integer.parseInt(attended_dialog.getText().toString());
                int total = Integer.parseInt(total_dialog.getText().toString());

                 if (attended <= total) {
                     set_and_update(holder,id,attended,total);
                     dialog.cancel();
                 }
                 else
                     attended_dialog.setError("Not more than total lectures");
                }catch (Exception e)
                {
                    if (attended_dialog.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Try Again",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (total_dialog.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Try Again",
                                Toast.LENGTH_SHORT).show();
                    }
                     else
                        Toast.makeText(activity, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void set_and_update(ViewHolder holder,int id,int attended,int total)
    {
        database.getDataDao().updateAttendance(id,attended,total);
        int nLect=calculate(holder,attended,total,criteria);
        double percentage=calPercentage(holder,attended,total);

        database.getDataDao().addAnswer(id,"You can bunk "+nLect+" lectures");

        database.getDataDao().addPercentage(id, percentage);

        holder.shw_text.setText(attended+"/"+total);
        holder.Attended.setText(""+attended);
        holder.Total.setText(""+total);
    }
}