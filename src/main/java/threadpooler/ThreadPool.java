package threadpooler;

import java.util.*;

public class ThreadPool<T, K> {

    private List<T> input = new ArrayList<>();
    private final Set<PooledThread<T, K>> threads = new HashSet<>();
    private final Map<T, K> results = new HashMap<>();
    private final Set<DataProcessor<T, K>> directDataProcessor = new HashSet<>();
    private final Set<MapDataProcessor<T, K>> allDataProcessor = new HashSet<>();
    private final Thread hypervisor;
    private boolean finished, exception = false, waitForStop = false, noData = false;
    private int hypervisorDelay = 1000;

    public ThreadPool(int threadsCount) {
        if(threadsCount == 0)
            throw new SecurityException("Thread Count must be over 1");
        for (int i = 0; i < threadsCount; i++) {
            threads.add(new PooledThread<>(this));
        }
        hypervisor = new Thread(this::hypervisor, this.toString() + "-Hypervisor");
    }

    public ThreadPool() {
        this(Runtime.getRuntime().availableProcessors() - 2);
    }

    public void addDirectDataProcessor(DataProcessor<T, K> dataProcessor) {
        directDataProcessor.add(dataProcessor);
    }

    public void setAllDataProcessor(MapDataProcessor<T, K> dataProcessor) {
        allDataProcessor.add(dataProcessor);
    }

    public void setCalculations(ThreadTask<T, K> task) {
        threads.forEach(tkPooledThread -> tkPooledThread.setTask(task));
    }

    public void setInput(List<T> input) {
        this.input.addAll(input);
    }

    public void addInput(T input) {
        this.input.add(input);
    }

    public void start() {
        threads.forEach(PooledThread::start);
        hypervisor.start();
    }

    public void setDelay(int delay) {
        threads.forEach(tkPooledThread -> tkPooledThread.setDelay(delay));
    }

    public void setHypervisorDelay(int hypervisorDelay) {
        this.hypervisorDelay = hypervisorDelay;
    }

    public void await() throws InterruptedException {
        while (!finished) {
            Thread.sleep(1);
        }
    }

    public void setCallStop(boolean bool) {
        threads.forEach(tkPooledThread -> tkPooledThread.setWaitForStop(bool));
        waitForStop = bool;
    }

    public void lastData() {
        this.noData = true;
    }

    protected void hypervisor() {
        while(!finished) {
            if(threads.stream().allMatch(PooledThread::isFinished)) {
                try {
                    allDataProcessor.forEach(tkMapDataProcessor -> tkMapDataProcessor.execute(results));
                    finished = true;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(noData && input.isEmpty() && waitForStop)
                threads.forEach(PooledThread::callStop);
            if(exception) {
                threads.forEach(PooledThread::abort);
                System.out.println("Aborting all Threads!");
                threads.forEach( tkPooledThread -> {
                    if(tkPooledThread.getStackTrace() != null) {
                        tkPooledThread.getStackTrace().printStackTrace();
                    }
                });
                finished = true;
            }
            try {
                Thread.sleep(hypervisorDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void addResult(T input, K result) {
        results.put(input, result);
        if(!directDataProcessor.isEmpty())
            try {
                directDataProcessor.forEach(dataProcessor -> dataProcessor.execute(input, result));
            }catch (Exception e) {
                System.out.println("Direct Data Processor created an Exception");
                e.printStackTrace();
            }
    }

    protected T getData() {
        if(input.isEmpty())
            return null;
        T val = input.get(0);
        input.remove(0);
        return val;
    }

    protected void setException(boolean exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ThreadPool{" +
                "input=" + input +
                ", threads=" + threads +
                ", results=" + results +
                ", hypervisor=" + hypervisor +
                '}';
    }
}
