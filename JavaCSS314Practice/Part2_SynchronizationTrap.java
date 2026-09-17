import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Part2_SynchronizationTrap {

    static final long TOTAL_POINTS = 50_000_000L;
    static final int THREADS = 4;

    static AtomicLong totalHits = new AtomicLong(0);

    static class Worker extends Thread {
        private final long points;

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
                    totalHits.incrementAndGet();
                }
            }
        }
    }

    static double runSingleThread() {
        Random random = new Random();
        long hits = 0;

        long start = System.nanoTime();

        for (long i = 0; i < TOTAL_POINTS; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();

            if (x * x + y * y <= 1.0) {
                hits++;
            }
        }

        long end = System.nanoTime();

        double pi = 4.0 * hits / TOTAL_POINTS;
        double timeMs = (end - start) / 1_000_000.0;

        System.out.printf("Single-thread Pi: %.6f%n", pi);
        System.out.printf("Single-thread Runtime: %.2f ms%n", timeMs);

        return timeMs;
    }

    static double runMultiThread() throws InterruptedException {
        totalHits.set(0);

        long pointsPerThread = TOTAL_POINTS / THREADS;
        Thread[] threads = new Thread[THREADS];

        long start = System.nanoTime();

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Worker(pointsPerThread);
            threads[i].start();
        }

        for (int i = 0; i < THREADS; i++) {
            threads[i].join();
        }

        long end = System.nanoTime();

        double pi = 4.0 * totalHits.get() / TOTAL_POINTS;
        double timeMs = (end - start) / 1_000_000.0;

        System.out.printf("Multi-thread Pi: %.6f%n", pi);
        System.out.printf("Multi-thread Runtime: %.2f ms%n", timeMs);

        return timeMs;
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Part 2 - The Synchronization Trap");
        System.out.println("----------------------------------");
        System.out.println("Threads: " + THREADS);
        System.out.println("Total points: " + TOTAL_POINTS);
        System.out.println();

        double singleTime = runSingleThread();

        System.out.println();

        double multiTime = runMultiThread();

        System.out.println();

        System.out.printf("Multi-thread / Single-thread: %.2fx%n",
                multiTime / singleTime);
    }
}