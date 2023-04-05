import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static Queue<Thread> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispatcher;

    public Dispatcher() {

    }

    private static void FCFS(Queue<Thread> readyQueue) throws InterruptedException {
        while(!readyQueue.isEmpty()) {
            //Protect the Ready Queue, so only one dispatcher
            //goes at a time
            RQ.acquire();
            // 1.) Access and loop through the ready queue
            // to find the next Task to run
            // (based on scheduling algorithm)
            for(int i = 0; i < dispatcher.length; i++){
                dispatcher[i].acquire();

                cpuCore.CPU[i].release();
            }

            RQ.release();
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
