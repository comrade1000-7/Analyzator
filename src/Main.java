import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static ArrayBlockingQueue<String> charA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> charB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> charC = new ArrayBlockingQueue<>(100);
    public static String maxStringA;
    public static String maxStringB;
    public static String maxStringC;
    public static int lastResultA;
    public static int lastResultB;
    public static int lastResultC;
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int maxCountChar (String request, char symbol) {
        char[] arr = request.toCharArray();
        int counter = 0;
        for (char sym : arr) {
            if (sym == symbol) {
                counter++;
            }
        }
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
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

        Thread threadA = new Thread(() -> {
            while (producer.isAlive()) {
                try {
                    String temp = charA.take();
                    int actualResult = maxCountChar(temp, 'a');
                    if (actualResult > lastResultA) {
                        lastResultA = actualResult;
                        maxStringA = temp;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread threadB = new Thread(() -> {
            while (producer.isAlive()) {
                try {
                    String temp = charB.take();
                    int actualResult = maxCountChar(temp, 'b');
                    if (actualResult > lastResultB) {
                        lastResultB = actualResult;
                        maxStringB = temp;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread threadC = new Thread(() -> {
            while (producer.isAlive()) {
                try {
                    String temp = charC.take();
                    int actualResult = maxCountChar(temp, 'c');
                    if (actualResult > lastResultC) {
                        lastResultC = actualResult;
                        maxStringC = temp;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

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


        System.out.println("Больше всего а " + maxStringA);
        System.out.println("Больше всего b " + maxStringB);
        System.out.println("Больше всего c " + maxStringC);
    }
}