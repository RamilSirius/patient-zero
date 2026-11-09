import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int k = 0;                                   // length of the current border of p[0..i-1]
        for (int i = 1; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) k = pi[k - 1];   // fall back to a shorter border
            if (p.charAt(i) == p.charAt(k)) k++;                         // the border grows by one letter
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        int n = text.length(), m = p.length();
        if (m == 0 || m > n) return new int[0];
        int[] pi = prefixFunction(p);
        List<Integer> hits = new ArrayList<>();
        int k = 0;                                   // how many letters of p are matched right now
        for (int i = 0; i < n; i++) {                // i only moves forward: the text pointer never goes back
            while (k > 0 && text.charAt(i) != p.charAt(k)) k = pi[k - 1];
            if (text.charAt(i) == p.charAt(k)) k++;
            if (k == m) {                            // a full match ends at i
                hits.add(i - m + 1);
                k = pi[m - 1];                       // keep the border: the next match may overlap this one
            }
        }
        int[] out = new int[hits.size()];
        for (int t = 0; t < out.length; t++) out[t] = hits.get(t);
        return out;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || k > a.length() || k > b.length()) return 0;

        // hash of every window of b -> the start positions of the windows with this hash
        long[] hb = windowHashes(b, k);
        Map<Long, List<Integer>> windowsOfB = new HashMap<>();
        for (int j = 0; j < hb.length; j++) windowsOfB.computeIfAbsent(hb[j], h -> new ArrayList<>()).add(j);

        long[] ha = windowHashes(a, k);
        int count = 0;
        for (int i = 0; i < ha.length; i++) {
            List<Integer> candidates = windowsOfB.get(ha[i]);
            if (candidates == null) continue;
            for (int j : candidates) {
                if (sameLetters(a, i, b, j, k)) { count++; break; }   // equal hashes are only a hint: check the letters
            }
        }
        return count;
    }

    static final long BASE = 5;                 // more than the 4 letter codes 1..4
    static final long MOD = 1_000_000_007L;     // a large prime

    static int code(char c) {
        switch (c) {
            case 'A': return 1;
            case 'C': return 2;
            case 'G': return 3;
            case 'T': return 4;
            default:  return c;                 // never happens for A, C, G, T input
        }
    }

    /** h[i] = hash of s[i..i+k-1] as a number in base BASE modulo MOD. Each next window is computed from the previous one in O(1). */
    static long[] windowHashes(String s, int k) {
        int count = s.length() - k + 1;
        long[] h = new long[count];
        long high = 1;                                           // BASE^(k-1) mod MOD: the weight of the letter that leaves
        for (int t = 1; t < k; t++) high = high * BASE % MOD;
        long cur = 0;
        for (int t = 0; t < k; t++) cur = (cur * BASE + code(s.charAt(t))) % MOD;
        h[0] = cur;
        for (int i = 1; i < count; i++) {
            cur = (cur - code(s.charAt(i - 1)) * high % MOD + MOD) % MOD;   // remove the old first letter (+MOD keeps it non-negative)
            cur = (cur * BASE + code(s.charAt(i + k - 1))) % MOD;            // shift and add the new last letter
            h[i] = cur;
        }
        return h;
    }

    static boolean sameLetters(String a, int i, String b, int j, int k) {
        for (int t = 0; t < k; t++) if (a.charAt(i + t) != b.charAt(j + t)) return false;
        return true;
    }
}
