import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];
        if (n == 0) return tested;
        int[] parent = new int[n];
        int[] order = rootAtZero(n, nb, parent);

        // in[v]  = cheapest cost of the subtree of v when v IS tested
        // out[v] = cheapest cost of the subtree of v when v is NOT tested
        long[] in = new long[n], out = new long[n];
        for (int t = n - 1; t >= 0; t--) {           // children before parents: from the leaves up to the root
            int v = order[t];
            in[v] = cost[v];
            out[v] = 0;
            for (int c : nb[v]) {
                if (c == parent[v]) continue;
                in[v] += Math.min(in[c], out[c]);    // v covers the link v-c, so c is free to choose
                out[v] += in[c];                     // v is not tested, so c must be tested to cover the link v-c
            }
        }

        // rebuild who is tested: walk down from the root with the same rule
        tested[0] = in[0] <= out[0];
        for (int t = 1; t < n; t++) {                // parents before children
            int v = order[t];
            tested[v] = !tested[parent[v]] || in[v] <= out[v];
        }
        return tested;
    }

    /** Roots the tree in person 0. Fills parent[] (parent[0] = -1) and returns the people in BFS order: every parent comes before its children. */
    static int[] rootAtZero(int n, int[][] nb, int[] parent) {
        int[] order = new int[n];
        boolean[] seen = new boolean[n];
        int head = 0, tail = 0;
        order[tail++] = 0;
        seen[0] = true;
        parent[0] = -1;
        while (head < tail) {
            int v = order[head++];
            for (int c : nb[v]) {
                if (seen[c]) continue;               // the way back to the parent
                seen[c] = true;
                parent[c] = v;
                order[tail++] = c;
            }
        }
        return order;
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) return 0;
        int[] parent = new int[n];
        int[] order = rootAtZero(n, nb, parent);

        int[] down = new int[n];                     // down[v] = links on the longest way from v down into its subtree
        int best = 0;
        for (int t = n - 1; t >= 0; t--) {
            int v = order[t];
            int first = 0, second = 0;               // the two longest ways down through different children
            for (int c : nb[v]) {
                if (c == parent[v]) continue;
                int way = down[c] + 1;
                if (way > first) { second = first; first = way; }
                else if (way > second) second = way;
            }
            down[v] = first;
            best = Math.max(best, first + second);   // the best chain that turns at v
        }
        return best;
    }
}
