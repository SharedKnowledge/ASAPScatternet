package net.sharksystem.asap.scatternet.scatternet.lib;


import net.sharksystem.asap.ASAPEncounterManager;
import net.sharksystem.asap.protocol.ASAPConnection;
import net.sharksystem.asap.scatternet.scatternet.exceptions.ConnectionAttemptFailed;
import net.sharksystem.asap.scatternet.scatternet.exceptions.SearchInterruptedByConnection;

import java.util.Set;
import java.util.concurrent.Callable;


/**
 * Each SWITCH method in this interface has one and only responsibility which is to handle everything needed to
 * switch the peer to one state and given the conditions let the peer stay in the same state.
 * It does not concern itself with the peer's next or previous state, we take the black box approach.
 * Each method should leave the peer in a state where it can still function, so that the subsequent
 * method handling it can do its work.
 * Before returning, the peer must be put manually in its appropriate state(see ENUM PeerState).
 * NO METHOD is responsible for switching to more than one state.
 * Do one thing and do it right.
 */

public interface PeerCoordinator extends ASAPEncounterManager, ASAPConnection {

    /**
     * Sets up the peer to switch to the IDLE state and puts it manually in the IDLE state.
     * Verifies relation between counter and MAX_COUNTER.
     * Sets up configurations needed in case the peer is just starting up for the first time.
     * @param counter total amount of connections established
     * @param MAX_COUNTER desired amount of connections per iteration
     * @param isStartingUp true if this peers is just awakening, false otherwise
     * @return counter equals MAX_COUNTER
     */
    boolean switchToIdle(int counter, int MAX_COUNTER, boolean isStartingUp);

    /**
     * Sets up the peer to switch to the SEARCHING state and puts it manually in the SEARCHING state.
     * Searches for peers in a different Thread and returns the discovered peer's ID wrapped by a Callable
     * -> asynchronous return.
     * @return callable with discovered peer's ID
     * @throws SearchInterruptedByConnection if another peer interrupts by attempting to connect
     */
    Callable<CharSequence> switchToSearching() throws SearchInterruptedByConnection;

    /**
     * Sets up the peer to switch to the CONSIDERING state and puts it manually in the CONSIDERING state.
     * Verifies if the peerID is already known i.e. a connection to this peer has already been established.
     * @param peerID the ID of the peer being considered for a connection
     * @return true if the peer is a not known peer, otherwise false
     */
    boolean switchToConsidering(CharSequence peerID);

    /**
     * Sets up the peer to switch to the CONNECTING state and puts it manually in the CONNECTING state.
     * Seeks connection to peer holding peerID. Attempt does not last longer than TIME_OUT.
     * The connection holds as long as HOLDING_PERIOD determines.
     * @throws ConnectionAttemptFailed if it does not manage to connect after TIME_OUT or other connection related
     * issues arise.
     */
    void switchToConnecting(final long TIME_OUT, CharSequence peerID) throws ConnectionAttemptFailed;

    /**
     * Sets up the peer to switch to the CONNECTED state and puts it manually in the CONNECTED state.
     * Increments the counter by one and adds peerID to knownPeers
     * @param HOLDING_PERIOD duration of connection
     * @param peerID ID of connected peer
     * @param knownPeers list of known peers
     */
    void switchToConnected(final long HOLDING_PERIOD, CharSequence peerID, Set<CharSequence> knownPeers);


    ////////////////////////////////////////// HELPERS ////////////////////////////////////////

    /**
     * @return all known peers
     */
    Set<CharSequence> getKnownPeers();

    /**
     * @param state given state
     * @return if peer is currently in the given state
     */
    boolean isInState(PeerStates state);


    /**
     * @return Peer's ID
     */
    CharSequence getPeerId();


    /**
     * @return Peer's current counter of past connections
     */
    int getCounter();


}
