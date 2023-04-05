import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static Queue<Thread> readyQueue;
    static Semaphore[] dispatcher;

    public Dispatcher() {

    }

    private static void FCFS(Queue<Thread> readyQueue) {
        while(!readyQueue.isEmpty()) {

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
