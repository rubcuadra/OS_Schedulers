package com.company;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Main
{
    private static final String file_path="src/com/company/data.txt";
    private static int quantum;
    private static List<Proc> waiting_procs;

    public static void main(String[] args) throws InterruptedException
    {
        setValues(file_path); //waiting_procs ya estan arreglados por el tiempo en el que deben salir

        final BlockingQueue<Proc> priorityBlockingQueue_readyProcesses = new PriorityBlockingQueue<>(waiting_procs.size(),(Proc p1, Proc p2)-> (int)(
                (p1.getPriority()!=p2.getPriority())
                        ?(p1.getPriority()-p2.getPriority())
                        :(p1.getDuration()-p2.getDuration()))); //Comparador


        PriorityScheduler schp = new PriorityScheduler(waiting_procs.size(),priorityBlockingQueue_readyProcesses);
        new Thread(schp).start(); //Scheduler se inicializa

        WaitingToReadyProducer proc_launcher = new WaitingToReadyProducer(waiting_procs,priorityBlockingQueue_readyProcesses);
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