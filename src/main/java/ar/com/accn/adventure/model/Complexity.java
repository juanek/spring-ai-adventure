package ar.com.accn.adventure.model;


import lombok.Getter;

@Getter
public enum Complexity {
    LOW(2),
    MEDIUM(3),
    HIGH(5);

    /**
     * -- GETTER --
     *  Returns the number of choices presented at each decision point.
     */
    private final int numberOfChoices;

    Complexity(int numberOfChoices) {
        this.numberOfChoices = numberOfChoices;
    }

}