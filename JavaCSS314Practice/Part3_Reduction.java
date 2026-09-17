import java.util.Random;

public class Part3_Reduction {

    static final long TOTAL_POINTS = 100_000_000L;

    static class Worker extends Thread {
        private final long points;
        private long localHits = 0;

        Worker(long points) {
            this.points = points;
        }

        @Override
        public void run() {
            Random random = new Random();

            for (long i = 0; i < points; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();

                if (x * x + y * y <= 1.0) {
                    localHits++;
                }
            }
        }

        public long getLocalHits() {
            return localHits;
        }
    }

    static class Result {
        long hits;
        double timeMs;

        Result(long hits, double timeMs) {
            this.hits = hits;
            this.timeMs = timeMs;
        }
    }

    static Result runBenchmark(int threadCount) throws InterruptedException {

        Worker[] workers = new Worker[threadCount];

        long pointsPerThread = TOTAL_POINTS / threadCount;
        long remainingPoints = TOTAL_POINTS % threadCount;

        long start = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            long points = pointsPerThread;

            if (i == threadCount - 1) {
                points += remainingPoints;
            }

            workers[i] = new Worker(points);
            workers[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            workers[i].join();
        }

        long totalHits = 0;

        for (int i = 0; i < threadCount; i++) {
            totalHits += workers[i].getLocalHits();
        }

        long end = System.nanoTime();

        double timeMs = (end - start) / 1_000_000.0;

        return new Result(totalHits, timeMs);
    }

    public static void main(String[] args) throws InterruptedException {

        int[] threadCounts = {1, 2, 4, 8, 16, 32};

        System.out.println("Part 3 - OpenMP-Style Reduction");
        System.out.println("--------------------------------");
        System.out.println("Total points: " + TOTAL_POINTS);
        System.out.println();

        System.out.println(
                "Threads | Runtime (ms) | Pi        | Speedup | Efficiency"
        );

        System.out.println(
                "----------------------------------------------------------"
        );

        double baselineTime = 0;

        for (int threadCount : threadCounts) {

            Result result = runBenchmark(threadCount);

            double pi = 4.0 * result.hits / TOTAL_POINTS;

            if (threadCount == 1) {
                baselineTime = result.timeMs;
            }

            double speedup = baselineTime / result.timeMs;
            double efficiency = (speedup / threadCount) * 100.0;

            System.out.printf(
                    "%7d | %12.2f | %.6f | %7.2fx | %9.2f%%%n",
                    threadCount,
                    result.timeMs,
                    pi,
                    speedup,
                    efficiency
            );
        }
    }
}