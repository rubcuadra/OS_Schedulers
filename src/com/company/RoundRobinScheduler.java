package com.company;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ruben on 11/26/16.
 */
public class RoundRobinScheduler extends Scheduler
{
    int quantum;

    RoundRobinScheduler(int quantum,int totalThreads,BlockingQueue<Proc> queue)
    {
        this.quantum = quantum;                  //Parte del algoritmo
        this.pendingThreads = totalThreads;      //Numero que nos dice cuando debemos acabar de procesar
        this.readyQueue = queue;                 //En esta cubeta estan los procesos que debemos procesar
        this.finishedQueue = new LinkedList<>(); //Aqui debemos meter los que ya acabamos de procesar
    }

    @Override
    public void run()
    {

    }
}
