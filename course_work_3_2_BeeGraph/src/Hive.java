
import java.util.List;
import java.util.Random;

public class Hive {

    Graph subGraph;

    public int totalNumberBees;
    public int numberInactive;
    public int numberActive;
    public int numberScout;

    public int typeOfChangeColor = 2;

    public int maxNumberCycles;
    public int maxNumberVisits;

    public double probPersuasion = 0.90;
    public double probMistake = 0.01;

    public Bee[] bees;
    public Graph bestMemorySituation;
    public int bestMeasureOfQuality;
    public int[] indexesOfInactiveBees;
    public int numberOfSteps = 0;
    private boolean permitToChangeBestSituation = false;

    public Hive(int totalNumberBees, int numberInactive,
                int numberActive, int numberScout, int maxNumberVisits,
                int maxNumberCycles, int typeOfChangeColor, Graph subGraph) {
        this.totalNumberBees = totalNumberBees;
        this.subGraph = subGraph;
        this.numberInactive = numberInactive;
        this.numberActive = numberActive;
        this.numberScout = numberScout;
        this.maxNumberVisits = maxNumberVisits;
        this.maxNumberCycles = maxNumberCycles;
        this.typeOfChangeColor = typeOfChangeColor;

        this.bees = new Bee[totalNumberBees];
        this.bestMemorySituation = generateRandomSituation();
        this.bestMeasureOfQuality =
                qualityOfSituation(this.bestMemorySituation);

        this.indexesOfInactiveBees = new int[numberInactive];

        for (int i = 0; i < totalNumberBees; ++i) {
            int currStatus;
            if (i < numberInactive) {
                currStatus = 0; // inactive
                indexesOfInactiveBees[i] = i;
            }
            else if (i < numberInactive + numberScout)
                currStatus = 2; // scout
            else
                currStatus = 1; // active

            Graph randomMemorySituation = generateRandomSituation();
            int mq = qualityOfSituation(randomMemorySituation);
            int numberOfVisits = 0;

            bees[i] = new Bee(currStatus,
                    randomMemorySituation, mq, numberOfVisits);

            if (bees[i].measureOfQuality > bestMeasureOfQuality) { // <!!!!!!!!!!!
                this.bestMemorySituation = generateRandomSituation();
                this.bestMeasureOfQuality = qualityOfSituation(bestMemorySituation);
            }
        }
        bees[0].memorySituation = bestMemorySituation;
    }

    private Graph generateRandomSituation(){
        Graph newGraph = subGraph.clone();

        for(GraphNode node : newGraph.getNodesList()){
            int temp = (int) (Math.random() * subGraph.getNumberOfColours());
            node.setColor(temp);
        }
        return newGraph;
    }
    private Graph changeSituation(Graph oldGraph) {
        if(typeOfChangeColor == 1) return maxConflictsChange(oldGraph);
        if(typeOfChangeColor == 2) return localSearchChange(oldGraph);
        return defaultChange(oldGraph);
    }
    private int qualityOfSituation(Graph currentGraph) {
        int conflictsCount = 0;
        int maxConnections = currentGraph.getNumderOfConections();

        for (GraphNode node1 : currentGraph.getNodesList()) {
            int vertexColor = node1.getColor();

            List<Integer> neighbors = node1.getConnectionsList();
            for (Integer neighbor : neighbors) {
                int neighborColor = currentGraph.getNode(neighbor).getColor();
                if (vertexColor == neighborColor) {
                    conflictsCount++;
                }
            }
        }
        return maxConnections - conflictsCount/2;
    }

    public Graph solveGraphColoring() {
        numberOfSteps = 0;
        int maxQuality = subGraph.getNumderOfConections();
        int cycle = 0;
        while (cycle < this.maxNumberCycles) {
            for (int i = 0; i < totalNumberBees; ++i) {
                if (this.bees[i].status == 1)
                    ProcessActiveBee(i);
                else if (this.bees[i].status == 2)
                    ProcessScoutBee(i);
                else if (this.bees[i].status == 0)
                    ProcessInactiveBee(i);
            }

            if(bestMeasureOfQuality == maxQuality){numberOfSteps = cycle; break;}
            if (cycle % 1000 == 0){
                int quality = (int) (((double) bestMeasureOfQuality/maxQuality)/0.01);

                //System.out.println("step = " + cycle + "; quality = " + quality + "%  ("  + bestMeasureOfQuality + "/" + maxQuality + ")");
            }
            ++cycle;
        }
        for (GraphNode mainNode : subGraph.getNodesList()){
            int color = bestMemorySituation.getNode(mainNode.getIndex()).getColor();
            mainNode.setColor(color);
        }
        return bestMemorySituation;
    }

