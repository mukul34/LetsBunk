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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bunker.letsbunk.Database.Data;
import com.bunker.letsbunk.Database.Database;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    Activity activity;
    ArrayList<Data> data;
    LayoutInflater inflater;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int criteria=0;
    Database database;
    public  static final String name ="attendance";
    DecimalFormat precision = new DecimalFormat("0.00");






    public MyAdapter(Activity activity,ArrayList<Data> data)
    {
        this.activity = activity;
        this.data = data;

        inflater=LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater LI=activity.getLayoutInflater();
        View vw=LI.inflate(R.layout.subject_info,null);
        return new ViewHolder(vw);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
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

        holder.edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                performEdit(holder);
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

        database.getDataDao().updateAttendance(id,new_attended,new_total);
        int ntot=calculate(holder,new_attended,new_total,criteria);
        double percentage=calPercentage(holder,new_attended,new_total);

        database.getDataDao().addAnswer(id,"You can bunk "+ntot+" lectures");

        database.getDataDao().addPercentage(id, percentage);


        holder.shw_text.setText(new_attended+"/"+new_total);
        holder.Attended.setText(""+new_attended);
        holder.Total.setText(""+new_total);


    }
    private void addUpdate(ViewHolder holder, String sName,int new_attended
                ,int new_total,int criteria)
    {
        int id= itemId(sName);

        new_attended=Integer.parseInt(holder.Attended.getText().toString())+1;
        new_total=Integer.parseInt(holder.Total.getText().toString())+1;

        database.getDataDao().updateAttendance(id,new_attended,new_total);

        int ntot=calculate(holder,new_attended,new_total,criteria);
        double percentage=calPercentage(holder,new_attended,new_total);
        database.getDataDao().addAnswer(id,"You can bunk "+ntot+" lectures");

        database.getDataDao().addPercentage(id, percentage);



        holder.shw_text.setText(new_attended+"/"+new_total);
        holder.Attended.setText(""+new_attended);
        holder.Total.setText(""+new_total);
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
            edit =itemView.findViewById(R.id.edit_button);
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
        float mage= (new_attended/divide);

        int ntot=(int)mage-new_total;
        if (ntot>0)
        {
            holder.answer.setText("You can bunk "+ntot+" lectures");
        }else {
            holder.answer.setText("You can not bunk lecture");
        }
        return ntot;
    }

    private double calPercentage(ViewHolder holder,int new_attended,int new_total)
    {
        double percentage=(double) new_attended/(new_total*0.01);
        holder.percentage.setText(precision.format(percentage)+"%");

        return  percentage;
    }

    private void performEdit(ViewHolder holder)
    {
        holder.Attended.setFocusable(true);
        holder.Attended.setEnabled(true);
        holder.Attended.setClickable(true);
        holder.Attended.setFocusableInTouchMode(true);
    }


}
