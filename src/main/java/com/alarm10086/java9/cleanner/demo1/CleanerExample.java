package com.alarm10086.java9.cleanner.demo1;

import java.lang.ref.Cleaner;

/**
 * see https://www.logicbig.com/tutorials/core-java-tutorial/gc/ref-cleaner.html
 *
 * @author alarm10086
 *
 */
public class CleanerExample {
    public static void main(String[] args) {
        Cleaner cleaner = Cleaner.create();
        for (int i = 0; i < 10; i++) {
            String id = Integer.toString(i);
            MyObject myObject = new MyObject(id);
            cleaner.register(myObject, new CleanerRunnable(id));
        }

        //myObjects are not reachable anymore
        //do some other memory intensive work
        for (int i = 1; i <= 10000; i++) {
            int[] a = new int[10000];
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    private static class CleanerRunnable implements Runnable {
        private String id;

        public CleanerRunnable(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.printf("MyObject with id %s, is gc'ed%n", id);

        }
    }
}
