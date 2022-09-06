package net.sharksystem.asap.scatternet.scatternet.utils;

/**
 *
 */
public interface TestConstants {
    /**
     * A SMALL SCATTERNET'S CONNECTION'S DURATION -- The time a connection is held
     */
    long SMALL_SCATTERNET_HOLDING_PERIOD = 30;
    /**
     * A SMALL SCATTERNET'S TIME OUT -- The time of a connections attempt
     */
    long SMALL_SCATTERNET_TIME_OUT = 10;

    /**
     * A SMALLS SCATTERNET'S AMOUNT OF ENCOUNTERS -- The amount of encounters who will be remembered
     * before a new iteration starts and the counter(not this one) and list of known peers will be reset
     */
    int SMALL_SCATTERNET_MAX_COUNTER = 2;
}
