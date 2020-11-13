# ThreadPool

Allow making Worker Threads and Using them in a Thread Pool!

## How to Use
### Create the ThreadPool
A ThreadPool has 2 Type Parameters. The first is the Input, the second is the Output. \
If the ThreadPool isn't given a number it takes the amount of Threads given by the Processor.
```java
ThreadPool<Integer, Boolean> pool = new ThreadPool(2); 
//Creates a ThreadPool with 2 Threads which takes an Integer and outputs a Boolean
```

### Give it an Input
The input can be any List of the First Type Parameter given to the ThreadPool.
```java
pool.setInput(Arrays.asList(1, 2 , 3, 4));
```

### Set the Operation
The Operation is an Interface of the type ThreadTask.
```java
pool.setCalculations(input -> {
    //Put Operation here!
});
```

### Add an Direct Data Processor
A Direct Data Processor is triggered everytime a new result is Finished.
```java
pool.addDirectDataProcessor((input, result) -> {
    //Process Data here!
});
```


### Add an All Data Processor
An All Data Processor is triggered after all Operations are completed.
```java
pool.setAllDataProcessor(input -> {
    //Process all Data here
});
```

### Set The Hypervisor Delay
This is optional but if the Delay between checking if the Threads are Finished is too Long or too short for your use case.\
The Default delay is 1000 Milliseconds.
```java
pool.setHypervisorDelay(100);
``` 

### Start the Pool
After all of that the ThreadPool must be started.
```java
pool.start();
```
