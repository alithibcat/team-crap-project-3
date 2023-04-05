import java.util.Queue;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {

        //thread creation and population of RQ by Collin
        int t = (int) (Math.random() * (25 - 1) + 1);
        Queue<Task> readyQueue = new LinkedList<>();
        for(int i = 0; i < t; i++){
            Task thread = new Task(i);
            readyQueue.add(thread);
        }
    }
}