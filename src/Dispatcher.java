import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {

    Semaphore dispatcher = new Semaphore(1);

    public Dispatcher(Queue<Thread> readyQueue) {

    }

    private static void FCFS(Queue<Thread> readyQueue) {
        while(!readyQueue.isEmpty()){

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
