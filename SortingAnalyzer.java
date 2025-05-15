import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SortingAnalyzer extends JFrame {
    private int[] array;
    private final int size = 15;
    private SortingPanel sortingPanel;
    private JComboBox<String> algorithmBox;
    private JSlider speedSlider;
    private JButton startButton, generateButton;
    private volatile boolean isSorting = false;

    public SortingAnalyzer() {
        setTitle("Sorting Analyzer");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        sortingPanel = new SortingPanel();
        add(sortingPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        algorithmBox = new JComboBox<>(new String[]{
            "Bubble Sort", "Selection Sort", "Insertion Sort",
            "Merge Sort", "Quick Sort", "Heap Sort",
            "Radix Sort", "Shell Sort", "Bucket Sort", "Counting Sort"
        });
        speedSlider = new JSlider(1, 100, 50);
        startButton = new JButton("Start Sorting");
        generateButton = new JButton("Generate Array");

        controlPanel.add(algorithmBox);
        controlPanel.add(new JLabel("Speed"));
        controlPanel.add(speedSlider);
        controlPanel.add(generateButton);
        controlPanel.add(startButton);
        add(controlPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> {
            if (!isSorting) {
                generateArray();
                sortingPanel.setArray(array);
            }
        });

        startButton.addActionListener(e -> {
            if (!isSorting) {
                isSorting = true;
                new Thread(() -> {
                    String algo = (String) algorithmBox.getSelectedItem();
                    Sorter sorter = new Sorter(array, sortingPanel, speedSlider);
                    sorter.showTimeComplexity(algo);
                    sorter.sort(algo);
                    isSorting = false;
                }).start();
            }
        });

        generateArray();
        sortingPanel.setArray(array);
        setVisible(true);
    }

    private void generateArray() {
        array = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(500) + 10;
        }
    }

    public static void main(String[] args) {
        new SortingAnalyzer();
    }
}

class SortingPanel extends JPanel {
    private int[] array;
    private int[] colorCodes;

    public void setArray(int[] array) {
        this.array = array.clone();
        this.colorCodes = new int[array.length];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (array == null) return;
        int width = getWidth() / array.length;
        for (int i = 0; i < array.length; i++) {
            g.setColor(getColor(colorCodes != null ? colorCodes[i] : 0));
            g.fillRect(i * width, getHeight() - array[i], width, array[i]);
            g.setColor(Color.BLACK);
            g.drawRect(i * width, getHeight() - array[i], width, array[i]);
        }
    }

    private Color getColor(int code) {
        switch (code) {
            case 1: return Color.RED;
            case 2: return Color.GREEN;
            case 3: return Color.ORANGE;
            default: return Color.BLUE;
        }
    }

    public void updateArray(int[] array, int[] highlights) {
        this.array = array.clone();
        this.colorCodes = highlights.clone();
        repaint();
    }

    public void updateArray(int[] array) {
        this.array = array.clone();
        if (this.colorCodes == null || this.colorCodes.length != array.length) {
            this.colorCodes = new int[array.length];
        } else {
            Arrays.fill(this.colorCodes, 0);
        }
        repaint();
    }
}

class Sorter {
    private int[] array;
    private SortingPanel panel;
    private JSlider speedSlider;

    public Sorter(int[] array, SortingPanel panel, JSlider speedSlider) {
        this.array = array;
        this.panel = panel;
        this.speedSlider = speedSlider;
    }

