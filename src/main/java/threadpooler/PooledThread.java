package threadpooler;

import java.util.List;
import java.util.ListIterator;

public class PooledThread<T, K> {

    private ThreadTask<T, K> task;
    private final Thread thread;
    private List<T> data;
    private int delay = 0;
    private final ThreadPool<T, K> threadPool;
    private boolean finished = false;

    public PooledThread(ThreadPool<T, K> threadPool, ThreadTask<T, K> task) {
        this.threadPool = threadPool;
        this.task = task;
        thread = new Thread(this::run, "PooledThread-" + Thread.activeCount());
    }

    public PooledThread(ThreadPool<T, K> threadPool) {
        this.threadPool = threadPool;
        thread = new Thread(this::run, "PooledThread-" + Thread.activeCount());
    }

    private void run() {
        ListIterator<T> dataPoint = data.listIterator();
        while(dataPoint.hasNext()) {
            T currentData = dataPoint.next();
            K currentResult = task.execute(currentData);
            threadPool.addResult(currentData, currentResult);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new SecurityException("Thread " + thread.getName() + "interrupted!", e);
            }
        }
        finished = true;
    }

    public void start(List<T> data) {
        if(task == null) throw new NullPointerException("Task is Null");
        this.data = data;
        thread.start();
    }

    public void start() {
        if(task == null) throw new NullPointerException("Task is Null");
        thread.start();
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTask(ThreadTask<T, K> task) {
        this.task = task;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public String toString() {
        return "PooledThread{" +
                ", data=" + data +
                '}';
    }
}
