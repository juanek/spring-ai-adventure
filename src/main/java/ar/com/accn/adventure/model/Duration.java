package ar.com.accn.adventure.model;


public enum Duration {
    SHORT(1),
    MEDIUM(10),
    LONG(20);

    private final int numberOfDecisions;

    Duration(int numberOfDecisions) {
        this.numberOfDecisions = numberOfDecisions;
    }

    /**
     * Returns the number of decision points associated with this
     * duration.
     */
    public int getNumberOfDecisions() {
        return numberOfDecisions;
    }
}