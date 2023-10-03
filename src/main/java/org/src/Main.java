package org.src;

public class Main {
    public static void main(String[] args) {
        int numIterations = 10000; // Number of iterations
        int numThreads = 7; // Number of threads (adjust as needed)
        int iterationsPerThread = numIterations / numThreads; // Iterations per thread

        for (int i = 0; i < numThreads; i++) {
            // Call your program's logic here
            Thread thread = new Thread(TicTacToeGUI::start);
            thread.start();
        }

    }
}
