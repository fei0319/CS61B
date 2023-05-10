package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<Integer>(), opCounts = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        for (int n = 1000, m = 10000; n <= 128000; n <<= 1) {
            Ns.addLast(n);
            opCounts.addLast(m);

            SLList<Integer> testList = new SLList<Integer>();
            for (int i = 0; i < n; ++i)
                testList.addLast(i);

            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < m; ++i)
                testList.addLast(i);
            times.addLast(sw.elapsedTime());
        }
        printTimingTable(Ns, times, opCounts);
    }

}