    private void sleep() {
        try {
            Thread.sleep(105 - speedSlider.getValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void showTimeComplexity(String algo) {
        String best = "", worst = "";
        switch (algo) {
            case "Bubble Sort": best = "O(n)", worst = "O(n^2)"; break;
            case "Selection Sort": best = "O(n^2)", worst = "O(n^2)"; break;
            case "Insertion Sort": best = "O(n)", worst = "O(n^2)"; break;
            case "Merge Sort": best = worst = "O(n log n)"; break;
            case "Quick Sort": best = "O(n log n)", worst = "O(n^2)"; break;
            case "Heap Sort": best = worst = "O(n log n)"; break;
            case "Radix Sort": best = worst = "O(nk)"; break;
            case "Shell Sort": best = "O(n log n)", worst = "O(n^2)"; break;
            case "Bucket Sort": best = "O(n+k)", worst = "O(n^2)"; break;
            case "Counting Sort": best = worst = "O(n+k)"; break;
        }
        JOptionPane.showMessageDialog(null,
            algo + "\nBest Case: " + best + "\nWorst Case: " + worst,
            "Time Complexity", JOptionPane.INFORMATION_MESSAGE);
    }

    public void sort(String algo) {
        switch (algo) {
            case "Bubble Sort": bubbleSort(); break;
            case "Selection Sort": selectionSort(); break;
            case "Insertion Sort": insertionSort(); break;
            case "Merge Sort": mergeSort(0, array.length - 1); break;
            case "Quick Sort": quickSort(0, array.length - 1); break;
            case "Heap Sort": heapSort(); break;
            case "Radix Sort": radixSort(); break;
            case "Shell Sort": shellSort(); break;
            case "Bucket Sort": bucketSort(); break;
            case "Counting Sort": countingSort(); break;
        }
        panel.updateArray(array);
    }

    private void bubbleSort() {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
                panel.updateArray(array);
                sleep();
            }
        }
    }

    private void selectionSort() {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
            panel.updateArray(array);
            sleep();
        }
    }

    private void insertionSort() {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
                panel.updateArray(array);
                sleep();
            }
            array[j + 1] = key;
            panel.updateArray(array);
            sleep();
        }
    }

    private void mergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i)
            L[i] = array[left + i];
        for (int j = 0; j < n2; ++j)
            R[j] = array[mid + 1 + j];

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            array[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
            panel.updateArray(array);
            sleep();
        }

        while (i < n1) {
            array[k++] = L[i++];
            panel.updateArray(array);
            sleep();
        }

        while (j < n2) {
            array[k++] = R[j++];
            panel.updateArray(array);
            sleep();
        }
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                panel.updateArray(array);
                sleep();
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        panel.updateArray(array);
        sleep();
        return i + 1;
    }

    private void heapSort() {
        int n = array.length;
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(n, i);

        for (int i = n - 1; i > 0; i--) {
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            panel.updateArray(array);
            sleep();
            heapify(i, 0);
        }
    }

    private void heapify(int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && array[l] > array[largest])
            largest = l;

        if (r < n && array[r] > array[largest])
            largest = r;

        if (largest != i) {
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            panel.updateArray(array);
            sleep();
            heapify(n, largest);
        }
    }

    private void radixSort() {
        int max = Arrays.stream(array).max().orElse(0);
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(exp);
        }
    }

    private void countingSortByDigit(int exp) {
        int n = array.length;
        int[] output = new int[n];
        int[] count = new int[10];

        for (int i = 0; i < n; i++)
            count[(array[i] / exp) % 10]++;

        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            output[count[(array[i] / exp) % 10] - 1] = array[i];
            count[(array[i] / exp) % 10]--;
        }

        for (int i = 0; i < n; i++) {
            array[i] = output[i];
            panel.updateArray(array);
            sleep();
        }
    }

    private void shellSort() {
        int n = array.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = array[i];
                int j;
                for (j = i; j >= gap && array[j - gap] > temp; j -= gap) {
                    array[j] = array[j - gap];
                    panel.updateArray(array);
                    sleep();
                }
                array[j] = temp;
                panel.updateArray(array);
                sleep();
            }
        }
    }

    private void bucketSort() {
        int n = array.length;
        if (n <= 0)
            return;

        int max = Arrays.stream(array).max().getAsInt();
        int bucketCount = (max / 10) + 1;
        List<List<Integer>> buckets = new ArrayList<>(bucketCount);

        for (int i = 0; i < bucketCount; i++)
            buckets.add(new ArrayList<>());

        for (int num : array) {
            buckets.get(num / 10).add(num);
        }

        for (List<Integer> bucket : buckets) {
            Collections.sort(bucket);
        }

        int index = 0;
        for (List<Integer> bucket : buckets) {
            for (int num : bucket) {
                array[index++] = num;
                panel.updateArray(array);
                sleep();
            }
        }
    }

    private void countingSort() {
        int max = Arrays.stream(array).max().orElse(0);
        int[] count = new int[max + 1];
        for (int num : array)
            count[num]++;

        int index = 0;
        for (int i = 0; i <= max; i++) {
            while (count[i]-- > 0) {
                array[index++] = i;
                panel.updateArray(array);
                sleep();
            }
        }
    }
}
