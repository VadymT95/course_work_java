
import java.util.ArrayList;
import java.util.HashMap;

public class GraphNode {
    private int index = 0;
    private int color = 0;

    ArrayList<Integer> connectionsList = new ArrayList<>();

    public GraphNode(int index){
        this.index = index;
        this.color = 0;
    }

    public void addConection(int indexOfNewVertex){
        this.connectionsList.add(indexOfNewVertex);
    }
    public void removeConection(int index){
        for(int i = 0; i < connectionsList.size(); i++){
            if(connectionsList.get(i) == index) connectionsList.remove(i);
        }
    }
    public GraphNode clone(){
        GraphNode newNode = new GraphNode(this.index);
        newNode.color = this.color;
        ArrayList<Integer> newList = new ArrayList<>();
        for(Integer i : connectionsList){
            newList.add(i);
        }
        newNode.setConnectionsList(newList);
        return newNode;
    }

    public boolean isConectionExist(int index){
        for(int i = 0; i < connectionsList.size(); i++){
            if(connectionsList.get(i) == index) return true;
        }
        return false;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<Integer> getConnectionsList() {
        return connectionsList;
    }
    public void setConnectionsList(ArrayList<Integer> connectionsList) {
        this.connectionsList = connectionsList;
    }
}