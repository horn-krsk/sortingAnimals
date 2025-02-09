
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Дан массив строк {"Лев", "Кот", "Собака", "Дети", "Медоед", "Россомаха", "Медведь"}
 * Задача раскидать в мапу, где ключ - первая буква слова,
 * значение - список слов на эту букву.
 */

public class Main {
    public static void main(String[] args) {

        String[] arrayString = {"Лев", "Кот", "Собака", "Дети", "Медоед", "Россомаха", "Медведь"};

        HashMap<String, String[]> mapString = new HashMap<>();
        long FillingMap0Time = FillingMap_0(arrayString, mapString);

        System.out.println();//разделитель пустая строка

        HashMap<String, List<String>> mapList = new HashMap<>();
        long FillingMap1Time = FillingMap_1(arrayString, mapList);

        System.out.println();//разделитель пустая строка

        ConcurrentHashMap<String, List<String>> mapListForMult = new ConcurrentHashMap<>();
        long FillingMap2Time = FillingMap_2(arrayString, mapListForMult);

        System.out.println("Время выполнения на примитивах, нс: " + FillingMap0Time
                + "\nВремя выполнения на коллекциях, нс: " + FillingMap1Time
                + "\nВремя выполнения на потоках, нс: " + FillingMap2Time);

    }

    /**
     * На примитивах
     *
     * @param array String[]
     * @param map   HashMap<String, String[]>
     * @return time long
     */
    static long FillingMap_0(String[] array, HashMap<String, String[]> map) {

        long startTime = System.nanoTime();

        for (int i = 0; i < array.length; i++) {

            char key = array[i].toCharArray()[0];

            if (map.containsKey(String.valueOf(key))) break;

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(array[i]).append(";");

            for (int j = i + 1; j < array.length; j++) {
                if (array[j].toCharArray()[0] == key) stringBuilder.append(array[j]);
            }
            map.put(String.valueOf(key), stringBuilder.toString().split(";"));
        }

        long endTime = System.nanoTime();
//TODO раскомментировать, если нужен результат
//        System.out.println("время выполнения FillingMap_0, нс: "
//                + (endTime - startTime));
//        map.forEach((key, value) -> {
//            System.out.println(key + " : " + Arrays.toString(value));
//        });
//        System.out.println();//разделитель пустая строка
        return endTime - startTime;
    }

    /**
     * На коллекциях
     *
     * @param array String[]
     * @param map   HashMap<String, List<String>>
     * @return time long
     */
    static long FillingMap_1(String[] array, HashMap<String, List<String>> map) {

        long startTime = System.nanoTime();
        ArrayList<String> inArrayList = new ArrayList<>(Arrays.asList(array));

        while (!inArrayList.isEmpty()) {

            List<String> outArraylist = new ArrayList<>();
            char key = inArrayList.get(0).toCharArray()[0];

            for (String item : inArrayList) {
                if (item.toCharArray()[0] == key) outArraylist.add(item);
            }
            inArrayList.removeIf(i -> i.toCharArray()[0] == key);
            map.put(String.valueOf(key), outArraylist);
        }

        long endTime = System.nanoTime();
//TODO раскомментировать, если нужен результат
//        System.out.println("время выполнения FillingMap_1, нс: "
//                + (endTime - startTime));
//        map.forEach((key, value) -> {
//            System.out.println(key + " : " + value.toString());
//        });
//        System.out.println();//разделитель пустая строка
        return endTime - startTime;
    }

    /**
     * На потоках
     *
     * @param array String[]
     * @param map   ConcurrentHashMap<String, List<String>>
     * @return time long
     */

    static long FillingMap_2(String[] array, ConcurrentHashMap<String, List<String>> map) {

        long startTime = System.nanoTime();

        List<String> keys = new ArrayList<>();

//        определяем количество уникальных первых букв,
//        по каждой запускаем отдельный поток
        for (String i : array) {
            char key = i.toCharArray()[0];
            if (!keys.contains(String.valueOf(key))) keys.add(String.valueOf(key));
        }

        CountDownLatch countDownLatch = new CountDownLatch(keys.size());
        try {
            for (String key : keys) {
                Runnable runnable = () -> {
                    ArrayList<String> inArrayList = new ArrayList<>(Arrays.asList(array));
                    inArrayList.removeIf(i -> i.toCharArray()[0] != key.toCharArray()[0]);
                    map.put(key, inArrayList);
                    countDownLatch.countDown();
                };
                new Thread(runnable).start();

            }
            countDownLatch.await();
            long endTime = System.nanoTime();
//            System.out.println("время выполнения FillingMap_2, нс: "
//                    + (endTime - startTime));
            map.forEach((i, value) -> {
                System.out.println(i + " : " + value.toString());
            });
            System.out.println();//разделитель пустая строка
            return endTime - startTime;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
