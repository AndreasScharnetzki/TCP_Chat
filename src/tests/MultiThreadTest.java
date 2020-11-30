package tests;

import implementation.ChatUIImpl;
import interfaces.ChatUI;
import org.junit.Test;

import java.io.*;

public class MultiThreadTest implements Runnable {

    //this is to ensure childThread has a reference to mainThread
    private Thread mainThreadReference = Thread.currentThread();
    private Thread serverThread;

    private final long ETERNALSLEEP = Long.MAX_VALUE;
    private final int PORT = 7777;
    private boolean constantSignal = true;

    // private int count = Thread.activeCount();

    private final String CMD_OPEN = "open " + PORT;
    private final String CMD_CONNECT = "con localhost " + PORT;
    private final String CMD_WRITE = ChatUI.CMD_WRITE.concat( " " );

    @Test
    public void multiThreadTest() throws Exception {

        serverThread = new Thread( new MultiThreadTest() );
        serverThread.start();

        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            System.out.println("Trying to connect now...");
        }

        Thread.currentThread().sleep( 2000 );

        ChatUI client = new ChatUIImpl();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( baos );

        try {
            dos.writeUTF( CMD_CONNECT );
        } catch (IOException e) {
            e.printStackTrace();
        }

//---------------------- connection established ---------------------

        byte[] inputBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream( inputBytes );
        InputStream myInputStream = bais;

        ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();

        //run with connect
        client.runUI( myInputStream, myOutputStream );

        //Thread displayOfWhatServerReceives = new Thread(  )
        serverThread.interrupt();                               //point 1
        Thread.sleep( 1000 );
        myOutputStream.reset();

        while(serverThread.isAlive()){
        System.out.println(myOutputStream);     //TODO: stop signal has to be send here
        myOutputStream.reset();
        Thread.sleep( 1000 );
        }
/*
       // String[] allWords = myOutputStream.toString().split(" ");
       // long serverTimeStamp = Long.parseLong( allWords[allWords.length-1].replace( ">","" ).trim());

        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            if (clientTimeStamp < serverTimeStamp){
                System.out.println("Client wrote first.");
            }else{
                System.out.println("Server wrote first.");
            }
 */
    }

//========================================== [S E R V E R T H R E A D] =================================================

    public void run() {
        ChatUI server = new ChatUIImpl();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( baos );

        try {
            dos.writeUTF( CMD_OPEN );
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] inputBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream( inputBytes );
        InputStream myInputStream = bais;

        ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();

        try {
            System.out.println("Opening ServerSocket");
            mainThreadReference.interrupt();
            //RaceCondition! -> 2sec sleep of MainThread, running OPEN_CMD here now
            server.runUI( myInputStream, myOutputStream );
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep( ETERNALSLEEP );                       //point 1
        } catch (InterruptedException e) {
            System.out.println("Connection established.");
            myOutputStream.reset();
        }

        while (constantSignal) {

            //Resetting streams, feeding 2nd command to ServerChatUI
            bais.reset();
            baos.reset();
            inputBytes = null;

            try {
                dos.writeUTF( CMD_WRITE + System.nanoTime()  );

                inputBytes = baos.toByteArray();
                bais = new ByteArrayInputStream( inputBytes );
                myInputStream = bais;

                server.runUI( myInputStream, myOutputStream );
                Thread.sleep( 1000);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
