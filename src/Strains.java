/**
 * Missions 1 and 2. Compare two strains with dynamic programming.
 *
 * Both are the same kind of table: d[i][j] answers a question about the first i letters of a and the first j letters of b.
 * Think about three things before you type: what d[i][j] means, how it follows from smaller cells, and what the first row and column are.
 */
public class Strains {

    /**
     * Mission 1. The smallest number of single-letter changes that turn a into b. One change is: replace a letter,
     * insert a letter, or delete a letter. This number is the "Mutations" line.
     * Example: "ACGT" -> "AGT" is 1 (delete C). "GATTACA" -> "GCATGCA" needs 3.
     */
    public static int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        // d[i][j] = the fewest changes that turn the first i letters of a into the first j letters of b
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;   // delete all i letters
        for (int j = 0; j <= m; j++) d[0][j] = j;   // insert all j letters
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int replace = d[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                int delete = d[i - 1][j] + 1;        // drop a[i-1]
                int insert = d[i][j - 1] + 1;        // add b[j-1]
                d[i][j] = Math.min(replace, Math.min(delete, insert));
            }
        }
        return d[n][m];
    }

    /**
     * Mission 2. The length of the longest common subsequence: letters that appear in both strains in the same order,
     * but not necessarily next to each other. This is the "Shared core" line.
     * Example: "ACGTT" and "AGTC": the longest is "AGT", length 3.
     */
    public static int lcsLength(String a, String b) {
        int n = a.length(), m = b.length();
        // L[i][j] = the length of the longest common subsequence of the first i letters of a and the first j letters of b.
        // Row 0 and column 0 stay 0: an empty prefix has nothing in common with anything.
        int[][] L = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) L[i][j] = L[i - 1][j - 1] + 1;   // both last letters go into the core
                else L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);                      // drop the last letter of a or of b
            }
        }
        return L[n][m];
    }
}
