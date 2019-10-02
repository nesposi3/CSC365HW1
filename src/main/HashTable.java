package main;

import java.util.function.Consumer;

public class HashTable {
    public static final double RESIZE_RATIO = .75;
    public static final int INITIAL_SIZE = 1024;

    public static class Node {
        public Node(String key) {
            this.key = key;
            this.hashCode = key.hashCode();
            this.wordFrequency = 1;
            this.next = null;
        }

        public String key;
        public int wordFrequency;
        public int hashCode;
        public Node next;
    }
    private String name;
    private int numKeys;
    private Node[] arr;

    public Node get(String key) {
        int h = key.hashCode();
        int i = h & (arr.length - 1);
        Node n = arr[i];
        while (n != null) {
            if (n.key.equals(key)) {
                return n;
            }
            n = n.next;
        }
        return null;
    }

    private void resize() {
        int newArrSize = arr.length * 2;
        Node[] newArr = new Node[newArrSize];
        for (int i = 0; i < arr.length; i++) {
            Node n = arr[i];
            while (n != null) {
                int h = n.hashCode;
                int newIndex = h & (newArrSize - 1);
                if (newArr[newIndex] != null && !newArr[newIndex].key.equals(n.key)) {
                    newArr[newIndex].next = n;
                } else {
                    newArr[newIndex] = n;
                }
                n = n.next;
            }
        }
        this.arr = newArr;
    }

    public boolean contains(String key) {
        int h = key.hashCode();
        int index = h & ((arr.length) - 1);
        Node n = arr[index];
        while (n != null) {
            if (n.key.equals(key)) {
                return true;
            }
            n = n.next;
        }
        return false;
    }

    public void add(String key) {
        int h = key.hashCode();
        int index = h & ((arr.length) - 1);
        Node n = arr[index];
        while (n != null) {
            if (n.key.equals(key)) {
                //Node already exists, increase frequency by one
                n.wordFrequency++;
                return;
            }
            n = n.next;
        }
        //Node does not exist. Add new node and resize if necessary.
        n = new Node(key);
        if (arr[index] == null) {
            arr[index] = n;
        } else {
            arr[index].next = n;
        }
        this.numKeys++;
        double ratio = ((double) numKeys / (double) arr.length);
        if (ratio >= RESIZE_RATIO) {
            // Resize necessary
            resize();
        }

    }

    public void printAll() {
        for (int i = 0; i < arr.length; i++) {
            Node n = arr[i];
            while (n != null) {
                System.out.println(n.key + " " + n.wordFrequency);
                n = n.next;
            }
        }
    }

    public HashTable(String name) {
        this.arr = new Node[INITIAL_SIZE];
        this.numKeys = 0;
        this.name = name;
    }

    /**
     * This is the number of nodes the hashmap has
     * @return
     */
    public int size(){
        int j = 0;
        for (int i = 0; i <arr.length ; i++) {
            Node n = arr[i];
            while (n!=null){
                j++;
                n = n.next;
            }
        }
        return j;
    }

    /**
     * This measures how many total words were in a document, takes into account word frequency in nodes
     * @return
     */
    public int totalWordCount(){
        int j = 0;
        for (int i = 0; i <arr.length ; i++) {
            Node n = arr[i];
            while (n!=null){
                j += n.wordFrequency;
                n = n.next;
            }
        }
        return j;
    }

    /**
     * Iterates over whole hashtable
     * @param consumer A lambda expression to apply to each node
     */
    public void forEach(Consumer<Node> consumer){
        for (int i = 0; i < arr.length; i++) {
            Node n = arr[i];
            while (n != null) {
                consumer.accept(n);
                n = n.next;
            }
        }
    }
    public String getName(){
        return name;
    }
}
