import threadpooler.ThreadPool;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static List<Integer> intArray = Arrays.asList(1, 2 , 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);

    public static void main(String[] args) {
        ThreadPool<Integer, Boolean> pool = new ThreadPool<>(4);
        pool.setInput(intArray);
        pool.setCalculations(input -> input % 2 == 0);
        pool.addDirectDataProcessor((input, result) ->
                System.out.println(input + " | " + result));
        pool.setAllDataProcessor(input -> System.out.println(input));

        System.out.println(pool.toString());

        pool.setDelay(2000);
        pool.setHypervisorDelay(500);

        pool.start();
    }
}
