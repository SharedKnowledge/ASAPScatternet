package net.sharksystem.asap.scatternet.scatternet.lib;

/**
 * These constants represent all possible states a peer might have.
 * A state transition should be the only moment where a peer's state change should be triggered
 * meaning that those states ARE NOT to be set changed without being part of an actual context in which
 * the peer's state truly transitioned.
 * This is so that tests can rely on that a peer's state actually reflects this peer's current role in the network.
 */
public enum PeerStates {
    /**
     * A PEER IN THE IDLE STATE. POSSIBLE TRANSITIONS: {IDLE, SEARCHING}
     */
    IDLE,
    /**
     * A PEER IN THE SEARCHING STATE. POSSIBLE TRANSITIONS: {CONSIDERING, CONNECTED}
     */
    SEARCHING,
    /**
     * A PEER IN THE CONSIDERING STATE. POSSIBLE TRANSITIONS: {SEARCHING, CONNECTING}
     */
    CONSIDERING,
    /**
     * A PEER IN THE CONNECTING STATE. POSSIBLE TRANSITIONS: {SEARCHING, CONNECTED}
     */
    CONNECTING,
    /**
     * A PEER IN THE CONNECTED STATE. POSSIBLE TRANSITIONS: {IDLE}
     */
    CONNECTED,
}
