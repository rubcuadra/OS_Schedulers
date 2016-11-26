package com.company;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ruben on 11/19/16.
 */
public class PriorityScheduler implements Runnable
{

    private static double processing_time=0.5;
    protected BlockingQueue<Proc> readyQueue;
    public Queue<Proc> finishedQueue;
    private int pendingThreads;

    public PriorityScheduler(int totalThreads,BlockingQueue<Proc> queue)
    {
        this.pendingThreads = totalThreads;
        this.readyQueue = queue;
        this.finishedQueue= new LinkedList<>();
    }

    public int getPendingThreads()
    {
        return pendingThreads;
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
                //System.out.println("Current time "+current_time);
                if (running==null && top_waiting==null)
                {
                    Thread.sleep((long) (processing_time*1000));
                    current_time+=processing_time;
                    continue;
                } //No ha caido nada
                if (running == null)  //Caso base, volver running el top
                {
                    running = readyQueue.poll();
                    //System.out.println("Agregado como running "+running);
                }
                else if (top_waiting!=null) //Si tenemos alguno en espera
                {
                    if (running.getPriority() > top_waiting.getPriority()) //Si el actual tiene mayor numero (menor prioridad) cambiarlos
                    {
                        System.out.println("Movido "+running.getName()+" a cola");
                        temp = running;
                        running = readyQueue.poll();
                        readyQueue.put(temp);
                    } else if (running.getPriority() == top_waiting.getPriority() && running.getDuration() > top_waiting.getDuration())
                    {
                        //Si tienen misma prioridad y el actual tardara mas, cambiarlos
                        System.out.println("Movido "+running.getName()+" a cola");
                        temp = running;
                        running = readyQueue.poll();
                        readyQueue.put(temp);
                    }
                }
                //Procesar running, restar duracion y dar ciclo; si la duracion llega <0 volver null running
                System.out.println("Ejecutando "+running);
                running.setDuration(running.getDuration() - processing_time);
                Thread.sleep((long) (processing_time*1000));
                current_time+=processing_time;
                if (running.getDuration() < 0 )
                {
                    --pendingThreads;
                    System.out.println("Terminado de procesar "+running.getName());
                    running.setFinish_time(current_time);
                    finishedQueue.add(running);
                    running = null;
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}