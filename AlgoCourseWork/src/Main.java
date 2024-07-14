/*
 * Name: Kulathantrige Lasanya Divyanie Premarathne
 * UoW ID: w1953145
 * IIT No: 20220185
 */

import java.io.*;
import java.util.*;

public class Main {
    static char[][] maze; // 2D array to store maze
    static boolean[][] visitedNodes; // 2D array to store visited nodes
    static int mazeRow = 0, mazeColumn = 0; // number of rows and columns in the maze
    static int startRow = -1, startColumn = -1, finishRow = -1,finishColumn = -1; // row and column index of start and finish
    static long timeStart, timeEnd; // starting time and the finishing time
    static double elapsedTimeSeconds; // time spent in seconds
    static PriorityQueue<NodeDetails> toBeVisited =new PriorityQueue<>(); // to add the neighboring nodes to visit (a priority queue based on distance)
    static ArrayList<NodeDetails> visited = new ArrayList<>(); // to store the visited nodes ( an arrayList cz later have to search the previous node within the visited list)
    static String[] dir = {"left","right","up","down"};
    static int[] r = {0, 0, -1, 1}; // Change in row for each direction (left, right, up, down)
    static int[] c = {-1, 1, 0, 0}; // Change in column for each direction (left, right, up, down)

    /*
     * to be able to trace back the path when F is found
     */
    public static class NodeDetails implements Comparable<NodeDetails> {
        int row, col, prevRow, prevCol, distance;
        String direction;

        public NodeDetails(int row, int col, int prevRow, int prevCol, String direction, int distance) {
            this.row = row;
            this.col = col;
            this.prevRow = prevRow;
            this.prevCol = prevCol;
            this.direction = direction;
            this.distance = distance;
        }

        /*
         * to accommodate the NodeDetails to print
         */
        public String toString() {
            return "[" + row + "," + col +"," +direction+"," +distance+"," +prevRow+"," +prevCol +"]";
        }

        /*
         * to define the priority when creating the toBeVisited PriorityQueue
         */
        public int compareTo(NodeDetails other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static void main(String[] args) {
        fileReader();

        pathFinder();

    }

    /*
     * simple parser which can read a map, determine the width and height
     * and the locations of the start and finish square
     */
    public static void fileReader() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the file path:");
        // Enter the file path as -> src/<folderName>/<fileName>.txt
        String filePath = scanner.nextLine();

        System.out.println();
        System.out.println("Your puzzle is as follows:");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            ArrayList<String> mazeLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                mazeLines.add(line);
            }
            reader.close();

            // Initialize maze with correct dimensions
            mazeRow = mazeLines.size();
            mazeColumn = mazeLines.get(0).length();
            maze = new char[mazeRow][mazeColumn];
            visitedNodes = new boolean[mazeRow][mazeColumn];

            // Fill maze array and find start and finish points
            for (int i = 0; i < mazeRow; i++) {
                for (int j = 0; j < mazeColumn; j++) {
                    char c = mazeLines.get(i).charAt(j);
                    maze[i][j] = c;
                    if (c == 'S') {
                        startRow = i;
                        startColumn = j;
                        // Add start point to the queue
                        toBeVisited.add(new NodeDetails(startRow, startColumn, -1, -1, "start", 0));
                    } else if (c == 'F') {
                        finishRow = i;
                        finishColumn = j;
                    }
                }
            }

            // displays the start and finish points (shifts to normal indexing starting with 1)
            System.out.println();
            System.out.println("Start Point = [" + (startColumn + 1) + "," + (startRow + 1) + "]");
            System.out.println("End Point = [" + (finishColumn + 1) + "," + (finishRow + 1) + "]");
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    /*
     * to check if index is within bounds
     */
    public static boolean isValid(int row, int col) {
        return row >= 0 && row < mazeRow && col >= 0 && col < mazeColumn;
    }

