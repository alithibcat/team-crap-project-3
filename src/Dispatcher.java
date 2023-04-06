import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static ArrayList<Task> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispatcher;

    public Dispatcher() {

    }

    private static void FCFS(ArrayList<Task> readyQueue) throws InterruptedException {
        while(!readyQueue.isEmpty()) {
            //Protect the Ready Queue, so only one dispatcher
            //goes at a time
            RQ.acquire();
            // 1.) Access and loop through the ready queue
            // to find the next Task to run
            // (based on scheduling algorithm)
            for(int i = 0; i < dispatcher.length; i++){
                dispatcher[i].acquire();


                //2) Decide the allotted burst for this Task and
                // let the respective CPU Core thread know what
                // that burst time is
                readyQueue.get(i).getTaskID(); //grab the task ID
                readyQueue.get(i).getMaxBurst(); // grab the task Burst time


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
