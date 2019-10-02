package com.bunker.letsbunk.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "myData")
public class Data
{

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long id;


    @ColumnInfo(name = "subject_name")
    private String name;

    @ColumnInfo(name = "attended_lecture")
    private int attended;

    @ColumnInfo(name = "total_lecture")
    private int total;

    @ColumnInfo(name = "answer")
    private String answer;

    @ColumnInfo(name="percentage")
    private double percentage;

    @Ignore
    public Data()
    {

    }

    public Data(String name,int attended, int total)
    {
        this.name=name;
        this.total = total;
        this.attended = attended;
    }

    @Ignore
    public Data(int id,String answer)
    {
        this.id=id;
        this.answer=answer;
    }




   public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }



    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public int getAttended()
    {
        return attended;
    }

    public void setAttended(int attended)
    {
        this.attended = attended;
    }


}
