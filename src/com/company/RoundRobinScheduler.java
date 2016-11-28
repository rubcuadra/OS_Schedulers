package com.company;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ruben on 11/26/16.
 */
public class RoundRobinScheduler extends Scheduler
{
    int quantum;

    RoundRobinScheduler(int quantum,int totalThreads,BlockingQueue<Job> queue)
    {
        this.quantum = quantum;                  //Parte del algoritmo
        this.pendingThreads = totalThreads;      //Numero que nos dice cuando debemos acabar de procesar
        this.readyQueue = queue;                 //En esta cubeta estan los procesos que debemos procesar
        this.finishedQueue = new LinkedList<>(); //Aqui debemos meter los que ya acabamos de procesar
    }

    @Override
    public void run()
    {
        System.out.println("Starting Round Robin");
        Job top_waiting,running;
        double current_time = 0; //In seconds

        while ( (top_waiting=readyQueue.peek())!=null || pendingThreads >0) //Mientras no hayamos acabado
        {
            try
            {
                if (top_waiting==null) //Delay hasta que nos caiga uno
                {
                    Thread.sleep((long) (100));
                    current_time+=0.1;
                    continue;
                }

                running = readyQueue.poll(); //Sacamos el de arriba

                System.out.print(running.getName()+" ");
                printBuiltString('.',(int)current_time);

                if (running.getDuration()<quantum) //Aqui se acabara
                {
                    printBuiltString('x',(int)running.getDuration());
                    System.out.print("|");

                    Thread.sleep((long) (running.getDuration()*time_multiplier)); //Lo procesamos
                    current_time+=running.getDuration();          //updateamos el tiempo
                    running.setDuration(0);
                    running.setFinish_time(current_time);
                    finishedQueue.add(running);                                   //Lo agregamos a terminados
                    --pendingThreads;
                }
                else
                {
                    printBuiltString('x',(int)quantum);
                    Thread.sleep((long)(quantum*time_multiplier)); //Lo procesamos
                    running.setDuration(running.getDuration()-quantum);
                    current_time+=quantum;
                    readyQueue.put(running);                        //Lo agregamos a la cola
                }
                System.out.println();

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
}
