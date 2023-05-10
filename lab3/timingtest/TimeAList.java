package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<Integer>(), opCounts = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        for (int n = 1000; n <= 4096000; n <<= 1) {
            Ns.addLast(n);
            opCounts.addLast(n);

            Stopwatch sw = new Stopwatch();
            AList<Integer> testList = new AList<Integer>();
            for (int i = 0; i < n; ++i)
                testList.addLast(i);
            times.addLast(sw.elapsedTime());
        }
        printTimingTable(Ns, times, opCounts);

    }
}
