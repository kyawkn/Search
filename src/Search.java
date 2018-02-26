import java.io.*;
import java.util.*;

/**
 * the Search program that contains, BFS, DFS and A* search
 * the program build the graph by using the data from city.dat
 * and edge.dat. And find the shortest path from the
 */

public class Search {

    // cities values

    ArrayList<String> cities;
    ArrayList<Double> latitudes;
    ArrayList<Double> longitudes;
    Graph graph;
    int cityCount;


    /*
       Constructor
     */
    public Search() {

        this.cities = new ArrayList<>();
        this.latitudes = new ArrayList<>();
        this.longitudes = new ArrayList<>();
        this.cityCount = 0;
    }


    /**
     * load the cities from the city.dat into the appropriate data structures
     */
    private void loadCities() {

        File file = new File("city.dat");
        try {
            Scanner sn = new Scanner(file);
            while (sn.hasNextLine()) {

                String line = sn.nextLine();
                String[] words = line.split("\\s+");

                // add values at cityID
                this.cities.add(cityCount, words[0]);
                this.latitudes.add(cityCount, Double.parseDouble(words[2]));
                this.longitudes.add(cityCount, Double.parseDouble(words[3]));
                this.cityCount ++;

            }

        } catch (Exception ex) {
            errorMessage(2, file.getName());
        }

    }

    /**
     * BuildGraph loads the edge.dat and build the graph
     */
    private void buildGraph() {

        // construct graph
        this.graph = new Graph(cityCount, cities);

        File file = new File("edge.dat");
        try {
            Scanner sn = new Scanner(file);

            while (sn.hasNextLine()) {
                String line = sn.nextLine();
                String[] words = line.split("\\s+");
                // load the edges
                this.graph.connectEdge(words[0], words[1]);
            }

        } catch (Exception ex) {
            errorMessage(2, file.getName());
        }
    }

    /**
     * DFS returns the shortest path list by traversing the children nodes first
     * @param start start city
     * @param end end city
     * @return shortest path list
     */
    private LinkedList<String> DFS(String start, String end) {

        // data structure
        Stack<String> dfsStack = new Stack<>(); // use Stack instead a Queue like BFS
        LinkedList<String> visited = new LinkedList<>();
        ArrayList<String> preds = new ArrayList<>();

        // initiate
        dfsStack.add(start);
        visited.add(start);
        for (int i = 0; i < cityCount; i++)
            preds.add(i, null);


        // DFS
        while (dfsStack.size() != 0) {
            String parent = dfsStack.pop();

            if(parent.equals(end)) return buildPath(start, end, preds);

            else {
                // get neighbors
                LinkedList<String> children =
                        this.graph.neighbors.get(this.cities.indexOf(parent));
                // sort the list by reverse order
                Collections.sort(children);
                Collections.reverse(children);

                if (children.contains(end)) {
                    int endID = this.cities.indexOf(end);
                    preds.set(endID, parent);
                    return buildPath(start, end, preds);

                } else {
                    for (String child: children) {

                        if (!visited.contains(child)) {
                            int childID = this.cities.indexOf(child);
                            preds.set(childID, parent);
                            dfsStack.add(child);
                            visited.add(child);
                        }
                    }
                }
            }

        }

        return buildPath(start, end, preds);

    }

    /**
     * getLowest returns the city with the lowest fvalues
     * @param visited visited city list
     * @param fcosts list of the final costs of cities
     * @return city name
     */
    private String getLowest(LinkedList<String> visited, ArrayList<Double> fcosts){

        Double lowest = Double.MAX_VALUE;
        String lowestCity = null;

        for(String city: visited) {
            if(fcosts.get(this.cities.indexOf(city)) < lowest) {
                lowest = fcosts.get(this.cities.indexOf(city));
                lowestCity = city;
            }
        }

        return lowestCity;
    }


    /**
     * A* search gives the shortest from the start to the end using heuristics
     * @param start start city
     * @param end end city
     * @return the shortest path list
     */
    private LinkedList<String> AStar(String start, String end) {

        // data structure
        ArrayList<String> preds = new ArrayList<>();
        LinkedList<String> open = new LinkedList<>();
        LinkedList<String> closed = new LinkedList<>();
        ArrayList<Double> gCosts = new ArrayList<>();
        ArrayList<Double> fCosts = new ArrayList<>();

        // init
        open.add(start);
        for (int i = 0; i < cityCount; i++) {
            gCosts.add(i, Double.MAX_VALUE);
            fCosts.add(i, 0.0);
            preds.add(i, null);
        }

        // initial costs
        gCosts.set(this.cities.indexOf(start),0.0);
        Double fStartCost = calcDistance(start, end);
        fCosts.set(this.cities.indexOf(start), fStartCost);


        while (open.size() != 0) {
            // choose the shortest from the list
            String parent = getLowest(open, fCosts);

            if(parent.equals(end)) return buildPath(start, end, preds);

            int parentID = cities.indexOf(parent);

            open.remove(parent);
            closed.add(parent);

            LinkedList<String> children = this.graph.neighbors.get(parentID);

            if(children.contains(end)) {
                preds.set(this.cities.indexOf(end), parent);
                return buildPath(start, end, preds);
            }

            // check the neighbors
            for(String child: children) {

                int childID = cities.indexOf(child);

                if(closed.contains(child)) continue;

                if (!open.contains(child)) open.add(child);

                Double tenG = gCosts.get(parentID) + calcDistance(parent, child);

                if(tenG >= gCosts.get(childID)) continue; // greater than? not a good path

                // this is the best path
                preds.set(childID, parent);
                gCosts.set(childID, tenG);
                fCosts.set(childID, gCosts.get(childID) + calcDistance(child, end));

            }


        }

        return null;
    }

