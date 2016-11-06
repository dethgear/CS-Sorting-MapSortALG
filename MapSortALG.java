import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class MapSortALG {
    
    private static PrintWriter out;
    private static Random rand;
    private static long seed;
   
    static {
        seed = System.currentTimeMillis();
        rand = new Random(seed);
        try {
            out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
    }
    
    // Returns lg(x), ie, base 2 logarithm of x.
    private static double lg(double x) {
        return Math.log(x) / Math.log(2.0)+1e-10;
    }

    // Returns the Shannon entropy of the items in a[].
    private static double entropy(Object[] a) {
        int N = a.length;  
        if (N == 0 || N == 1) { return 1; }
        //Object[] count = new Object[N];
        double sigma = 0.0;
        Map<Object, Integer> count = 
            new HashMap<Object, Integer>();
        for (int i = 0; i < N; i++) {
            //Hash Map
            if (count.containsKey(a[i])) {
                count.put(a[i], count.get(a[i]) + 1);
            }
            else {
                count.put(a[i], 1);
            }
            /* //Binary Search
            int search = Arrays.binarySearch(a, a[i]);
            if (search < 0) { continue; }
            else {
               delta[(int) a[i]]++; 
            }
            *  //Amortized Iterating search
            int alpha = a[i];
            if (delta[alpha] != 0) { continue; }
            else { delta[alpha] = 1; }
            for (int j = i+1; j < N; j++) {
                int beta = (int) a[j];
                if (alpha == beta) { delta[alpha]++; }
            }*/

        }
        //Sigma Calculator for HashMap
        for (Map.Entry<Object, Integer> i : count.entrySet()) {
            double P = i.getValue() * Math.pow(N, -1);
            sigma += P * lg(P);
        }
        /* //Sigma calculator for index
        for (Object i : count) {
            if (i == 0) { continue; }
            double P = i / (double) N;
            double prob = lg(P) * P;
            sigma += prob;
        }
        */
        if (sigma == 0.0) { return 0; }
        double H = -(1 / lg(N)) * sigma;
        double time = 0.0;
        for (int i = 0; i <= 100; i++) {
            double start = System.currentTimeMillis();
            sort(count, N);
            time += System.currentTimeMillis() - start;
        }
        time /= 100;
        out.println("Dictionary Probability Sort Time: " + time + "ms");
        return H;
    }
    
    private static void sort(Map<Object, Integer> count, int N) {
        double avgTime = 0.0;
        Object[] a = new Object[N];
        int j = 0;
        for (Map.Entry<Object, Integer> i : count.entrySet()) {
            Object key = i.getKey();
            int amount = i.getValue();
            while (amount-- != 0) {
                a[j++] = key;
            }
        }
    }
    
    private static double Radix(Object[] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = (int)a[i];
        }
        double time = 0.0;
        for (int i = 0; i <= 100; i++) {
            shuffle(b);
            double start = System.currentTimeMillis();
            LSDsort(b);
            time += System.currentTimeMillis() - start;
        }
        time /= 100;
        return time;
    }
    
    private static void LSDsort(int[] a) {
        int BITS = 32;                 // each int is 32 bits 
        int W = BITS / 8;  // each int is 4 bytes
        int R = 1 << 8;    // each bytes is between 0 and 255
        int MASK = R - 1;              // 0xFF

        int N = a.length;
        int[] aux = new int[N];

        for (int d = 0; d < W; d++) {         

            // compute frequency counts
            int[] count = new int[R+1];
            for (int i = 0; i < N; i++) {           
                int c = ((int)a[i] >> 8*d) & MASK;
                count[c + 1]++;
            }

            // compute cumulates
            for (int r = 0; r < R; r++)
                count[r+1] += count[r];

            // for most significant byte, 0x80-0xFF comes before 0x00-0x7F
            if (d == W-1) {
                int shift1 = count[R] - count[R/2];
                int shift2 = count[R/2];
                for (int r = 0; r < R/2; r++)
                    count[r] += shift1;
                for (int r = R/2; r < R; r++)
                    count[r] -= shift2;
            }

            // move data
            for (int i = 0; i < N; i++) {
                int c = (a[i] >> 8*d) & MASK;
                aux[count[c]++] = a[i];
            }

            // copy back
            for (int i = 0; i < N; i++)
                a[i] = aux[i];
        }
    }

    public static void shuffle(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + rand.nextInt(n-i);     // between i and n-1
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }
    
    // Test client that compares Quick and Quick3way sorts on inputs 
    // with different values of Shannon entropy. [DO NOT EDIT]
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        Integer[] a = new Integer[N];
        for (int i = 0; i < a.length; i++) {
            a[i] = i % x;
        }
        double entropy = entropy(a);
        out.println("H = " + entropy);
        out.println("Radix speed: " + Radix(a) + "ms");
        a = new Integer[N];
        for (int i = 0; i < a.length; i++) {
            a[i] = i % y;
        }
        entropy = entropy(a);
        out.println("H = " + entropy);
        out.println("Radix speed: " + Radix(a) + "ms");
    }
}
