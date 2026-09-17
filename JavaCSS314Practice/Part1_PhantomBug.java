import java.util.Random;

public class Part1_PhantomBug {

    static long totalHits = 0;
    static final int THREADS = 4;
    static final long TOTAL_POINTS = 50_000_000L;

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
                    totalHits++;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        totalHits = 0;

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

        double pi = 4.0 * totalHits / TOTAL_POINTS;
        double runtimeMs = (end - start) / 1_000_000.0;

        System.out.println("Part 1 - The Phantom Bug");
        System.out.println("-------------------------");
        System.out.println("Threads: " + THREADS);
        System.out.println("Total points: " + TOTAL_POINTS);
        System.out.println("Total hits: " + totalHits);
        System.out.printf("Pi approximation: %.6f%n", pi);
        System.out.printf("Runtime: %.2f ms%n", runtimeMs);
    }
}