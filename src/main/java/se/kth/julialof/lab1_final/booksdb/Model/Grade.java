package se.kth.julialof.lab1_final.booksdb.Model;

public class Grade {
    private int value; // Variabel för att lagra betyget
    public Grade(int value) {

        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Betyget måste vara mellan 1 och 5.");
        }
        this.value = value;
    }
    public Grade() {
        this.value = 0;
    }

    // Getter-metod för att hämta betyget
    public int getValue() {
        return value;
    }

    // Setter-metod för att ändra betyget
    public void setValue(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Betyget måste vara mellan 1 och 5.");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return "Betyg: " + value;

    }

}
