package com.company;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ruben on 11/26/16.
 */
public abstract class Scheduler implements Runnable
{
    public static final double processing_time=0.5; //De cuanto en cuanto se debe reducir el proceso
    protected int pendingThreads;                   //Nos sirve para saber cuando acabamos de procesar todos

    protected BlockingQueue<Proc> readyQueue;       //Esta cubeta se le estan depositando y sacando procesos por multiples hilos
    protected Queue<Proc> finishedQueue;            //Aqui es donde pone los procesos el scheduler

    public Queue<Proc> getFinishedQueue()
    {
        return finishedQueue;
    }
    public boolean hasPendingThreads()
    {
        return pendingThreads > 0;
    }

    public String getReport()
    {
        String result = "";
        double duracion = 0.0,globalWaiting= 0.0;
        for (Proc p:finishedQueue )
        {
            duracion+=p.getFinish_time()-p.getArrival_time();
            result += p.getName()+" Tardo: "+duracion+"\tEn espera "+(duracion-p.getLength())+"|"; //Se agrega un | por que un \n cortaba comunicacion cliente-servidor
            globalWaiting+= duracion-p.getLength();
        }
        result+="Duracion promedio "+globalWaiting/finishedQueue.size()+"s\n";
        return result;
    }

}
