package threadpooler;

import java.util.*;
import java.util.stream.Collectors;

public class ThreadPool<T, K> {

    private List<T> input;
    private Set<PooledThread<T, K>> threads = new HashSet<>();
    private Map<T, K> results = new HashMap<>();
    private Set<DataProcessor<T, K>> directDataProcessor = new HashSet<>();
    private Set<MapDataProcessor<T, K>> allDataProcessor = new HashSet<>();
    private Thread hypervisor;
    private boolean finished;
    private int hypervisorDelay = 1000;

    public ThreadPool(int threadsCount) {
        if(threadsCount == 0)
            throw new SecurityException("Thread Count must be over 1");
        for (int i = 0; i < threadsCount; i++) {
            threads.add(new PooledThread<T, K>(this));
        }
        hypervisor = new Thread(this::hypervisor, this.toString() + "-Hypervisor");
        hypervisor.start();
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
        this.input = input;
        double splitSize = (double) input.size() / (double) threads.size();
        Iterator<PooledThread<T, K>> iterator = threads.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            PooledThread<T, K> currentThread = iterator.next();
            int finalI = (int) i;
            List<T> currentInput = new ArrayList<>(input.stream().filter(t -> input.indexOf(t) >= finalI * splitSize && input.indexOf(t) < finalI * splitSize + splitSize).collect(Collectors.toList()));
            currentThread.setData(currentInput);
            i++;
        }
    }

    public void start() {
        threads.forEach(PooledThread::start);
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

    protected void hypervisor() {
        while(!finished) {
            if(threads.stream().noneMatch(tkPooledThread -> !tkPooledThread.isFinished())) {
                allDataProcessor.forEach(tkMapDataProcessor -> tkMapDataProcessor.execute(results));
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
        if(directDataProcessor != null)
            try {
                directDataProcessor.forEach(dataProcessor -> dataProcessor.execute(input, result));
            }catch (Exception e) {
                System.out.println("Direct Data Processor created an Exception");
                e.printStackTrace();
            }
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
