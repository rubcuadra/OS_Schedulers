package com.company;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ruben on 11/19/16.
 */
public class WaitingToReadyProducer implements Runnable
{
    protected BlockingQueue<Proc> readyQueue;
    protected List<Proc> waitingProcs;

    public WaitingToReadyProducer(List<Proc> waitingProc, BlockingQueue<Proc> readyQueue)
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
            try
            {
                double time_past_in_mili = (double)((int)System.currentTimeMillis()-start);
                if (waitingProcs.get(0).getArrival_time()*1000 <= time_past_in_mili) //Si el tiempo de llegada es menor o igual al tiempo actual, lanzarlo
                {
                    System.out.println("Lanzando proceso "+waitingProcs.get(0) );
                    readyQueue.put(waitingProcs.remove(0));
                }
                Thread.sleep(250); //No hay que saturarlos
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}