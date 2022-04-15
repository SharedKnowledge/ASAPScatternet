package net.sharksystem.asap.scatternet;

import net.sharksystem.asap.*;
import net.sharksystem.asap.apps.testsupport.SocketFactory;
import net.sharksystem.asap.apps.testsupport.TestASAPConnectionHandler;
import net.sharksystem.asap.apps.testsupport.TestHelper;
import net.sharksystem.streams.StreamPair;
import net.sharksystem.streams.StreamPairImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Just to explain usage of this component / this API
 */
public class UsageTests {
    /**
     * Encounter manager tests - can be used as template for scatter net tests
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void copyFromASAPJavaLib() throws IOException, InterruptedException {
        // finally - a peer will handle an established connection - it will run an ASAP session
        ASAPConnectionHandler aliceASAPConnectionHandler =
                new TestASAPConnectionHandler(); // would be a peer in a real environment

        /* there can be different protocols - encounter manager bring them all together
        This manager is to be called whenever a new scatter net connection was established
         */
        ASAPEncounterManagerImpl aliceASAPEncounterManager = new ASAPEncounterManagerImpl(aliceASAPConnectionHandler);

        // same on Bob side - we need more test with far more than two peers
        ASAPConnectionHandler bobASAPConnectionHandler =
                new TestASAPConnectionHandler(); // would be a peer

        ASAPEncounterManagerImpl bobASAPEncounterManager = new ASAPEncounterManagerImpl(bobASAPConnectionHandler);

        /////////////////// connection establishment
        // Bob connects with Alice
        int alicePortNumber = TestHelper.getPortNumber();
        ServerSocket aliceSrvSocket = new ServerSocket(alicePortNumber);
        SocketFactory aliceSocketFactory = new SocketFactory(aliceSrvSocket);
        new Thread(aliceSocketFactory).start();
        Thread.sleep(42);

        Socket bob2Alice = new Socket("localhost", alicePortNumber);
        // connected
        String b2aRemoteAddress = ASAPEncounterHelper.getRemoteAddress(bob2Alice);

        // create stream pair - encounter manager only accept stream pairs
        StreamPair bob2AliceStreamPair = StreamPairImpl.getStreamPairWithEndpointAddress(
                bob2Alice.getInputStream(),
                bob2Alice.getOutputStream(),
                b2aRemoteAddress);


        String a2bRemoteAddress = aliceSocketFactory.getRemoteAddress();
        StreamPair alice2BobStreamPair = StreamPairImpl.getStreamPairWithEndpointAddress(
                aliceSocketFactory.getInputStream(),
                aliceSocketFactory.getOutputStream(),
                a2bRemoteAddress);

        // tell encounter manager
        aliceASAPEncounterManager.handleEncounter(alice2BobStreamPair, EncounterConnectionType.INTERNET);
        bobASAPEncounterManager.handleEncounter(bob2AliceStreamPair, EncounterConnectionType.INTERNET);
    }

    private int portnumber;
    private SocketFactory socketFactory;
    private Socket socket;

    private void createNewSocketAndFactory() throws IOException, InterruptedException {
        this.portnumber = TestHelper.getPortNumber();
        this.socketFactory = new SocketFactory(new ServerSocket(this.portnumber));
        new Thread(this.socketFactory).start();
        Thread.sleep(42);
        this.socket = new Socket("localhost", this.portnumber);
    }

    @Test
    public void usage1() throws IOException, InterruptedException, ASAPException {

        /* Assumptions:
        - Each peer can handle up to three (minimal number) connections to other peers.
        - One final connection must be free all the time to allow connection establishment attempts from other peers.
        - ScatternetCreator must decide -
        a) accept a new connection - because enough free slots
        b) refuse new connection
        c) accept new connection *and* close another one
         */

        int maxOpenConnections = 2; // last one must kept open

        // Alice
        ASAPConnectionHandler aliceASAPConnectionHandler =
                new TestASAPConnectionHandler(); // would be a peer in a real environment
        ASAPEncounterManagerImpl aliceASAPEncounterManager = new ASAPEncounterManagerImpl(aliceASAPConnectionHandler);
        ASAPScatternetCreator aliceAsapScatternetCreator =
                new ASAPScatternetImpl(maxOpenConnections, aliceASAPEncounterManager);

        // Bob
        ASAPConnectionHandler bobASAPConnectionHandler =
                new TestASAPConnectionHandler(); // would be a peer in a real environment
        ASAPEncounterManagerImpl bobASAPEncounterManager = new ASAPEncounterManagerImpl(bobASAPConnectionHandler);
        ASAPScatternetCreator bobAsapScatternetCreator =
                new ASAPScatternetImpl(maxOpenConnections, bobASAPEncounterManager);

        /////////////////// Alice and Bob connect
        this.createNewSocketAndFactory();

        // connection established now - tell scatternet creator - this must do the magic
        aliceAsapScatternetCreator.handleConnection(
                this.socketFactory.getInputStream(), this.socketFactory.getOutputStream());

        bobAsapScatternetCreator.handleConnection(this.socket.getInputStream(), socket.getOutputStream());

        // something must be tested here - maybe with even with encounter manager.

        // peer Clara enters the scene
        ASAPConnectionHandler claraASAPConnectionHandler =
                new TestASAPConnectionHandler(); // would be a peer in a real environment
        ASAPEncounterManagerImpl claraASAPEncounterManager = new ASAPEncounterManagerImpl(claraASAPConnectionHandler);
        ASAPScatternetCreator claraAsapScatternetCreator =
                new ASAPScatternetImpl(maxOpenConnections, claraASAPEncounterManager);

        /////////////////// Alice and Clara connect
        this.createNewSocketAndFactory();
        // connection established now - tell scatternet creators - this must do the magic
        aliceAsapScatternetCreator.handleConnection(socketFactory.getInputStream(), socketFactory.getOutputStream());
        claraAsapScatternetCreator.handleConnection(socket.getInputStream(), socket.getOutputStream());

        // something must be tested here - maybe with even with encounter manager.

        /////////////////// Bob and Clara connect
        this.createNewSocketAndFactory();
        // connection established now - tell scatternet creators - this must do the magic
        bobAsapScatternetCreator.handleConnection(socketFactory.getInputStream(), socketFactory.getOutputStream());
        claraAsapScatternetCreator.handleConnection(socket.getInputStream(), socket.getOutputStream());

        // something must be tested here - maybe with even with encounter manager.

        // peer David enters the scene
        ASAPConnectionHandler davidASAPConnectionHandler =
                new TestASAPConnectionHandler(); // would be a peer in a real environment
        ASAPEncounterManagerImpl davidASAPEncounterManager = new ASAPEncounterManagerImpl(davidASAPConnectionHandler);
        ASAPScatternetCreator davidAsapScatternetCreator =
                new ASAPScatternetImpl(maxOpenConnections, davidASAPEncounterManager);

        /////////////////// Alice and David connect
        this.createNewSocketAndFactory();
        // connection established now - tell scatternet creator - this must do the magic
        aliceAsapScatternetCreator.handleConnection(socketFactory.getInputStream(),  socketFactory.getOutputStream());
        davidAsapScatternetCreator.handleConnection(socket.getInputStream(),  socket.getOutputStream());

        // something must be tested here - maybe with even with encounter manager.
        // Alice should close a connection to Bob or Clara
        Assertions.fail("nothing tested yet");
    }

    @Test
    public void testASAPProtocol() {
        Assertions.fail("TODO: are ASAP packages routed within that scatter net?");
    }
}
