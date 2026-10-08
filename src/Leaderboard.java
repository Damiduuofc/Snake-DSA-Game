import java.util.ArrayList;
import java.util.List;

/**
 * Manages game high scores, Bubble Sort sorting, and Linear Search lookups.
 */
public class Leaderboard {

    private List<LeaderboardEntry> entries;

    public Leaderboard() {
        entries = new ArrayList<>();
        loadInitialData();
    }

    /**
     * Loads the initial sample dataset.
     */
    private void loadInitialData() {
        entries.add(new LeaderboardEntry("Damidu", 850));
        entries.add(new LeaderboardEntry("Kasun", 400));
        entries.add(new LeaderboardEntry("Amal", 950));
        entries.add(new LeaderboardEntry("Nimal", 650));
    }

    /**
     * Demonstrates the Bubble Sort sorting process in terminal.
     */
    public void demonstrateBubbleSort() {
        System.out.println("===================================================");
        System.out.println(" Leaderboard (Before Bubble Sort)");
        System.out.println("===================================================");
        for (LeaderboardEntry entry : entries) {
            System.out.println(entry);
        }

        // Apply Bubble Sort
        BubbleSort.sort(entries);

        System.out.println("\nAfter Bubble Sort (Descending - Highest First):");
        System.out.println("===================================================");
        System.out.println(" Leaderboard");
        System.out.println("===================================================");
        for (LeaderboardEntry entry : entries) {
            System.out.println(entry);
        }
        System.out.println("===================================================\n");
    }

    /**
     * Demonstrates Linear Search in terminal.
     */
    public void demonstrateLinearSearch() {
        System.out.println("===================================================");
        System.out.println(" Linear Search Demonstration");
        System.out.println("===================================================");
        System.out.println("Search Query: 'Damidu'");

        LeaderboardEntry result = LinearSearch.searchByName(entries, "Damidu");

        System.out.println("\nResult:");
        if (result != null) {
            System.out.println("Player: " + result.getName());
            System.out.println("Score:  " + result.getScore());
        } else {
            System.out.println("Player not found.");
        }
        System.out.println("===================================================\n");
    }

    /**
     * Searches for a player using Linear Search DSA.
     */
    public LeaderboardEntry searchPlayer(String name) {
        return LinearSearch.searchByName(entries, name);
    }

    /**
     * Records a score for a player. If player exists and score is higher, updates it.
     * If player does not exist, adds the new player and score.
     * Re-sorts the entire leaderboard using Bubble Sort DSA.
     */
    public void recordScore(String name, int score) {
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }
        
        boolean found = false;
        for (LeaderboardEntry entry : entries) {
            if (entry.getName().equalsIgnoreCase(name.trim())) {
                found = true;
                if (score > entry.getScore()) {
                    entry.setScore(score);
                    System.out.println(">> Leaderboard: Updated " + name + "'s high score to " + score);
                }
                break;
            }
        }

        if (!found) {
            entries.add(new LeaderboardEntry(name.trim(), score));
            System.out.println(">> Leaderboard: Added new player " + name + " with score " + score);
        }

        // Sort via Bubble Sort DSA
        BubbleSort.sort(entries);
    }

    /**
     * Adds a new score and re-sorts the leaderboard using Bubble Sort.
     */
    public void addScore(String name, int score) {
        recordScore(name, score);
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }
}
