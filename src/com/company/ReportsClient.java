package com.company;

import java.io.*;
import java.net.*;
/**
 * Created by Ruben on 11/19/16.
 */
public class ReportsClient implements Runnable
{
    private static int port;
    private static String host;
    private static String msg;
    ReportsClient(String host,int serverPort,String msg)
    {
        this.msg = msg;
        this.host = host;
        this.port = serverPort;
    }

    @Override
    public void run()
    {
        String serverHostname = new String (host);


        System.out.println ("Attemping to connect to host "+serverHostname+ " on port "+port);

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try
        {
            echoSocket = new Socket(serverHostname, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        }
        catch (UnknownHostException e)
        {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        //ESTO MANDA EL MENSAJE
        out.println(msg);


        out.close();
        try
        {
            in.close();
            stdIn.close();
            echoSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
