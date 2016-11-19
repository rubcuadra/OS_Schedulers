package com.company;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Ruben on 11/18/16.
 */
public class Scheduler_Priority extends Thread
{
    private static final double processing_time = 0.5; //Cada ciclo restara medio segundo al proceso

    public static Queue<Proc> readyQueue;
    private boolean _alive = true;
    private Proc running = null;

    Scheduler_Priority(int initial)
    {
        readyQueue = new PriorityBlockingQueue<Proc>(initial,(Proc p1, Proc p2)-> (int)(
                (p1.getPriority()!=p2.getPriority())
                        ?(p1.getPriority()-p2.getPriority())
                        :(p1.getDuration()-p2.getDuration())));
    } //Prioridad diferente pesa prioridad, caso igual pesa duracion

    public void addProcess(Proc newP)
    {
        System.out.println("Llego proceso "+newP);
        readyQueue.add(newP);
    }

    @Override
    public void run()
    {
        synchronized (Scheduler_Priority.this)
        {
            System.out.println("Corriendo Scheduler Priority");
            Proc top,temp;
            //Mientras existan procesos o nos digan que sigamos vivos
            while ( (top = readyQueue.peek()) != null || _alive)
            {
                System.out.println(".");
                if (running==null && top==null) {continue;} //Sleep?

                if (running == null)  //Caso base, volver running el top
                {
                    running = readyQueue.poll();
                    System.out.println("Agregado como running "+running);
                }
                else if (top!=null) //Si tenemos alguno en espera
                {
                    if (running.getPriority() < top.getPriority()) //Si el actual tiene menor prioridad cambiarlos
                    {
                        temp = running;
                        running = readyQueue.poll();
                        readyQueue.add(temp);
                    } else if (running.getPriority() == top.getPriority() && running.getDuration() > top.getDuration())
                    {
                        //Si tienen misma prioridad y el actual tardara mas, cambiarlos
                        temp = running;
                        running = readyQueue.poll();
                        readyQueue.add(temp);
                    }
                }
                //Procesar running, restar duracion y dar ciclo; si la duracion llega <0 volver null running
                System.out.println("Ejecutando "+running);
                running.setDuration(running.getDuration()-processing_time);
                if (running.getDuration() < 0 )
                {
                    System.out.println("Terminado de procesar "+running.toString());
                    running = null;
                }
            }

            notify();
        }
    }
    public void set_Alive(boolean alive) {this._alive = alive;}
}
