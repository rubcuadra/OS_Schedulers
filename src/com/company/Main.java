package com.company;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadFactory;

public class Main
{
    private static final String file_path="src/com/company/data.txt";
    private static int quantum;
    private static int initial_proc = 1;
    private static Scheduler_Priority sch_p;
    private static Queue<Proc> processes;

    public static void main(String[] args) throws InterruptedException
    {
        sch_p = new Scheduler_Priority(initial_proc);

        setValues(file_path); //processes ya estan arreglados por el tiempo en el que deben salir

        synchronized (sch_p)
        {
            sch_p.start();
            Thread.sleep(2000);
            int start = (int) System.currentTimeMillis();  //Cada 1000 son 1 segundo
            while (processes.peek()!=null)
            {
                double time_past_in_mili = (double)((int)System.currentTimeMillis()-start);
                //Si el tiempo de llegada es menor o igual al tiempo actual, lanzarlo
                if (processes.peek().getArrival_time()*1000 <= time_past_in_mili)
                    sch_p.addProcess(processes.poll());
            }

            sch_p.set_Alive(false); //Decirle que ya no hay mas procesos
            sch_p.wait();   //Esperar a que despache todos para acabar
        }


        /*
        En esto punto processes tiene una lista de procesos que se deben ejecutar a ciertos tiempos, la idea es que el Scheduler tenga una Cola de prioridades de procesos
        el main va a ordenar los procesos por tiempo y los va ir agregando a esta cola cuando llegue el tiempo en el que se dispara, entonces el scheduler mientras lo ejecuta cada
        segundo/ciclo debe comparar si el elemento tope de la cola(El cual debe ser el de mayor prioridad) tiene mayor prioridad que el que se esta ejecutando, en caso de que SI
        entonces lo toma, lo ejecuta y manda el que tenia a la cola

        En caso de misma prioridad, el de menor tiempo tiene
         */

    }

    private static boolean setValues(String p) //los lee del archivo
    {
        //Por tiempos
        processes = new PriorityQueue<Proc>(initial_proc, (Proc o1,Proc o2)-> (int)(o1.getArrival_time()- o2.getArrival_time()));

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
        return true;
    }

}
