package ar.com.accn.adventure.model;


public enum Complexity {
    LOW(2),
    MEDIUM(3),
    HIGH(5);

    private final int numberOfChoices;

    Complexity(int numberOfChoices) {
        this.numberOfChoices = numberOfChoices;
    }

    /**
     * Returns the number of choices presented at each decision point.
     */
    public int getNumberOfChoices() {
        return numberOfChoices;
    }
}