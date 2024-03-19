/*
 *  Bryce Anderson Lab7 March 2024.
 */
package lab7;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.File;
import java.io.IOException;

public class Huffman {
    private String input;
    private String encodedInput;
    private HashMap<Character, String> binaryPrefixCodes = new HashMap<>();
    private Node root;

    /**
     * Builds the Huffman tree without needing to calculate frequencies,
     * build the tree, and set the input codes
     * 
     * @param s String to build tree from
     */
    public Huffman(String s) {
        this.input = s;
        calculateFrequencies(s);
        setPrefixCodes(root, new StringBuilder());
    }

    /**
     * Huffman Tree Node that holds the character, frequency, left, and right.
     */ 
    public class Node implements Comparable<Node> {
        int frequency;
        char character;
        Node left;
        Node right;

        /** Used for making leaf nodes based on the frequency and character */
        public Node(int freq, char c) {
            this.frequency = freq;
            this.character = c;
        }

        /** Used for making parent nodes with no characters */
        public Node(int freq, Node l, Node r) {
            this.frequency = freq;
            this.left = l;
            this.right = r;
        }

        /** Used for implementing comparable in the priority queue */
        @Override
        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }
    }
    
    /**
     * 
     * @param s
     */
    public void calculateFrequencies(String s) {
        // Frequencies hashmap for an integer value for each character
        HashMap<Character, Integer> frequencies = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            frequencies.put(s.charAt(i), frequencies.getOrDefault(s.charAt(i), 0) + 1);
        }

        PriorityQueue<Node> queue = new PriorityQueue<>();
        // Adds all of the characters as Nodes to the Prioirty Queue
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            Node node = new Node(entry.getValue(), entry.getKey());
            queue.add(node);
        }

        buildTree(queue);
    }

    /**
     * Builds the tree by polling from the queue, it then returns the root
     * of the main tree that it built
     */
    private void buildTree(PriorityQueue<Node> queue) {
        while (queue.size() > 1) {
            Node x = queue.poll();
            Node y = queue.poll();

            Node newNode = new Node(x.frequency + y.frequency, x, y);

            queue.add(newNode);
        }

        root = queue.poll();
    }

    /**
     * Sets the prefix codes based on the created tree
     * 
     * @param node   Node to traverse or encode
     * @param prefix The current string builder
     */
    private void setPrefixCodes(Node node, StringBuilder prefix) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                // Adds the leaf node to the to the map with the current prefix
                binaryPrefixCodes.put(node.character, prefix.toString());
            } else {
                // Traverses down the left tree adding a '0'
                prefix.append('0');
                setPrefixCodes(node.left, prefix);
                prefix.deleteCharAt(prefix.length() - 1);

                // Traverses down the left tree adding a '1'
                prefix.append('1');
                setPrefixCodes(node.right, prefix);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    /**
     * Returns an encoded version of the input string in binary
     * 
     * @return An encoded version of the input string in binary
     */
    public String encode() {
        return encode(this.input);
    }
    public String encode(String s) {
        StringBuilder sb = new StringBuilder();

        // Uses the binaryPrefixCodes to encode the string
        for (int i = 0; i < s.length(); i++) {
            sb.append(binaryPrefixCodes.get(s.charAt(i)));
        }
 
        this.encodedInput = sb.toString();
        return encodedInput;
    }

    /**
     * Returns a decoded version of the encoded input String
     * 
     * @return A decoded version of the encoded input String
     */
    public String decode() {
        return decode(encodedInput);
    }
    public String decode(String s) {
        StringBuilder sb = new StringBuilder();
        Node currentNode = root;

        int i = 0;
        while (i < s.length()) {
            // Traverses left subtree if zero, otherwise it goes right
            currentNode = (s.charAt(i) == '0') ? currentNode.left : currentNode.right;

            // Adds the character to the stringbuilder, since it's a leaf
            if (currentNode.left == null && currentNode.right == null) {
                sb.append(currentNode.character);
                currentNode = root;
            }
            i++;
        }

        return sb.toString();
    }

    /**
     * Returns the compression rate of the Huffman Tree
     * 
     * @return The compression rate of the huffman tree
     */
    public double getCompressionRate() {
        return (double) encodedInput.length() / input.length() / 8.0;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Please provide an input file as a command-line argument");
            return;
        }
    
        String filename = args[0];
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        StringBuilder sb = new StringBuilder();

        // Uses a scanner and a string builder to store the file
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
            sb.append("\n");
        }
        // Removes last line break
        sb.deleteCharAt(sb.length() - 1);

        sc.close();

        String input = sb.toString();
        
        // Creates a Huffman object with the string directly
        Huffman huff = new Huffman(input);
    
        // if the input is less than 100 characters, it will print it
        if (input.length() < 100) {
            System.out.println("Input string: " + input);
    
            String encodedString = huff.encode();
            System.out.println("Encoded string: " + encodedString);
    
            String decodedString = huff.decode();
            System.out.println("Decoded string: " + decodedString);
        } else {
            huff.encode();
            huff.decode();
        }
    
        Boolean decodeStatus = huff.decode().equals(input);
        System.out.println("Decoded equals input: " + decodeStatus);
    
        double compressionRate = huff.getCompressionRate();
        System.out.println("Compression rate: " + compressionRate);
    }
}