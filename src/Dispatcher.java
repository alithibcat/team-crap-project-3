import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static Queue<Thread> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispatcher;

    public Dispatcher() {

    }

    private static void FCFS(Queue<Thread> readyQueue) {
        while(true) {

        }
    }

    private static void RR() {

    }

    private static void NSJF() {

    }

    private static void PSJF() {

    }

    @Override
    public void run() {

    }
}
