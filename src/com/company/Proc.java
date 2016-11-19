package com.company;

/**
 * Created by Ruben on 11/18/16.
 */
public class Proc implements Comparable<Proc>
{
    private String name;
    private double arrival_time,priority,duration;

    Proc(String[] file_line)
    {
        name = file_line[0];
        arrival_time = Double.parseDouble(file_line[1]);
        duration = Double.parseDouble(file_line[2]);
        priority = Double.parseDouble(file_line[3]);
    }
    @Override
    public String toString()
    {
        return name + " Remaining:"+duration+"s\tP:"+priority+"\t\t Arrived: "+arrival_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(double arrival_time) {
        this.arrival_time = arrival_time;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(Proc o)
    {
        return (int)(this.arrival_time-o.arrival_time);
    }
}
