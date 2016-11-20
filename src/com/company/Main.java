package com.company;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Main
{
    private static final String file_path="src/com/company/data.txt";
    private static int quantum;
    private static int initial_proc = 10;
    private static Scheduler_Priority sch_p;
    private static List<Proc> waiting_procs;

    public static void main(String[] args) throws InterruptedException
    {
        setValues(file_path); //waiting_procs ya estan arreglados por el tiempo en el que deben salir

        final BlockingQueue<Proc> priorityBlockingQueue_readyProcesses = new PriorityBlockingQueue<>(waiting_procs.size(),(Proc p1, Proc p2)-> (int)(
                (p1.getPriority()!=p2.getPriority())
                        ?(p1.getPriority()-p2.getPriority())
                        :(p1.getDuration()-p2.getDuration()))); //LinkedBlockingQueue

        sch_p = new Scheduler_Priority(initial_proc);


        PriorityScheduler schp = new PriorityScheduler(waiting_procs.size(),priorityBlockingQueue_readyProcesses);
        new Thread(schp).start(); //Scheduler

        waitingToReadyProducer proc_launcher = new waitingToReadyProducer(waiting_procs,priorityBlockingQueue_readyProcesses);
        new Thread(proc_launcher).start(); //Lanzamos el productor
    }

    private static boolean setValues(String p) //los lee del archivo
    {
        //Por tiempos
        waiting_procs = new ArrayList<>();

        boolean firstLine = true;
        try (Scanner scanner = new Scanner(new File(p)))
        {
            while (scanner.hasNext())
            {
                if (firstLine)
                {
                    quantum = Integer.parseInt(scanner.nextLine() );
                    firstLine = false;
                }
                else
                {
                    String[] current_line = scanner.nextLine().split(",");
                    waiting_procs.add(new Proc(current_line));
                }
            }
        } catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        Collections.sort(waiting_procs,(Proc o1, Proc o2)-> (int)(o1.getArrival_time() - o2.getArrival_time()));
        return true;
    }

}

class waitingToReadyProducer implements Runnable
{
    protected BlockingQueue<Proc> readyQueue;
    protected List<Proc> waitingProcs;

    public waitingToReadyProducer(List<Proc> waitingProc,BlockingQueue<Proc> readyQueue)
    {
        this.waitingProcs = waitingProc;
        this.readyQueue = readyQueue;
    }

    @Override
    public void run()
    {
        int start = (int) System.currentTimeMillis();  //Cada 1000 son 1 segundo
        while (waitingProcs.size()!=0) //Almost true
        {
            //System.out.println("Producer");
            try
            {
                double time_past_in_mili = (double)((int)System.currentTimeMillis()-start);

                if (waitingProcs.get(0).getArrival_time()*1000 <= time_past_in_mili) //Si el tiempo de llegada es menor o igual al tiempo actual, lanzarlo
                {
                    System.out.println("Lanzando proceso "+waitingProcs.get(0) );
                    readyQueue.put(waitingProcs.remove(0));
                }
                Thread.sleep(250);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}


class PriorityScheduler implements Runnable
{

    private static double processing_time=0.5;
    protected BlockingQueue<Proc> readyQueue;
    private int totalThreads;

    public PriorityScheduler(int totalThreads,BlockingQueue<Proc> queue)
    {
        this.totalThreads=totalThreads;
        this.readyQueue = queue;
    }


    @Override
    public void run()
    {
        Proc top_waiting,temp;
        Proc running=null;
        while ( (top_waiting=readyQueue.peek())!=null || totalThreads>0)
        {
            //System.out.println("Scheduler");
            try
            {
                if (running==null && top_waiting==null) {continue;} //Sleep?
                if (running == null)  //Caso base, volver running el top
                {
                    running = readyQueue.poll();
                    //System.out.println("Agregado como running "+running);
                }
                else if (top_waiting!=null) //Si tenemos alguno en espera
                {
                    if (running.getPriority() > top_waiting.getPriority()) //Si el actual tiene mayor numero (menor prioridad) cambiarlos
                    {
                        System.out.println("Movido "+running+" a cola");
                        temp = running;
                        running = readyQueue.poll();
                        readyQueue.put(temp);
                    } else if (running.getPriority() == top_waiting.getPriority() && running.getDuration() > top_waiting.getDuration())
                    {
                        //Si tienen misma prioridad y el actual tardara mas, cambiarlos
                        System.out.println("Movido "+running+" a cola");
                        temp = running;
                        running = readyQueue.poll();
                        readyQueue.put(temp);
                    }
                }
                //Procesar running, restar duracion y dar ciclo; si la duracion llega <0 volver null running
                System.out.println("Ejecutando "+running);
                running.setDuration(running.getDuration() - processing_time);
                if (running.getDuration() < 0 )
                {
                    --totalThreads;
                    System.out.println("Terminado de procesar "+running.toString());
                    running = null;
                }
                Thread.sleep(250);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
