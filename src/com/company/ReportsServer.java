package com.company;

import java.net.*;
import java.io.*;

/**
 * Created by Ruben on 11/19/16.
 */
public class ReportsServer implements Runnable
{
    private static int port;
    ReportsServer(int port) {this.port = port;}

    @Override
    public void run()
    {
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: "+port);
            System.exit(1);
        }

        Socket clientSocket = null;
        System.out.println ("Waiting for connection.....");

        try
        {
            clientSocket = serverSocket.accept();
        }
        catch (IOException e)
        {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        try
        {

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                System.out.println (inputLine);
                out.println(inputLine);
                if (true || inputLine.equals("Bye.")) break;
            }

            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}