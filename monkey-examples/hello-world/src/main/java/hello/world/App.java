/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package hello.world;

import io.monkey.runtime.Monkey;

public class App {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Monkey.run(App.class);

        long end = System.currentTimeMillis();

        System.out.println("startup cost:" + (end - start) + " ms.");
    }
}
