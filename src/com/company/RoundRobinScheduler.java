package com.company;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.logging.ErrorManager;

/**
 * Created by Ruben on 11/26/16.
 */
public class RoundRobinScheduler extends Scheduler
{
    int quantum;

    RoundRobinScheduler(int quantum,int totalThreads,BlockingQueue<Proc> queue)
    {
        this.quantum = quantum;                  //Parte del algoritmo
        this.pendingThreads = totalThreads;      //Numero que nos dice cuando debemos acabar de procesar
        this.readyQueue = queue;                 //En esta cubeta estan los procesos que debemos procesar
        this.finishedQueue = new LinkedList<>(); //Aqui debemos meter los que ya acabamos de procesar
    }

    @Override
    public void run()
    {
        Proc top_waiting,temp; Proc running=null;
        double current_time = 0; //In seconds

        while ( (top_waiting=readyQueue.peek())!=null || pendingThreads >0)
        {
            try
            {
                

                Thread.sleep((long) (sleeps));
                break;

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
}
