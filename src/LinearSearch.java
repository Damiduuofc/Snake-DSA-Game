import java.util.List;

/**
 * Linear Search Algorithm Implementation.
 * 
 * DSA Component: Searching Algorithm
 * Time Complexity:
 *   - Best Case: O(1) (target is at index 0)
 *   - Average / Worst Case: O(N) (target is at the end or not present)
 * Space Complexity: O(1)
 * 
 * Process:
 *   Player 1 -> Player 2 -> Player 3 -> ... -> Player N
 *   Inspects each element sequentially until target matches.
 */
public class LinearSearch {

    /**
     * Searches for a player by name in a list of LeaderboardEntry items.
     * 
     * @param entries the list of entries to search through
     * @param targetName the name of the player to find
     * @return the matching LeaderboardEntry, or null if not found
     */
    public static LeaderboardEntry searchByName(List<LeaderboardEntry> entries, String targetName) {
        if (entries == null || targetName == null) {
            return null;
        }

        System.out.println("--- Starting Linear Search for: '" + targetName + "' ---");

        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry current = entries.get(i);
            System.out.println("  Step " + (i + 1) + ": Inspecting index " + i + " -> " + current.getName() + " (" + current.getScore() + ")");

            if (current.getName().equalsIgnoreCase(targetName.trim())) {
                System.out.println("  ==> MATCH FOUND at index " + i + "! [Time: O(" + (i + 1) + ") steps]");
                return current;
            }
        }

        System.out.println("  ==> Player '" + targetName + "' NOT FOUND after checking all " + entries.size() + " entries.");
        return null;
    }

    /**
     * Searches for a target value in a primitive integer array.
     * 
     * @param array array of integers
     * @param target integer value to search for
     * @return index of target if found, or -1 if not found
     */
    public static int search(int[] array, int target) {
        if (array == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
