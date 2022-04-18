package net.sharksystem.asap.scatternet;

import net.sharksystem.asap.ASAPEncounterManager;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPPeer;
import net.sharksystem.asap.EncounterConnectionType;
import net.sharksystem.asap.protocol.ASAPConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

class ASAPScatternetCreatorImpl implements ASAPScatternetCreator {
    private final int maxConnectionsNumber;
    private final ASAPEncounterManager asapEncounterManager;
    private final ASAPPeer peer;

    public ASAPScatternetCreatorImpl(int maxConnectionsNumber, ASAPEncounterManager asapEncounterManager, ASAPPeer peer) {
        this.maxConnectionsNumber = maxConnectionsNumber;
        this.asapEncounterManager = asapEncounterManager;
        this.peer = peer;
    }

    ASAPScatternetCreatorImpl(int maxConnectionsNumber, ASAPEncounterManager asapEncounterManager) {
        this(maxConnectionsNumber, asapEncounterManager, null); // for tests only
    }

    @Override
    public ASAPConnection handleConnection(InputStream inputStream, OutputStream outputStream, boolean b, boolean b1, Set<CharSequence> set, Set<CharSequence> set1) throws IOException, ASAPException {
        // TODO
        return null;
    }

    @Override
    public ASAPConnection handleConnection(InputStream inputStream, OutputStream outputStream, boolean b, boolean b1, EncounterConnectionType encounterConnectionType, Set<CharSequence> set, Set<CharSequence> set1) throws IOException, ASAPException {
        // TODO: implement this - other methods are just simpler variants.
        // http://sharksystem.net/asap/javadoc/net/sharksystem/asap/ASAPConnectionHandler.html

        // do some magic - e.g. a) create a connection and tell encounter manager. b) close another one etc.
        return null;
    }

    @Override
    public ASAPConnection handleConnection(InputStream inputStream, OutputStream outputStream) throws IOException, ASAPException {
        // TODO
        return null;
    }

    @Override
    public ASAPConnection handleConnection(InputStream inputStream, OutputStream outputStream, EncounterConnectionType encounterConnectionType) throws IOException, ASAPException {
        // TODO
        return null;
    }
}
