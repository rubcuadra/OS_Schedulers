package com.company;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadFactory;

public class Main
{
    private static final String file_path="src/com/company/data.txt";
    private static int quantum;
    private static int initial_proc = 10;
    private static Scheduler_Priority sch_p;
    private static List<Proc> processes;

    public static void main(String[] args) throws InterruptedException
    {

        setValues(file_path); //processes ya estan arreglados por el tiempo en el que deben salir

        sch_p = new Scheduler_Priority(initial_proc);

        synchronized (sch_p)
        {
            sch_p.start();

            int start = (int) System.currentTimeMillis();  //Cada 1000 son 1 segundo
            while (processes.size()!=0)
            {
                double time_past_in_mili = (double)((int)System.currentTimeMillis()-start);
                if (processes.get(0).getArrival_time()*1000 <= time_past_in_mili) //Si el tiempo de llegada es menor o igual al tiempo actual, lanzarlo
                    sch_p.addProcess(processes.remove(0)); //Esto no es concurrente POR?
            }

            sch_p.set_Alive(false); //Decirle que ya no hay mas procesos
            sch_p.wait();           //Esperar a que despache todos para acabar
        }
    }

    private static boolean setValues(String p) //los lee del archivo
    {
        //Por tiempos
        processes = new ArrayList<>();

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
                    processes.add(new Proc(current_line));
                }
            }
        } catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        Collections.sort(processes,(Proc o1,Proc o2)-> (int)(o1.getArrival_time() - o2.getArrival_time()));
        return true;
    }

}
