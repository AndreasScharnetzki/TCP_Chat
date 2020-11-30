package tests;

import implementation.ChatUIImpl;
import interfaces.ChatUI;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import static interfaces.ChatUI.MSG_PORT_LOCATOR;
import static interfaces.ChatUI.MSG_SUCCESSFULL_CON;

public class TCPTest implements Runnable {

    //this is to ensure childThread has a reference to mainThread
    private Thread mainThreadReference = Thread.currentThread();
    private Thread serverThread;

    private final long ETERNALSLEEP = Long.MAX_VALUE;
    private final int PORT = 7777;

    private final String CMD_OPEN = "open " + PORT;
    private final String CMD_CONNECT = "con localhost " + PORT;
    private final String CMD_WRITE = ChatUI.CMD_WRITE.concat( " " );
    private final String CMD_EXIT = ChatUI.CMD_EXIT;
    private final String MSG1 = "Hello";
    private final String MSG2 = "world";

    private final String CMD_SIGN = ">";

    @Test
    public void goodTest_TCP01_send() throws Exception {
        System.out.println( "\nT E S T L O G: \n" );

        Thread.currentThread().setName( "ClientThread" );

        int count = Thread.activeCount();
        // 2 Threads [Test, ClientChatUI]
        System.out.println( "\n! Number of currently active threads (using Thread.activeCount()) = " + count + "\n" +
                "Threads: IntegrationTest, ClientChatUI\n" );

        System.out.println( "00.) CLIENT: ! CLIENT_Thread running" +
                "\n             - creating SERVER_Thread now and starting it" );

        serverThread = new Thread( new TCPTest() );
        serverThread.start();

        System.out.println( "01.) CLIENT: ! SERRVER_Thread started" +
                "\n             - going to sleep now (waiting for SERVER_Thread to start)." );
        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            System.out.println( "04.) CLIENT: ! is now awake" +
                    "\n             - feeding CLIENTChatUI with CONNECTION_CMD now" );
        }

        //enough time for CPU-Scheduler to switch back once more to serverThread and let it start the socket-opening,
        // letting it remain in .accept()-state (waiting for incoming connection-req)
        Thread.currentThread().sleep( 2000 );

        ChatUI client = new ChatUIImpl();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( baos );

        try {
            dos.writeUTF( CMD_CONNECT );
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] inputBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream( inputBytes );
        InputStream myInputStream = bais;

        ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();

        client.runUI( myInputStream, myOutputStream );
        System.out.println( "06.) CLIENT: ! CONNECTION_CMD successfully passed to CLIENTChatUI" +
                "\n             - waking up SERVER now " +
                "\n             - about to check first Assert (Connection)" );

        Thread.sleep( 1000 );
        serverThread.interrupt();

        String expected = CMD_SIGN + CMD_SIGN + "Server wrote: " + MSG_SUCCESSFULL_CON + System.lineSeparator();

        Assert.assertEquals( expected, myOutputStream.toString() );
        myOutputStream.reset();
        System.out.println( "08.) CLIENT: *** First Assert surpassed. *** -> connection to SERVER established" +
                "\n             - going to sleep now" );
        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            System.out.println( "10.) CLIENT: ! got woken up by SERVER" +
                    "\n             - trying to send the 1st message now" );
        }

//Resetting streams, feeding 2nd command to ClientChatUI

        bais.reset();
        baos.reset();
        inputBytes = null;
        myOutputStream.reset();

        dos.writeUTF( CMD_WRITE + MSG1 );

        inputBytes = baos.toByteArray();
        bais = new ByteArrayInputStream( inputBytes );
        myInputStream = bais;

        client.runUI( myInputStream, myOutputStream );

        System.out.println( "11.) CLIENT: ! has passed the 1st message successfully to CLIENTChatUI" +
                "\n             - waking up SERVER now" +
                "\n             - going to sleep afterwards" );
        serverThread.interrupt();

        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            System.out.println( "15.) CLIENT: ! is now awake" +
                    "\n             - facing fourth Assert now (2nd message)" );
        }

        Thread.currentThread().sleep( 1000 );

        expected = CMD_SIGN + CMD_SIGN + "Server wrote: " + MSG2 + System.lineSeparator();
        Assert.assertEquals( expected, myOutputStream.toString() );

        System.out.println( "16.) CLIENT: *** Fourth Assert surpassed *** -> 2nd message received" +
                "\n             - waking up CLIENT now" +
                "\n             - going to sleep" );
        serverThread.interrupt();

        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {

            System.out.println( "18.) CLIENT: ! woken up by SERVER " +
                    "\n               ===! END OF TEST REACHED !=== " +
                    "\n\n                (╯°□°）╯︵ ┻━┻" );
        }
    }

