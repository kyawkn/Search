import java.util.*;


/**
 * Graph.java
 * the Graph class builds the graph with neighboring cities and their distances
 * that can be used by all operations
 * author @ Kyaw Khant Nyar (kxk3035@rit.edu)
 */
public class Graph {


    public List<LinkedList<String>> neighbors;
    public ArrayList<String> cities;

    // Constructor

    Graph(int cityCount, ArrayList<String> cities) {
        this.neighbors = new ArrayList<>();
        this.cities = cities;
        for(int i = 0; i < cityCount; i++) {
            this.neighbors.add(i, new LinkedList<>());
        }
    }

    public void connectEdge(String cityA, String cityB) {
        int cityAID = cities.indexOf(cityA);
        int cityBID = cities.indexOf(cityB);
        LinkedList<String> neighborA = this.neighbors.get(cityAID);
        LinkedList<String> neighborB = this.neighbors.get(cityBID);
        neighborA.add(cityB);
        neighborB.add(cityA);
        this.neighbors.set(cityAID, neighborA);
        this.neighbors.set(cityBID, neighborB);
    }


    public boolean isNeighbor(String cityA, String cityB) {

        int cityAID = cities.indexOf(cityA);
        LinkedList<String> neighborA = this.neighbors.get(cityAID);
        return neighborA.contains(cityB);
    }


}
