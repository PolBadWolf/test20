package org.example.test20.core;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Runner {

    private static final BlockingQueue<byte[]> BLOCKING_QUEUE = new ArrayBlockingQueue<>(2);

    public static void main(String[] args) {
        Supplier supplier = new Supplier();
        Consumer consumer = new Consumer();

        new Thread(supplier).start();
        new Thread(consumer).start();

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //supplier.stop();
        //consumer.stop();

    }

    private static class Supplier implements Runnable {
        private final AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void run() {
            while (running.get()) {
                BLOCKING_QUEUE.add(UUID.randomUUID().toString().getBytes());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            running.set(false);
        }

    }

    private static class Consumer implements Runnable {
        private final AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void run() {
            while (running.get()) {
                try {
                    byte[] bytes = BLOCKING_QUEUE.poll(1, TimeUnit.SECONDS);
                    if (bytes != null) {
                        System.out.println(String.format("%s - received %s", Instant.now(), new String(bytes)));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            running.set(false);
        }

    }

}
