package LotManager;
import java.util.*;

public class PlateGenerator {
    private final Set<String> usedPlates;
    private final Random random;

    public PlateGenerator() {
        this.usedPlates = new HashSet<>();
        this.random = new Random();
    }

    public PlateGenerator(Set<String> existingPlates) {
        this.usedPlates = new HashSet<>(existingPlates);
        this.random = new Random();
    }

    public void loadExistingPlates(Set<String> existing) {
        usedPlates.addAll(existing);
    }

    public String generateUniquePlate() {
        String plate;
        do {
            plate = randomLetters(3) + "-" + String.format("%03d", random.nextInt(1000));
        } while (usedPlates.contains(plate));

        usedPlates.add(plate);
        return plate;
    }

    public Set<String> getUsedPlates() {
        return new HashSet<>(usedPlates);
    }

    private String randomLetters(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            sb.append(letter);
        }
        return sb.toString();
    }
}