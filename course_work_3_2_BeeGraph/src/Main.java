
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Graph parameters for testing
        int numVertices = 300;
        int numConnections = 900;
        int maxConnectionsNum = 9;
        int numOfColours = 12;

        // Creating a graph
        Graph graph = new Graph(numConnections, numVertices, maxConnectionsNum, numOfColours);
        graph.init();


        // Parameters of the final graph
        int numVerticesMainGraph = 300;
        int numConnectionsMainGraph = 900;
        int maxConnectionsNumMainGraph = 12;
        int numOfColoursMainGraph = 12;

        Graph mainGraph = new Graph(numConnectionsMainGraph, numVerticesMainGraph, maxConnectionsNumMainGraph, numOfColoursMainGraph);
        mainGraph.init();

        // Options for the best solution
        int numberOfThreads = 8;
        int totalNumberBees = 20;
        int numberInactive = 2;
        int numberScout = 4;
        int numberActive = 14;
        int maxNumberVisits = 1000;
        int maxNumberCycles = 20000;
        int typeOfChangeColor = 1;

        // Range of parameters
        int min_numberOfThreads = 2;
        int min_totalNumberBees = 20;
        int min_numberScout = 2;
        int min_numberActive = 10;
        int min_maxNumberVisits = 1000;
        int min_typeOfChangeColor = 0;

        int max_numberOfThreads = 8;
        int max_totalNumberBees = 100;
        int max_numberScout = 10;
        int max_numberActive = 80;
        int max_maxNumberVisits = 2000;
        int max_typeOfChangeColor = 2;
        ArrayList<BeeGarden> generation = new ArrayList<>();
        List<Map.Entry<Integer, Long>> timeList;
        Map.Entry<Integer, Long> bestResult;


        // totalNumberBees
        // ******************************************************************
        for(int i = min_totalNumberBees; i < max_totalNumberBees; i+=4){
            BeeGarden beeGarden1 = new BeeGarden(
                    (i - min_totalNumberBees)/4, graph,numberOfThreads,i,
                    numberInactive,(totalNumberBees-numberScout-i),numberScout, maxNumberVisits,
                    maxNumberCycles,typeOfChangeColor);

            generation.add(beeGarden1);
        }
        timeList = runTests(generation,4);
        printSortedResultsById(timeList);

        bestResult = getMinTimeAndId(timeList);
        BeeGarden b1 = generation.get(bestResult.getKey());
        System.out.println("**** test end ****");
        System.out.println("best = " + bestResult.getKey() + "; Total bees = " + b1.getTotalNumberBees() + " ----- " + bestResult.getValue());
        totalNumberBees = b1.getTotalNumberBees();
        generation.clear();
        timeList.clear();
        // numberActive
        // ******************************************************************
        for(int i = min_numberActive; i < totalNumberBees-min_numberScout-2; i++){
            BeeGarden beeGarden1 = new BeeGarden(
                    i-min_numberActive, graph,numberOfThreads,totalNumberBees,
                    numberInactive,i,(totalNumberBees-numberInactive-i), maxNumberVisits,
                    maxNumberCycles,typeOfChangeColor);

            generation.add(beeGarden1);
        }
        timeList = runTests(generation,4);
        printSortedResultsById(timeList);

        bestResult = getMinTimeAndId(timeList);
        BeeGarden b2 = generation.get(bestResult.getKey());
        System.out.println("**** test end ****");
        System.out.println("best = " + (bestResult.getKey()) + "; Active bees = " + b2.getNumberActive() + " ----- " + bestResult.getValue());
        numberActive = b2.getNumberActive();
        generation.clear();
        timeList.clear();


        // numberScout
        // ******************************************************************
        for(int i = min_numberScout; i < totalNumberBees-numberActive-2; i++){
            BeeGarden beeGarden1 = new BeeGarden(
                    i-min_numberScout, graph,numberOfThreads,totalNumberBees,
                    (totalNumberBees-numberActive-i),numberActive,i, maxNumberVisits,
                    maxNumberCycles,typeOfChangeColor);

            generation.add(beeGarden1);
        }
        timeList = runTests(generation,4);
        printSortedResultsById(timeList);

        bestResult = getMinTimeAndId(timeList);
        BeeGarden b3 = generation.get(bestResult.getKey());
        System.out.println("**** test end ****");
        System.out.println("best = " + (bestResult.getKey()+2) + "; Scout bees = " + b3.getNumberScout() + " ----- " + bestResult.getValue());
        numberScout = b3.getNumberScout();
        numberInactive = totalNumberBees-numberActive-numberScout;
        generation.clear();
        timeList.clear();

        // numberOfThreads
        // ******************************************************************
        for(int i = min_numberOfThreads; i <= max_numberOfThreads; i++){
            BeeGarden beeGarden1 = new BeeGarden(
                    i-min_numberOfThreads, graph,i,totalNumberBees,
                    numberInactive,numberActive,numberScout, maxNumberVisits,
                    maxNumberCycles,typeOfChangeColor);

            generation.add(beeGarden1);
        }
        timeList = runTests(generation,4);
        printSortedResultsById(timeList);

        bestResult = getMinTimeAndId(timeList);
        BeeGarden b4 = generation.get(bestResult.getKey());
        System.out.println("**** test end ****");
        System.out.println("best = " + (bestResult.getKey()+2) + "; Threads = " + b4.getNumberOfThreads() + " ----- " + bestResult.getValue());
        numberOfThreads = b4.getNumberOfThreads();
        generation.clear();
        timeList.clear();


        // maxNumberVisits
        // ******************************************************************
        for(int i = min_maxNumberVisits; i < max_maxNumberVisits; i+=50){
            BeeGarden beeGarden1 = new BeeGarden(
                    (i-min_maxNumberVisits)/50, graph,numberOfThreads,totalNumberBees,
                    numberInactive,numberActive,numberScout, i,
                    maxNumberCycles,typeOfChangeColor);

            generation.add(beeGarden1);
        }
        timeList = runTests(generation,4);
        printSortedResultsById(timeList);

        bestResult = getMinTimeAndId(timeList);
        BeeGarden b5 = generation.get(bestResult.getKey());
        System.out.println("**** test end ****");
        System.out.println("best test id = " + bestResult.getKey() + "; max number of visits = " + b5.getMaxNumberVisits() + " ----- " + bestResult.getValue());
        maxNumberVisits = b5.getMaxNumberVisits();
        generation.clear();
        timeList.clear();
        // typeOfChangeColor
        // ******************************************************************
        for(int i = min_typeOfChangeColor; i <= max_typeOfChangeColor; i++){
            BeeGarden beeGarden1 = new BeeGarden(
                    i-min_typeOfChangeColor, graph,numberOfThreads,totalNumberBees,
                    numberInactive,numberActive,numberScout, maxNumberVisits,
                    maxNumberCycles,i);

            generation.add(beeGarden1);
        }
        timeList = runTests(generation,4);
        printSortedResultsById(timeList);

        bestResult = getMinTimeAndId(timeList);
        BeeGarden b6 = generation.get(bestResult.getKey());
        System.out.println("**** test end ****");
        System.out.println("best test id = " + bestResult.getKey() + "; type of changing color = " + b6.getTypeOfChangeColor() + " ----- " + bestResult.getValue());
        typeOfChangeColor = b6.getTypeOfChangeColor();
        generation.clear();
        timeList.clear();

        // ******************************************************************
        // ******************************************************************
        // ******************************************************************
        BeeGarden bestBeeGarden = new BeeGarden(
                    0, mainGraph,numberOfThreads,totalNumberBees,
                    numberInactive,numberActive,numberScout, maxNumberVisits,
                    maxNumberCycles,typeOfChangeColor);
        System.out.println("---------------------------------------------------------");
        System.out.println("**** final test ****");
        System.out.println("Parameters:");

        System.out.println("numberOfThreads --- " + numberOfThreads  + ";");
        System.out.println("totalNumberBees --- " + totalNumberBees  + ";");
        System.out.println("numberInactive ---- " + numberInactive  + ";");
        System.out.println("numberScout ------- " + numberScout   + ";");
        System.out.println("numberActive ------ " + numberActive  + ";");
        System.out.println("maxNumberVisits --- " + maxNumberVisits   + ";");
        System.out.println("maxNumberCycles --- " + maxNumberCycles   + ";");
        System.out.println("typeOfChangeColor - " + typeOfChangeColor    + ";");

        long timeOfSolving = runTestWithParameters(bestBeeGarden);

        System.out.println("time = " + timeOfSolving + " -- solved\n");
        mainGraph.clearColor();

        System.out.println("Linear solve start");
        Hive hv1 = new Hive(totalNumberBees,numberInactive,numberActive,numberScout,maxNumberVisits,maxNumberCycles,typeOfChangeColor,mainGraph);
        long startTimeHv1 = System.currentTimeMillis();
        hv1.solveGraphColoring();
        long endTimeHv1 = System.currentTimeMillis();
        System.out.println("Solved linear. time = " + (endTimeHv1-startTimeHv1) + ";");


    }

    private static long runTestWithParameters(BeeGarden beeGarden) {
        long totalTime = 0;
        for(int i = 0; i < 5; i++) {
            long startTime1 = System.currentTimeMillis();
            beeGarden.solveGraphColoring();
            long endTime1 = System.currentTimeMillis();
            totalTime = totalTime + (endTime1 -startTime1);
        }


        return totalTime/5;
    }

    private static List<Map.Entry<Integer, Long>> runTests(List<BeeGarden> beeGardens, int numberOfThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Map.Entry<Integer, Long>>> testFutures = new ArrayList<>();
        List<Map.Entry<Integer, Long>> testResults = new ArrayList<>();

        for (BeeGarden beeGarden : beeGardens) {
            Future<Map.Entry<Integer, Long>> testResult = executor.submit(() -> {
                long duration = runTestWithParameters(beeGarden);
                return new AbstractMap.SimpleEntry<>(beeGarden.getId(), duration);
            });
            testFutures.add(testResult);
        }

        for (Future<Map.Entry<Integer, Long>> result : testFutures) {
            try {
                testResults.add(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        return testResults;
    }
    private static Map.Entry<Integer, Long> getMinTimeAndId(List<Map.Entry<Integer, Long>> testResults) {
        Optional<Map.Entry<Integer, Long>> minEntry = testResults.stream()
                .min(Comparator.comparing(Map.Entry::getValue));

        if (minEntry.isPresent()) {
            return minEntry.get();
        } else {
            throw new IllegalStateException("Error: The result list is empty");
        }
    }
    private static void printSortedResultsById(List<Map.Entry<Integer, Long>> testResults) {
        testResults.stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEachOrdered(entry -> System.out.println("id = " + entry.getKey() + "; time = " + entry.getValue()));
    }
}