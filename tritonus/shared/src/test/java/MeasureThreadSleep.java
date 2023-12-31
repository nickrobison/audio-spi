/*
 * MeasureThreadSleep.java
 */

/* Sun JDK 1.4.2 or later is required to compile and run this program. */


public class MeasureThreadSleep {
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            printUsageAndExit();
        }
        int millis = Integer.parseInt(args[0]);
        int nanos = -1;
        if (args.length == 2) {
            nanos = Integer.parseInt(args[1]);
        }

        for (int i = 0; i < 10; i++) {
            try {
                long start;
                long end;
                if (nanos != -1) {
                    start = System.nanoTime();
                    Thread.sleep(millis, nanos);
                    end = System.nanoTime();
                } else {
                    start = System.nanoTime();
                    Thread.sleep(millis);
                    end = System.nanoTime();
                }
                out("measured duration (ms): " + (end - start));
            } catch (InterruptedException e) {
                // IGNORE
            }
        }
    }


    private static void printUsageAndExit() {
        out("MeasureThreadSleep: usage:");
        out("\tjava MeasureThreadSleep <milliseconds> [<nanoseconds>]");
        System.exit(1);
    }


    private static void out(String strMessage) {
        System.out.println(strMessage);
    }


}


/* MeasureThreadSleep.java */
