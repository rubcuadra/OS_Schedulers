package com.company;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static com.company.Scheduler.*;

public class Main
{
    private static final int PORT = 4444; //EN el que se comunica cliente servidor para imprimir datos
    private static final String HOST = "127.0.0.1"; //localhost

    private static final String file_path="src/com/company/procesos.txt";
    private static final String second_file_path="src/com/company/procesos1-temp.txt";
    private static List<Job> waiting_jobs;
    private static int quantum;

    public static void main(String[] args) throws InterruptedException, IOException
    {
        int choice = 1;         //0 = Prioridad; 1 = Round Robin
        Scheduler scheduler;    //Aqui guardaremos el objeto scheduler

        final BlockingQueue<Job> readyProcesses; //Prioridad ocupa comparador especial; Robin solo un FIFO
        setValues(file_path);                     //waiting_jobs ya estan arreglados por el tiempo en el que deben salir
        drawGantt(waiting_jobs);                 //Dibujamos los procesos

        switch (choice)
        {
            case 0: //Priority
                readyProcesses =  new PriorityBlockingQueue<>(waiting_jobs.size(),(Job p1, Job p2)-> (int)((p1.getPriority()!=p2.getPriority()) ?(p1.getPriority()-p2.getPriority()) :(p1.getDuration()-p2.getDuration())));
                scheduler = new PriorityScheduler(waiting_jobs.size(),readyProcesses);
                break;
            default:
                readyProcesses = new LinkedBlockingQueue<>(); //Una cubeta normal, la prioridad no se requiere
                scheduler = new RoundRobinScheduler(quantum, waiting_jobs.size(),readyProcesses);
                break;
        }

        //Con esto instanciamos el hilo que servira los procesos
        Thread scheduler_thread = new Thread(scheduler);
        //Hilo que levantara procesos
        Thread proc_launcher_thread = new Thread(new WaitingToReadyProducer(waiting_jobs,readyProcesses));
        //Este hilo esta escuchando a que le reporten resultados para imprimirlos
        Thread report_server_thread = new Thread(new ReportsServer(PORT));

        report_server_thread.start();                 //Levantamos el servidor que imprime resultados
        scheduler_thread.start();                     //Levantamos el Planificador solicitado
        proc_launcher_thread.start();                 //Levantamos el hilo que dispara procesos

        while (  scheduler.hasPendingThreads() )      //Mientras no se acaben los procesos, esperar
        {Thread.sleep((long) sleeps);}                         //Aqui ya podemos enviar un reporte

        //Este hilo es el cliente, lanza el reporte al servidor, el cual lo imprime
        (new Thread(new ReportsClient(HOST,PORT,scheduler.getReport()))).start();
    }

    public static void drawGantt(List<Job> p)
    {
        double time_lapse = 0.5; //Cada cuanto pintar, esto se le resta al duration del proc
        //Cada segundo equivale a 2 "_" y a 2 " "
        String line = "Proceso# -> Prioridad\n";
        for (int i = 0; i < p.size() ; i++)
        {
            line+=p.get(i).getName()+" -> "+p.get(i).getPriority()+"\t|";
            for (double j = 0; j < p.get(i).getArrival_time(); j+=time_lapse) {line += " ";}
            for (double j = 0; j < p.get(i).getDuration(); j+=time_lapse)
            {line += "-";}
            line+=">\n";
        }
        System.out.print(line);
    }

    private static boolean setValues(String p) //los lee del archivo
    {
        waiting_jobs = new ArrayList<>(); //Por tiempos
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
                    String[] current_line = scanner.nextLine().split(" ");
                    waiting_jobs.add(new Job(current_line));
                }
            }
        } catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        Collections.sort(waiting_jobs,(Job o1, Job o2)-> (int)((o1.getArrival_time()!=o2.getArrival_time()?o1.getArrival_time()-o2.getArrival_time():o1.getPriority()-o2.getPriority())));
        return true;
    }

}