    /*
     * to determine the direction of the movement and determine the path
     */
    public static void pathFinder() {
        timeStart = System.nanoTime();
        while (!toBeVisited.isEmpty()) {
            // Poll the node with the least distance
            NodeDetails current = toBeVisited.poll();
            int row = current.row;
            int col = current.col;

            // Check all four neighboring cells
            for (int i = 0; i < 4; i++) {
                int newRow = row + r[i];
                int newCol = col + c[i];

                if (isValid(newRow, newCol)) {
                    if (maze[newRow][newCol] == 'F') {
                        visited.add(current);
                        NodeDetails finishNode = new NodeDetails(newRow, newCol, row, col, "finish", current.distance + 1);
                        visited.add(finishNode); // Add finish node to visited
                        printPath(finishNode);
                        return; // Exit method
                    }
                    if (maze[newRow][newCol] == '.') {
                        NodeDetails newNode = new NodeDetails(newRow, newCol, row, col, dir[i], current.distance + 1);
                        slide(newNode , i);
                        if (maze[newNode.row][newNode.col] == 'F') {
                            visited.add(current);
                            NodeDetails finishNode = new NodeDetails(newRow, newCol, row, col, "finish", current.distance + 1);
                            visited.add(finishNode); // Add finish node to visited
                            printPath(finishNode);
                            return; // Exit method
                        }
                    }
                }
            }
            visitedNodes[row][col] = true;
            visited.add(current);
        }
    }

    /*
     * to accommodate the sliding feature
     */
    public static void slide(NodeDetails currentNode, int i) {
        int row = currentNode.row;
        int col = currentNode.col;
        int prevRow = currentNode.prevRow;
        int prevCol = currentNode.prevCol;
        int distance = currentNode.distance + 1;

        while (isValid(row + r[i], col+c[i]) && maze[row +r[i]][col+c[i]] != '0' ) {
            switch (i){
                case 0-> col--; //left
                case 1-> col++; // right
                case 2-> row--; // up
                case 3 -> row++; //down
            }

            if (maze[row][col] == '.' || maze[row][col] == 'S') {
                distance++;
            } else if(maze[row][col] == 'F'){
                distance++;
                break;
            } else {
                // if dint slide, change to the initial position
                switch (i){
                    case 0 -> col++; // left
                    case 1 -> col--; // right
                    case 2 -> row++; // up
                    case 3 -> row--; // down
                }
                break;
            }
        }

        if(!visitedNodes[row][col]) {
            toBeVisited.add(new NodeDetails(row, col, prevRow, prevCol, "up", distance));
        }

        // Update currentNode's row, col, and distance
        currentNode.row = row;
        currentNode.col = col;
        currentNode.distance = distance;
    }

    /*
     * to trace the path through the preRow and prevCol saved using the NodeDetails class
     * saved in the visited ArrayList
     */
    public static NodeDetails findPrevious(NodeDetails current) {
        // Find the previous node in the visited list
        for (NodeDetails visitedNode : visited) {
            if (visitedNode.row == current.prevRow && visitedNode.col == current.prevCol) {
                return visitedNode;
            }
        }
        return null;
    }

    /*
     * to print the path saved in the path stack with the help of findPrevious method
     */
    public static void printPath(NodeDetails finishNode) {
        timeEnd = System.nanoTime();
        Stack<NodeDetails> path = new Stack<>(); // store the path
        NodeDetails current = finishNode;

        // Find the path by traversing the visited nodes
        while (current != null) {
            path.push(current);
            current = findPrevious(current);
        }

        // Print the path
        int step = 1;
        while (!path.isEmpty()) {
            NodeDetails stepNode = path.pop();
            if (stepNode.direction.equals("start")) {
                System.out.println();
                System.out.println(step + ". Start at (" + (startColumn + 1) + ", " + (startRow + 1) + ")");
            } else if (stepNode.direction.equals("finish")) {
                System.out.println(step + ". Finish at (" + (finishColumn + 1) + ", " + (finishRow + 1) + ")");
                step++;
                System.out.println(step + ". Done!");
            } else {
                System.out.println(step + ". Move " + stepNode.direction + " to (" + (stepNode.col + 1) + ", " + (stepNode.row + 1) + ")");
            }
            step++;
        }

        // print the time taken, rounded to three decimal places
        elapsedTimeSeconds = (double) (timeEnd - timeStart) / 1_000_000_000;
        System.out.println();
        System.out.println("It took " + String.format("%.4f", elapsedTimeSeconds) + " seconds to find the path from S to F.");
    }
}