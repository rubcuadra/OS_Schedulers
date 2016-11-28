package com.company;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.company.Scheduler.time_multiplier;

/**
 * Created by Ruben on 11/19/16.
 */
public class WaitingToReadyProducer implements Runnable
{
    protected BlockingQueue<Job> readyQueue;
    protected List<Job> waitingJobs;

    public WaitingToReadyProducer(List<Job> waitingJob, BlockingQueue<Job> readyQueue)
    {
        this.waitingJobs = waitingJob;
        this.readyQueue = readyQueue;
    }

    @Override
    public void run()
    {
        int start = (int) System.currentTimeMillis();  //Cada 1000 son 1 segundo

        while (waitingJobs.size()!=0) //Almost true
        {
            try
            {
                double time_past_in_mili = (double)((int)System.currentTimeMillis()-start);

                if (waitingJobs.get(0).getArrival_time()*time_multiplier <= time_past_in_mili) //Si el tiempo de llegada es menor o igual al tiempo actual, lanzarlo
                {
                    //System.out.println("|-> "+ waitingJobs.get(0) );
                    readyQueue.put(waitingJobs.remove(0));
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}