
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Graph {
    private int numderOfConections = 0;
    private int numberOfVertices = 0;
    private int maxNumberOfConections = 0;
    private int numberOfColours = 0;
    private ArrayList<GraphNode> nodesList;

    public Graph(int numderOfConections, int numberOfVertices, int maxNumberOfConections, int numberOfColours){
        this.numberOfVertices = numberOfVertices;
        this.numderOfConections = numderOfConections;
        this.maxNumberOfConections = maxNumberOfConections;
        this.numberOfColours= numberOfColours;
        this.nodesList = new ArrayList<>();
    }
    public Graph(ArrayList<GraphNode> subgraphNodes, int numderOfConections,int maxNumberOfConections,int numberOfColours){
        this.numberOfVertices = subgraphNodes.size();
        this.numderOfConections = numderOfConections;
        this.maxNumberOfConections = maxNumberOfConections;
        this.numberOfColours= numberOfColours;
        this.nodesList = subgraphNodes;
    }

    public Graph clone(){
        Graph newGraph = new Graph(this.numderOfConections, this.numberOfVertices, this.maxNumberOfConections, this.numberOfColours);
        ArrayList<GraphNode> newList = new ArrayList<>();
        for (GraphNode node : this.nodesList){
            newList.add(node.clone());
        }
        newGraph.setNodesList(newList);
        return newGraph;
    }
    public void init(){
        for(int i = 0; i < numberOfVertices; i++){
            nodesList.add(new GraphNode(i));  // initialization of nodes
        }
        for(int i = 0; i < numberOfVertices-1; i++){   // filling each node with at least 1 link
            addNewConection(i, i+1);
        }
        addNewConection(numberOfVertices-1, 0);
        int skip = 0;
        for(int i = 0; i < numderOfConections - numberOfVertices + skip; i++){   // the main generation
            int randomNumber = (int) (Math.random() * numberOfVertices);
            if(createRandomConection(randomNumber) == false){
                skip++;
            }
        }
    }
    public void addNewConection(int index1, int index2){
        GraphNode currentNode1 = nodesList.get(index1);
        GraphNode currentNode2 = nodesList.get(index2);
        currentNode1.addConection(index2);
        currentNode2.addConection(index1);
    }
    public boolean createRandomConection(int currentNodeIndex){
        GraphNode currentNode = nodesList.get(currentNodeIndex);
        if(currentNode.getConnectionsList().size() >= maxNumberOfConections) return false;//throw new IllegalArgumentException("Node is full!");

        int randomVertex = 0;
        GraphNode secondNode;
        while (true) {
            while (true) {
                randomVertex = (int) (Math.random() * numberOfVertices);
                secondNode = nodesList.get(randomVertex);
                if(randomVertex == currentNodeIndex) continue;
                if (secondNode.getConnectionsList().size() < maxNumberOfConections) break;
            }
            if(secondNode.isConectionExist(currentNodeIndex) == false && currentNode.isConectionExist(randomVertex) == false) break;
        }
        addNewConection(currentNodeIndex,randomVertex);
        return true;
    }
    public void print(){
        for(int i = 0; i < numberOfVertices; i++){
            GraphNode currentNode = nodesList.get(i);
            System.out.print(i  + " (color = " + currentNode.getColor() + ") -- ");
            for (int nodeIndex : currentNode.getConnectionsList()){
                System.out.print(nodeIndex +"; ");
            }
            System.out.println("");
        }
    }
    public void clearColor(){
        for(int i = 0; i < numberOfVertices; i++){
            GraphNode currentNode = nodesList.get(i);
                currentNode.setColor(0);
        }
    }

    public GraphNode getNode(int index){
        for(GraphNode node : nodesList) {
            if (node.getIndex() == index) return node;
        }
        return null;
    }
    public GraphNode getNodeByArrayIndex(int index){
        return nodesList.get(index);
    }
    public int counterOfColorsMistakes() {
        int conflictsCount = 0;
        for (GraphNode node1 : nodesList) {
            int vertexColor = node1.getColor();
            List<Integer> neighbors = node1.getConnectionsList();
            for (Integer neighbor : neighbors) {
                int neighborColor = getNode(neighbor).getColor();
                if (vertexColor == neighborColor) {
                    conflictsCount++;
                }
            }
        }
        return conflictsCount/2;
    }

    public int getNumderOfConections() {
        return numderOfConections;
    }
    public void setNumderOfConections(int numderOfConections) {
        this.numderOfConections = numderOfConections;
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }
    public void setNumberOfVertices(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
    }

    public int getMaxNumberOfConections() {
        return maxNumberOfConections;
    }
    public void setMaxNumberOfConections(int maxNumberOfConections) {
        this.maxNumberOfConections = maxNumberOfConections;
    }

    public int getNumberOfColours() {
        return numberOfColours;
    }
    public void setNumberOfColours(int numberOfColours) {
        this.numberOfColours = numberOfColours;
    }

    public ArrayList<GraphNode> getNodesList() {
        return nodesList;
    }
    public void setNodesList(ArrayList<GraphNode> nodesList) {
        this.nodesList = nodesList;
    }

}