    /**
     * BFS search finds the shortest path by visiting the the neighbors first
     * @param start start city
     * @param end end city
     * @return shortest path list
     */
    private LinkedList<String> BFS(String start, String end) {

        // data structures
        LinkedList<String> bfsQ = new LinkedList<>();
        LinkedList<String> visited = new LinkedList<>();
        ArrayList<String> preds = new ArrayList<>();

        // init
        bfsQ.add(start);
        visited.add(start);

        // init the predecessor list
        for (int i = 0; i < cityCount; i++)
            preds.add(i, null);

        while (bfsQ.size() != 0) {

            String parent = bfsQ.poll();

            if(parent.equals(end)) return buildPath(start, end, preds);

            else {
                // get the neighbors nodes
                LinkedList<String> children =
                        this.graph.neighbors.get(this.cities.indexOf(parent));
                Collections.sort(children);
                // if found
                if (children.contains(end)) {
                    preds.set(this.cities.indexOf(end), parent);
                    return buildPath(start, end, preds);
                }

                // visit the neighbors
                for (String child: children) {
                    if (!visited.contains(child)) {
                        visited.add(child);
                        int childID = this.cities.indexOf(child);
                        preds.set(childID, parent);
                        bfsQ.add(child);
                    }
                }
            }

        }

        return buildPath(start, end, preds);

    }

    /**
     * buildPath builds the path from the predecessor list
     * @param start start city
     * @param end end city
     * @param preds the predecessor list
     * @return shortest path list
     */
    public LinkedList<String> buildPath(String start, String end, ArrayList<String> preds) {

        LinkedList<String> path = new LinkedList<>();
        String cur = end; // start from the back
        // build the list
        while ( preds.get(cities.indexOf(cur)) != null) {
            path.add(cur);
            cur = preds.get(cities.indexOf(cur));
        }
        path.add(start);
        // the list is in the reversed order so fix it
        Collections.reverse(path);
        return path;
    }


    public void displayAnswer (String searchType, LinkedList<String> cityPath) {

        // print header
        System.out.printf("%s Search Results: %n", searchType);

        Double distance = 0.0;

        // print answers
        for (int i = 1; i < cityPath.size(); i++) {
            String cityA = cityPath.get(i -1);
            String cityB = cityPath.get(i);

            System.out.println(cityA);
            distance += calcDistance(cityA, cityB);

        }
        long dist = Math.round(distance);
        // print the destination
        System.out.println(cityPath.getLast());
        System.out.printf("That took %d hops to find.%n", cityPath.size()-1);
        System.out.printf("Total distance = %d miles.%n", dist);


    }



    public Double calcDistance (String cityA, String cityB) {

        // cityA and cityB's values
        int cityAID = this.cities.indexOf(cityA);
        int cityBID = this.cities.indexOf(cityB);
        Double latA = this.latitudes.get(cityAID) * 100.0 / 100.0;
        Double latB = this.latitudes.get(cityBID) * 100.0 / 100.0;
        Double longA = this.longitudes.get(cityAID) * 100.0 / 100.0;
        Double longB = this.longitudes.get(cityBID) * 100.0 / 100.0;
        // calculation
        return Math.sqrt( (latA-latB)*(latA-latB) + (longA-longB)*(longA-longB) ) * 100;

    }

    public static void main(String[] args) {

        if(args.length !=2) errorMessage(1, null);

        String start = null;
        String end = null;

        // check how the user wants the input
        if(args[0].equals("-")) {
           Scanner sn = new Scanner(System.in);
           start = sn.next();
           end = sn.next();
        } else {
            File inputFile = new File(args[0]);
            try {
                Scanner sn = new Scanner(inputFile);
                start = sn.next();
                end = sn.next();

            } catch (Exception ex) {
                errorMessage(2, inputFile.getName());
            }
        }


        Search sc = new Search();
        sc.loadCities();
        sc.buildGraph();



        if(sc.cities.indexOf(start) == - 1 )
            errorMessage(3, start);
        if(sc.cities.indexOf(end) == -1)
            errorMessage(3, end);

        // calculate
        LinkedList<String> bfsList = sc.BFS(start, end);
        LinkedList<String> dfsList = sc.DFS(start, end);
        LinkedList<String> aStarList = sc.AStar(start, end);

        // check how the user wants the output
        if(!args[1].equals("-")) {
            File outFile = new File(args[1]);
            try {
                PrintStream ps = new PrintStream(outFile);
                System.setOut(ps);
            } catch (Exception ex) {
                errorMessage(2, outFile.getName());
            }

        }
        // output the results
        System.out.println();
        sc.displayAnswer("Breadth-First", bfsList);
        System.out.print("\n\n");
        sc.displayAnswer("Depth-First", dfsList);
        System.out.print("\n\n");
        sc.displayAnswer("A*", aStarList);
        System.out.print("\n");


    }

    /*
       output the error message to System.err and exit the program
     */
    private static void errorMessage(int errno, String err) {

        if(errno == 1 ) {
            System.err.println("Usage: java Search inputFile outputFile");
        } else if(errno == 2){
            System.err.println("File not found: " + err);
        } else {
            System.err.println("No Such city: " + err);
        }
        System.exit(1);
    }


}