    private void ProcessActiveBee(int i) {
        Graph changedSituation = changeSituation(bees[i].memorySituation);
        int changedCliqueQuality = qualityOfSituation(changedSituation);
        double prob = Math.random();
        boolean memoryWasUpdated = false;
        boolean numberOfVisitsOverLimit = false;

        if (changedCliqueQuality < bees[i].measureOfQuality) {
            if (prob < probMistake) { // mistake
                ++bees[i].numberOfVisits;
                if (bees[i].numberOfVisits > maxNumberVisits)
                    numberOfVisitsOverLimit = true;
            }
            else { // No mistake
                bees[i].memorySituation = changedSituation.clone();
                if(permitToChangeBestSituation)bestMemorySituation = bees[i].memorySituation;
                bees[i].measureOfQuality = changedCliqueQuality;
                bees[i].numberOfVisits = 0;
                memoryWasUpdated = true;
            }
        }
        else {
            if (prob < probMistake) { // Mistake
                bees[i].memorySituation = changedSituation.clone();
                bees[i].measureOfQuality = changedCliqueQuality;
                bees[i].numberOfVisits = 0;
                memoryWasUpdated = true;
            }
            else { // No mistake
                ++bees[i].numberOfVisits;
                if (bees[i].numberOfVisits > maxNumberVisits)
                    numberOfVisitsOverLimit = true;
            }
        }

        if (numberOfVisitsOverLimit == true) {
            bees[i].status = 0;
            bees[i].numberOfVisits = 0;
            int x = (int) Math.random() * numberInactive;
            bees[indexesOfInactiveBees[x]].status = 1;
            indexesOfInactiveBees[x] = i;
        }
        else if (memoryWasUpdated == true) {
            if (bees[i].measureOfQuality > this.bestMeasureOfQuality) {
                this.bestMemorySituation = bees[i].memorySituation.clone();
                this.bestMeasureOfQuality = bees[i].measureOfQuality;
            }
            DoWaggleDance(i);
        }
        else {
            return;
        }
    }
    private void DoWaggleDance(int i) {
        for (int ii = 0; ii < numberInactive; ++ii) {
            int b = indexesOfInactiveBees[ii];
            if (bees[i].measureOfQuality > bees[b].measureOfQuality) {
                double p = Math.random();
                if (this.probPersuasion > p) {
                    bees[b].memorySituation = bees[i].memorySituation.clone();
                    bees[b].measureOfQuality = bees[i].measureOfQuality;
                }
            }
        }
    }
    private void ProcessScoutBee(int i) {
        Graph randomFoodSource = generateRandomSituation();
        int randomFoodSourceQuality =
                qualityOfSituation(randomFoodSource);
        if (randomFoodSourceQuality > bees[i].measureOfQuality) {
            bees[i].memorySituation = randomFoodSource;
            bees[i].measureOfQuality = randomFoodSourceQuality;
            if (bees[i].measureOfQuality > bestMeasureOfQuality) {
                this.bestMemorySituation = bees[i].memorySituation;
                this.bestMeasureOfQuality = bees[i].measureOfQuality;
            }
            DoWaggleDance(i);
        }
    }
    private void ProcessInactiveBee(int i) {
        return;
    }

    private Graph defaultChange(Graph oldGraph) {
        Random random = new Random();

        // We copy the graph to create a new graph with a changed vertex
        Graph newGraph = oldGraph.clone();

        // We choose a random vertex
        int numberOfVertices = newGraph.getNumberOfVertices();
        int randomVertex = random.nextInt(numberOfVertices);
        randomVertex = newGraph.getNodeByArrayIndex(randomVertex).getIndex();

        // We choose a random color
        int maxColors = oldGraph.getNumberOfColours();
        int currentColor = newGraph.getNode(randomVertex).getColor();
        int randomColor;
        do {
            randomColor = random.nextInt(maxColors);
        } while (currentColor == randomColor);

        newGraph.getNode(randomVertex).setColor(randomColor);

        return newGraph;
    }
    private Graph maxConflictsChange(Graph oldGraph) {
        Graph newGraph = oldGraph.clone();

        // We find the vertex with the largest number of conflicts
        int maxConflicts = -1;
        int conflictVertex = -1;

        for (GraphNode node1 : newGraph.getNodesList()){
            int conflicts = conflictCount(oldGraph,node1.getIndex());
            if (conflicts > maxConflicts) {
                maxConflicts = conflicts;
                conflictVertex = node1.getIndex();
            }
        }
        // Change the color of the vertex with the most conflicts to the first available color that does not violate the color constraints
        int currentColor = newGraph.getNode(conflictVertex).getColor();
        for (int newColor = 0; newColor < oldGraph.getNumberOfColours(); newColor++) {
            if (newColor != currentColor && !hasColorConflict(oldGraph, conflictVertex, newColor)) {
                newGraph.getNode(conflictVertex).setColor(newColor);
                break;
            }
        }
        return newGraph;
    }
    private int conflictCount(Graph oldGraph, int vertex) {
        GraphNode node = oldGraph.getNode(vertex);
        int conflicts = 0;
        if (node == null){
             conflicts = 4;
        }
        for (int neighbor : node.getConnectionsList()) {
            GraphNode neighborNode = oldGraph.getNode(neighbor);
            if (neighborNode.getColor() == node.getColor()) {
                conflicts++;
            }
        }
        return conflicts;
    }
    private boolean hasColorConflict(Graph oldGraph, int vertex, int color) {
        GraphNode node = oldGraph.getNode(vertex);
        for (int neighbor : node.getConnectionsList()) {
            GraphNode neighborNode = oldGraph.getNode(neighbor);
            if (neighborNode.getColor() == color) {
                return true;
            }
        }
        return false;
    }
    private Graph localSearchChange(Graph oldGraph) {
        Graph newGraph = oldGraph.clone();

        // We choose a random vertex
        Random random = new Random();
        int numberOfVertices = newGraph.getNumberOfVertices();
        int randomVertex1 = random.nextInt(numberOfVertices);

        // We apply a local search to choose the best color for this vertex, taking into account the colors of its neighbors
        GraphNode node1 = newGraph.getNodeByArrayIndex(randomVertex1);
        int bestColor = node1.getColor();
        int minConflicts = conflictCount(newGraph, node1.getIndex());
        for (int newColor = 0; newColor < oldGraph.getNumberOfColours(); newColor++) {
            if (newColor != bestColor) {
                newGraph.getNode(node1.getIndex()).setColor(newColor);
                int conflicts = conflictCount(newGraph, node1.getIndex());
                if (conflicts < minConflicts) {
                    minConflicts = conflicts;
                    bestColor = newColor;
                }
            }
        }

        // Change the color of the vertex to the best color found
        newGraph.getNode(node1.getIndex()).setColor(bestColor);

        return newGraph;
    }
}
