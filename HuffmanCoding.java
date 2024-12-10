import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode {
    char data;
    int frequency;
    HuffmanNode left, right;

    HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        left = right = null;
    }
}

public class HuffmanCoding {
    private static HashMap<Character, String> huffmanCodes = new HashMap<>();
    private static HashMap<String, Character> reverseHuffmanCodes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Read input from a file
        File inputFile = new File("input.txt");
        String message = readFile(inputFile);

        // Build frequency map
        HashMap<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : message.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        // Build Huffman Tree
        HuffmanNode root = buildHuffmanTree(frequencyMap);

        // Generate Huffman Codes
        generateCodes(root, new StringBuilder());

        // Encode the message
        StringBuilder encodedMessage = new StringBuilder();
        for (char c : message.toCharArray()) {
            encodedMessage.append(huffmanCodes.get(c));
        }

        // Write the encoded message to a text file
        File compressedFile = new File("compressed.txt");
        writeTextFile(compressedFile, encodedMessage.toString());

        // Save the Huffman codes for decompression
        File codesFile = new File("codes.txt");
        saveHuffmanCodes(codesFile);

        System.out.println("Huffman encoding completed. Compressed data saved to 'compressed.txt'.");

        // Read and decompress the file
        String compressedMessage = readFile(compressedFile);
        reverseHuffmanCodes = loadHuffmanCodes(codesFile);
        String decompressedMessage = decompress(compressedMessage, root);

        System.out.println("Decompressed message:");
        System.out.println(decompressedMessage);
    }

    // Read file content as a string
    private static String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            content.append(line).append("\n");
        }
        br.close();
        return content.toString().trim();
    }

    // Build Huffman Tree
    private static HuffmanNode buildHuffmanTree(HashMap<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue =
                new PriorityQueue<>((a, b) -> a.frequency - b.frequency);

        for (var entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode newNode = new HuffmanNode('$', left.frequency + right.frequency);
            newNode.left = left;
            newNode.right = right;
            priorityQueue.add(newNode);
        }

        return priorityQueue.poll();
    }

    // Generate Huffman Codes
    private static void generateCodes(HuffmanNode root, StringBuilder code) {
        if (root == null) return;

        if (root.data != '$') {
            huffmanCodes.put(root.data, code.toString());
            reverseHuffmanCodes.put(code.toString(), root.data);
        }

        generateCodes(root.left, code.append('0'));
        code.deleteCharAt(code.length() - 1);

        generateCodes(root.right, code.append('1'));
        code.deleteCharAt(code.length() - 1);
    }

    // Write text data to a file
    private static void writeTextFile(File file, String encodedMessage) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(encodedMessage);
        }
    }

    // Save Huffman Codes to a file
    private static void saveHuffmanCodes(File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (var entry : huffmanCodes.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        }
    }

    // Load Huffman Codes from a file
    private static HashMap<String, Character> loadHuffmanCodes(File file) throws IOException {
        HashMap<String, Character> codes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                codes.put(parts[1], parts[0].charAt(0));
            }
        }
        return codes;
    }

    // Decompress the encoded message
    private static String decompress(String encodedMessage, HuffmanNode root) {
        StringBuilder result = new StringBuilder();
        HuffmanNode current = root;

        for (char bit : encodedMessage.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.left == null && current.right == null) {
                result.append(current.data);
                current = root;
            }
        }

        return result.toString();
    }
                                               }
