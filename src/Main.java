import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static ArrayBlockingQueue<String> charA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> charB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> charC = new ArrayBlockingQueue<>(100);

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            try {
                int max = maxCountChar(queue, letter);
                System.out.println("В потоке " + Thread.currentThread() + " больше всего " + letter + ": " + max);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static int maxCountChar(BlockingQueue<String> queue, char symbol) throws InterruptedException {
        int count = 0;
        int max = 0;
        String temp;
        for (int i = 0; i < 10_000; i++) {
            temp = queue.take();
            for (char a : temp.toCharArray()) {
                if (a == symbol) count++;
            }
            if (count > max) max = count;
            count = 0;
        }
        return max;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    String tmp = generateText("abc", 100_000);
                    charA.put(tmp);
                    charB.put(tmp);
                    charC.put(tmp);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        producer.start();

        Thread threadA = getThread(charA, 'a');
        Thread threadB = getThread(charB, 'b');
        Thread threadC = getThread(charC, 'c');

        threadA.start();
        threadB.start();
        threadC.start();

        producer.join();
        threadA.join();
        threadB.join();
        threadC.join();

        producer.interrupt();
        threadA.interrupt();
        threadB.interrupt();
        threadC.interrupt();
    }
}