import threadpooler.ThreadPool;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static List<Integer> intArray = Arrays.asList(1, 2 , 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ThreadPool<Integer, Boolean> pool = new ThreadPool<>(5);
        pool.setInput(intArray);
        pool.setCalculations(input ->  {
            if(input == 12)
                throw new SecurityException();
            return input % 2 == 0;
        });
        pool.setAllDataProcessor(input -> {
            long end = System.currentTimeMillis();
            System.out.println((end - start));
        });

        pool.setHypervisorDelay(100);

        pool.start();
    }
}
