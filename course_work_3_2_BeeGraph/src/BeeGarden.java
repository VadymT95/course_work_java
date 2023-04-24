
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class BeeGarden {
    private Graph mainGraph;
    private int id;

    private List<Hive> hives;
    private int numberOfThreads;

    private int totalNumberBees;
    private int numberInactive;
    private int numberActive;
    private int numberScout;
    private int maxNumberVisits;
    private int maxNumberCycles;
    private int typeOfChangeColor;

    private ExecutorService executorService;

    public BeeGarden(int id, Graph mainGraph, int numberOfThreads,
                     int totalNumberBees, int numberInactive,
                     int numberActive, int numberScout,
                     int maxNumberVisits, int maxNumberCycles,
                     int typeOfChangeColor) {
        this.id = id;
        this.mainGraph = mainGraph;
        this.numberOfThreads = numberOfThreads;
        this.hives = new ArrayList<>();

        this.totalNumberBees = totalNumberBees;
        this.numberInactive = numberInactive;
        this.numberActive = numberActive;
        this.numberScout = numberScout;
        this.maxNumberVisits = maxNumberVisits;
        this.maxNumberCycles = maxNumberCycles;
        this.typeOfChangeColor = typeOfChangeColor;
    }

    public Graph solveGraphColoring() {
        // Split the graph into subgraphs and create swarms
        createSwarms();

        // Run swarms in a thread pool
        List<Future<Graph>> results = runSwarmsInThreadPool();

        // Wait for swarms to complete and get colored subgraphs
        List<Graph> coloredSubgraphs = retrieveColoredSubgraphs(results);

        // Combine colored subgraphs
        Graph coloredGraph = mergeColoredSubgraphs(coloredSubgraphs);

        return coloredGraph;
    }

    private void createSwarms() {
        // We divide the graph into subgraphs
        List<Graph> subgraphs = splitGraph(mainGraph, numberOfThreads);

            // We create swarms for each subgraph
        for (Graph subgraph : subgraphs) {
            hives.add(new Hive(totalNumberBees, numberInactive, numberActive, numberScout,
                    maxNumberVisits, maxNumberCycles, typeOfChangeColor, subgraph));
        }
    }
    private List<Graph> splitGraph(Graph graph, int numberOfSubgraphs) {
        List<Graph> subgraphs = new ArrayList<>();

        int numberOfVertices = graph.getNumberOfVertices();
        int verticesPerSubgraph = numberOfVertices / numberOfSubgraphs;
        int remainder = numberOfVertices % numberOfSubgraphs;

        int startIndex = 0;
        for (int i = 0; i < numberOfSubgraphs; i++) {
            int endIndex = startIndex + verticesPerSubgraph;
            if (remainder > 0) {
                endIndex++;
                remainder--;
            }

            List<Integer> vertexIndices = new ArrayList<>();
            for (int j = startIndex; j < endIndex; j++) {
                vertexIndices.add(j);
            }

            Graph subgraph = createSubgraph(graph, vertexIndices);
            subgraphs.add(subgraph);
            startIndex = endIndex;
        }

        return subgraphs;
    }

    private Graph createSubgraph(Graph graph, List<Integer> vertexIndices) {
        ArrayList<GraphNode> subgraphNodes = new ArrayList<>();
        int numberOfConnections = 0;

        for (Integer vertexIndex : vertexIndices) {
            GraphNode originalNode = graph.getNode(vertexIndex);
            GraphNode subgraphNode = originalNode.clone();

            ArrayList<Integer> subgraphConnections = new ArrayList<>();
            ArrayList<Integer> connectionsCopy = new ArrayList<>(subgraphNode.getConnectionsList());
            for (Integer connection : connectionsCopy) {
                if (vertexIndices.contains(connection)) {
                    subgraphConnections.add(connection);
                } else {
                    subgraphNode.removeConection(connection);
                }
            }
            numberOfConnections += subgraphConnections.size();
            subgraphNode.setConnectionsList(subgraphConnections);
            subgraphNodes.add(subgraphNode);
        }

        numberOfConnections /= 2;
        Graph subgraph = new Graph(subgraphNodes, numberOfConnections, graph.getMaxNumberOfConections(), graph.getNumberOfColours());
        return subgraph;
    }

    private List<Future<Graph>> runSwarmsInThreadPool() {
        // We create an ExecutorService with a fixed number of threads
        executorService = Executors.newFixedThreadPool(hives.size());

        // We create a list to store the results
        List<Future<Graph>> results = new ArrayList<>();

        // We start swarms in the thread pool
        for (Hive hive : hives) {
            Future<Graph> future = executorService.submit(() -> hive.solveGraphColoring());
            results.add(future);
        }

        // We close the ExecutorService and wait for the completion of all threads
        executorService.shutdown();

        return results;
    }

    private List<Graph> retrieveColoredSubgraphs(List<Future<Graph>> results) {
        List<Graph> coloredSubgraphs = new ArrayList<>();

        for (Future<Graph> future : results) {
            try {
                coloredSubgraphs.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return coloredSubgraphs;
    }
    public Graph mergeColoredSubgraphs(List<Graph> coloredSubgraphs) {
        // Rewrite the colors of vertices in mainGraph according to their indices
        for (Graph subgraph : coloredSubgraphs) {
            for (GraphNode node : subgraph.getNodesList()) {
                mainGraph.getNode(node.getIndex()).setColor(node.getColor());
            }
        }

        // Resolve color collisions at subgraph junctions
        boolean hasConflicts;
        do {
            hasConflicts = false;
            for (GraphNode node : mainGraph.getNodesList()) {
                for (int neighborIndex : node.getConnectionsList()) {
                    GraphNode neighbor = mainGraph.getNode(neighborIndex);
                    if (node.getColor() == neighbor.getColor()) {
                        hasConflicts = true;

                        // Change the color of one of the adjacent vertices to a color that does not match the colors of other adjacent vertices
                        int newColor = findNewColorForNode(node, mainGraph);
                        node.setColor(newColor);
                    }
                }
            }
        } while (hasConflicts);

        return mainGraph;
    }

    private int findNewColorForNode(GraphNode node, Graph graph) {
        // This method finds a new color for a vertex that does not match the colors of other adjacent vertices
        Set<Integer> usedColors = new HashSet<>();
        for (int neighborIndex : node.getConnectionsList()) {
            usedColors.add(graph.getNode(neighborIndex).getColor());
        }

        int newColor = 1;
        while (usedColors.contains(newColor)) {
            newColor++;
        }
        return newColor;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNumberOfThreads() {
        return numberOfThreads;
    }
    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    public int getTotalNumberBees() {
        return totalNumberBees;
    }
    public void setTotalNumberBees(int totalNumberBees) {
        this.totalNumberBees = totalNumberBees;
    }
    public int getNumberInactive() {
        return numberInactive;
    }
    public void setNumberInactive(int numberInactive) {
        this.numberInactive = numberInactive;
    }
    public int getNumberActive() {
        return numberActive;
    }
    public void setNumberActive(int numberActive) {
        this.numberActive = numberActive;
    }
    public int getNumberScout() {
        return numberScout;
    }
    public void setNumberScout(int numberScout) {
        this.numberScout = numberScout;
    }
    public int getMaxNumberVisits() {
        return maxNumberVisits;
    }
    public void setMaxNumberVisits(int maxNumberVisits) {
        this.maxNumberVisits = maxNumberVisits;
    }
    public int getMaxNumberCycles() {
        return maxNumberCycles;
    }
    public void setMaxNumberCycles(int maxNumberCycles) {
        this.maxNumberCycles = maxNumberCycles;
    }
    public int getTypeOfChangeColor() {
        return typeOfChangeColor;
    }
    public void setTypeOfChangeColor(int typeOfChangeColor) {
        this.typeOfChangeColor = typeOfChangeColor;
    }
}