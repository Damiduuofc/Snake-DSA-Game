import java.util.List;

/**
 * Bubble Sort Algorithm Implementation.
 * 
 * DSA Component: Sorting Algorithm
 * Time Complexity:
 *   - Best Case: O(N) with early exit optimization
 *   - Average / Worst Case: O(N^2)
 * Space Complexity: O(1) in-place
 * Order: Descending (highest score to lowest score)
 */
public class BubbleSort {

    /**
     * Sorts an integer array of scores in descending order.
     * 
     * @param scores array of scores to sort in-place
     */
    public static void sort(int[] scores) {
        if (scores == null || scores.length <= 1) {
            return;
        }

        for (int i = 0; i < scores.length - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < scores.length - i - 1; j++) {
                // Descending comparison: if left element is less than right, swap
                if (scores[j] < scores[j + 1]) {
                    int temp = scores[j];
                    scores[j] = scores[j + 1];
                    scores[j + 1] = temp;
                    swapped = true;
                }
            }

            // Early exit optimization if array is already sorted
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Sorts an array of LeaderboardEntry objects in descending order of score.
     * 
     * @param entries array of LeaderboardEntry items to sort in-place
     */
    public static void sort(LeaderboardEntry[] entries) {
        if (entries == null || entries.length <= 1) {
            return;
        }

        for (int i = 0; i < entries.length - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < entries.length - i - 1; j++) {
                if (entries[j].getScore() < entries[j + 1].getScore()) {
                    LeaderboardEntry temp = entries[j];
                    entries[j] = entries[j + 1];
                    entries[j + 1] = temp;
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Sorts a List of LeaderboardEntry objects using Bubble Sort.
     */
    public static void sort(List<LeaderboardEntry> list) {
        if (list == null || list.size() <= 1) {
            return;
        }

        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).getScore() < list.get(j + 1).getScore()) {
                    LeaderboardEntry temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
