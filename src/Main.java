import java.util.Queue;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {

        //thread creation and population of RQ by Collin
        int T = (int) (Math.random() * (25 - 1) + 1); // Number Task Threads
        Queue<Thread> readyQueue = new LinkedList<>();
        for(int i = 0; i < T; i++){
            int B =  (int) (Math.random() * (50 - 1) + 1); // Burst Time
            Task task = new Task(i,B);
            Thread thread = new Thread(task);
            readyQueue.add(thread);
        }
    }
}