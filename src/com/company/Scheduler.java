package com.company;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ruben on 11/26/16.
 */
public abstract class Scheduler implements Runnable
{
    public static final double processing_time = 0.0005; //De cuanto en cuanto se debe reducir el proceso, dado en ms
    public static final int time_multiplier = 1000;      //1000 = Segundos , 1 = ms

    public static final double delta = processing_time*time_multiplier; //
    public static final double sleeps = delta*time_multiplier;          //Usado para thread.sleep

    int pendingThreads;                                 //Nos sirve para saber cuando acabamos de procesar todos

    protected BlockingQueue<Job> readyQueue;       //Esta cubeta se le estan depositando y sacando procesos por multiples hilos
    protected Queue<Job> finishedQueue;            //Aqui es donde pone los procesos el scheduler

    public boolean hasPendingThreads()
    {
        return pendingThreads > 0;
    }

    public String getReport()
    {
        //Turnaround = De inicio a fin
        //Waiting    = Turnaround - duration
        String result = "";
        double duracion = 0.0,globalWaiting= 0.0;
        for (Job p:finishedQueue )
        {
            duracion+=p.getFinish_time()-p.getArrival_time();
            result += p.getName()+" Tardo: "+duracion+"\tEn espera "+(duracion-p.getLength())+"|"; //Se agrega un | por que un \n cortaba comunicacion cliente-servidor
            globalWaiting+= duracion-p.getLength();
        }
        result+="Duracion promedio "+globalWaiting/finishedQueue.size()+"s\n";
        return result;
    }

    protected void printBuiltString(char c, int n)
    {
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        System.out.print(new String(arr));
    }

}