//========================================== [S E R V E R T H R E A D] =================================================

    public void run() {
        Thread.currentThread().setName( "ServerThread" );
        ChatUI server = new ChatUIImpl();

        int count = Thread.activeCount();
        System.out.println( "\n! Number of currently active threads (using Thread.activeCount()) = " + count + "\n" +
                "Threads: IntegrationTest, ServerChatUI, ClientChatUI\n" );

        System.out.println( "02.) SERVER: ! SERVER_Thread started running" +
                "\n             - going to initiate SERVERChatUIs streams now" );

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
            System.out.println( "03.) SERVER: ! finished initiating SERVERChatUIs streams" +
                    "\n             - passing OPEN_CMD to SERVERChatUI now (waiting for incoming connection request from CLIENT)" +
                    "\n             - waking up CLIENT now" );

            //RaceCondition! -> handled by 2 sec sleep of MainThread
            mainThreadReference.interrupt();
            server.runUI( myInputStream, myOutputStream );

            System.out.println( "05.) SERVER: ! OPEN_CMD successfully passed in SERVER_Thread" +
                    "\n             - going to sleep now" );
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            System.out.println( "07.) SERVER: ! woken up by CLIENT" +
                    "\n             - facing second Assert now (Connection)" );
        }
        String expected = CMD_SIGN + MSG_PORT_LOCATOR + PORT + System.lineSeparator() + CMD_SIGN;
        Assert.assertEquals( expected, myOutputStream.toString() );
        myOutputStream.reset();

        System.out.println( "09.) SERVER: *** Second Assert surpassed. *** -> connection to CLIENT established." +
                "\n             - waking up CLIENT" +
                "\n             - then going to sleep until 1st Message is send" );
        //here the Clients starts to send message
        mainThreadReference.interrupt();

        try {
            Thread.sleep( ETERNALSLEEP );
        } catch (InterruptedException e){
            System.out.println( "12.) SERVER: ! got woken up by CLIENT. " +
                    "\n             -  facing third Assert now" );
        }

        expected = "client wrote: " + MSG1 + System.lineSeparator();
        Assert.assertEquals( expected, myOutputStream.toString() );

        System.out.println( "13.) SERVER: *** Third Assert surpassed *** -> 1st message received" +
                "\n             - sending 2nd message to CLIENT now" );

        count = Thread.activeCount();
        System.out.println( "\n! Number of currently active threads (using Thread.activeCount()) = " + count + "\n" +
                "Threads: IntegrationTest, ServerChatUI, TCPServer(listening), ClientChatUI, TCPClient(listening)\n" );

//Resetting streams, feeding 2nd command to ServerChatUI
        bais.reset();
        baos.reset();
        inputBytes = null;
        myOutputStream.reset();

        try {
            dos.writeUTF( CMD_WRITE + MSG2 );

            inputBytes = baos.toByteArray();
            bais = new ByteArrayInputStream( inputBytes );
            myInputStream = bais;

            server.runUI( myInputStream, myOutputStream );
        } catch (Exception e) {
            System.err.println( "ERROR whilst trying to send Message from SERVER to CLIENT" + e.getStackTrace() );
        }

        System.out.println( "14.) SERVER: ! 2nd message had been successfully passed to SERVERChatUI" +
                "\n             - waking up CLIENT now" +
                "\n             - SERVER is going to sleep" );

        mainThreadReference.interrupt();

        try {
            Thread.currentThread().sleep( ETERNALSLEEP );
        } catch (InterruptedException e) {
            System.out.println( "17.) SERVER: ! woken up by CLIENT" +
                    "\n               ===! END OF TEST REACHED !===" );
            mainThreadReference.interrupt();
        }
    }

    //======================================================================================================================
    //displays name + status of the thread which is calling this method
    private void id() {
        System.out.println( Thread.currentThread().getName() + ", status: " + Thread.currentThread().getState() );
    }
}


/**
 * Literature
 * <p>
 * https://www.geeksforgeeks.org/main-thread-java/
 * <p>
 * https://www.baeldung.com/java-thread-join
 * <p>
 * https://www.geeksforgeeks.org/difference-between-thread-start-and-thread-run-in-java/
 * <p>
 * https://stackoverflow.com/questions/19988092/does-a-child-thread-in-java-prevent-the-parent-threads-to-terminate
 * <p>
 * <p>
 * https://www.codejava.net/java-core/concurrency/how-to-list-all-threads-currently-running-in-java
 */