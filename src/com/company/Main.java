package com.company;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Main
{
    private static final int PORT = 4444; //EN el que se comunica cliente servidor para imprimir datos
    private static final String HOST = "127.0.0.1"; //localhost
    private static final String file_path="src/com/company/data.txt";
    private static List<Proc> waiting_procs;
    private static int quantum;

    public static void main(String[] args) throws InterruptedException, IOException
    {
        final BlockingQueue<Proc> priorityBlockingQueue_readyProcesses = new PriorityBlockingQueue<>(waiting_procs.size(),(Proc p1, Proc p2)-> (int)(
                (p1.getPriority()!=p2.getPriority())
                        ?(p1.getPriority()-p2.getPriority())
                        :(p1.getDuration()-p2.getDuration()))); //Comparador

        setValues(file_path);       //waiting_procs ya estan arreglados por el tiempo en el que deben salir
        drawGantt(waiting_procs);   //Los dibujamos

        Thread Scheduler_Priority = new Thread(new PriorityScheduler(waiting_procs.size(),priorityBlockingQueue_readyProcesses));
        Thread proc_launcher = new Thread(new WaitingToReadyProducer(waiting_procs,priorityBlockingQueue_readyProcesses));
        Thread rs = new Thread(new ReportsServer(PORT));
        Thread rc = new Thread(new ReportsClient(HOST,PORT,"HOLA MUNDO"));

        rs.start();                 //Levantamos el servidor que imprime resultados
        Scheduler_Priority.start(); //Levantamos el Planificador de prioridades
        proc_launcher.start();      //Levantamos el hilo que dispara procesos

        rc.start(); //Asegurarnos que ya acabo el planificador y que ya esta corriendo el server para reportar datos
    }

    public static void drawGantt(List<Proc> p)
    {
        //Cada segundo equivale a 2 "_" y a 2 " "
        String line = "Proceso# -> Prioridad:\n";

        for (int i = 0; i < p.size() ; i++)
        {
            line+=p.get(i).getName()+" -> "+p.get(i).getPriority()+"\t|";
            for (int j = 0; j < p.get(i).getArrival_time(); j++)
            {
                line += "  ";
            }
            for (int j = 0; j < p.get(i).getDuration(); j++)
            {
                line += "--";
            }
            line+=">\n";
        }

        System.out.print(line);
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