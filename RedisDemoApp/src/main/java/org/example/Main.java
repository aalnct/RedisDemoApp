package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Map<String,String > dataMap = new HashMap<>();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public Main () {}

    private static void sleep (long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }

    static void simpleAsynchExample () throws Exception {
        System.out.println(" Example 1 : Simple Asynch Example Operations ");

        CompletableFuture<String> future =
                CompletableFuture.supplyAsync( () -> {
                    System.out.println(" Fetching value for key 1 on thread: " + Thread.currentThread()
                            .getName());

                    sleep(500);
                    return dataMap.get("key1");
                }, executorService);
    }

    static void chainedOperations () {

    }

    public static void main(String[] args) {

    }
}