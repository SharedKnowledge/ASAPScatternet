package net.sharksystem.asap.scatternet.scatternet;

import net.sharksystem.asap.scatternet.scatternet.exceptions.SearchInterruptedByConnection;
import net.sharksystem.asap.scatternet.scatternet.lib.PeerCoordinator;
import net.sharksystem.asap.scatternet.scatternet.lib.PeerStates;
import net.sharksystem.asap.scatternet.scatternet.utils.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;


public class PeerStateTests {
    private PeerCoordinator peer_A;
    private PeerCoordinator peer_B;

    private CharSequence peer_A_partners_id;
    private CharSequence peer_B_partners_id;

    private Callable<CharSequence> callable_peer_a;
    private Callable<CharSequence> callable_peer_b;

    /**
     * true if peer A was the one that found peer B, false otherwise
     */
    boolean peer_A_is_finder;

    private Set<CharSequence> known_peers_peer_A = new HashSet<>();
    private Set<CharSequence> known_peers_peer_B = new HashSet<>();


    @Test
    @Order(1)
    public void switchToIdleTest() throws Exception {
        Assertions.assertInstanceOf(PeerCoordinator.class, peer_A);
        Assertions.assertInstanceOf(PeerCoordinator.class, peer_B);

        Assertions.assertTrue(peer_A.switchToIdle(0, TestConstants.SMALL_SCATTERNET_MAX_COUNTER, true));
        Assertions.assertTrue(peer_B.switchToIdle(0, TestConstants.SMALL_SCATTERNET_MAX_COUNTER, true));

        Thread.sleep(5); // setup time

        Assertions.assertTrue(peer_A.isInState(PeerStates.IDLE));
        Assertions.assertTrue(peer_B.isInState(PeerStates.IDLE));

        setFinderHelper();
    }


    @Test
    @Order(2)
    public void switchToSearchingTest() throws SearchInterruptedByConnection, InterruptedException {
        callable_peer_a = peer_A.switchToSearching();
        Thread.sleep(5); // little heads off for race condition's sake
        callable_peer_b = peer_A.switchToSearching();

        Thread.sleep(10); // time for one of the peers to find the other

        Assertions.assertTrue(peer_A.isInState(PeerStates.SEARCHING));
        Assertions.assertTrue(peer_B.isInState(PeerStates.SEARCHING));
    }

    @Test
    @Order(3)
    public void switchToConsideringTest() { switchFinderToConsideringHelper(); }

    @Test
    @Order(4)
    public void switchToConnectingTest() throws InterruptedException { switchFinderToConnectingHelper(); }

    @Test
    @Order(5)
    public void switchToConnectedTest() throws InterruptedException { switchFinderToConnectedHelper(); }

    ////////////////////////////////////////////    HELPERS     //////////////////////////////////////////


    /**
     * After the SEARCHING state, we cannot determine which peer found the other one first.
     * In order to solve this issue we remember who found whom first and call this respective peer "finder".
     */
    private void setFinderHelper() throws Exception {
        try { // Peer A found B
            peer_A_partners_id = callable_peer_a.call();
            peer_A_is_finder = true;
        } catch (Exception e) { // Peer B found A
            peer_B_partners_id = callable_peer_b.call();
            peer_A_is_finder = false;
        }
    }

    /**
     * Helps switch the finder to the CONSIDERING state
     */
    private void switchFinderToConsideringHelper() {
        Assertions.assertNotNull(peer_A_is_finder ? peer_A_partners_id : peer_B_partners_id);
        Assertions.assertTrue(peer_A_is_finder ?
                peer_A_partners_id == peer_B.getPeerId() : peer_B_partners_id == peer_A.getPeerId());
        Assertions.assertFalse(peer_A_is_finder ?
                peer_A.getKnownPeers().contains(peer_A_partners_id) :
                peer_B.getKnownPeers().contains(peer_B_partners_id));
        // In this small test we can presume that the time for the "consideration" will be small since there are
        // not so many known peers, so the Assertion can be made immediately after the switch.
        // But in a bigger network a peer would need more time to consider connecting to another peer
        // since it would have to iterate over all its known peers.
        Assertions.assertTrue(peer_A_is_finder ? peer_A.switchToConsidering(peer_A_partners_id, peer_A.getKnownPeers()) :
                peer_B.switchToConsidering(peer_B_partners_id, peer_B.getKnownPeers()));
        Assertions.assertTrue(peer_A_is_finder ? peer_A.isInState(PeerStates.CONSIDERING) :
                peer_B.isInState(PeerStates.CONSIDERING));
    }

    /**
     * Helps switch the finder to the CONNECTING state
     */
    private void switchFinderToConnectingHelper() throws InterruptedException {
        if(peer_A_is_finder) {
            Assertions.assertDoesNotThrow(() -> peer_A.switchToConnecting(
                    TestConstants.SMALL_SCATTERNET_TIME_OUT,
                    peer_A_partners_id));
        } else {
            Assertions.assertDoesNotThrow(() -> peer_B.switchToConnecting(
                    TestConstants.SMALL_SCATTERNET_TIME_OUT,
                    peer_B_partners_id));
        }
        Thread.sleep(TestConstants.SMALL_SCATTERNET_TIME_OUT); // wait for connection attempt
        Assertions.assertTrue(peer_A_is_finder ? peer_A.isInState(PeerStates.CONNECTING) :
                peer_B.isInState(PeerStates.CONNECTING));
    }

    /**
     * Helps switch the finder to the CONNECTED state
     */
    private void switchFinderToConnectedHelper() throws InterruptedException {
        if (peer_A_is_finder) {
            peer_A.switchToConnected(TestConstants.SMALL_SCATTERNET_HOLDING_PERIOD, peer_A_partners_id, known_peers_peer_A);
            Thread.sleep(5);
            Assertions.assertTrue(peer_A.isInState(PeerStates.CONNECTED));
            Assertions.assertEquals(peer_A.getCounter(), 1);
            Assertions.assertTrue(peer_A.getKnownPeers().contains(peer_B.getPeerId()));
        } else {
            peer_B.switchToConnected(TestConstants.SMALL_SCATTERNET_HOLDING_PERIOD, peer_B_partners_id, known_peers_peer_B);
            Thread.sleep(5);
            Assertions.assertTrue(peer_B.isInState(PeerStates.CONNECTED));
            Assertions.assertEquals(peer_B.getCounter(), 1);
            Assertions.assertTrue(peer_B.getKnownPeers().contains(peer_A.getPeerId()));
        }
    }



}