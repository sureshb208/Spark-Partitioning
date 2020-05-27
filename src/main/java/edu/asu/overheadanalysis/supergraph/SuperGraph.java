package edu.asu.overheadanalysis.supergraph;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SuperGraph {
  private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

  public static Graph kWayMerge(List<Graph> graphs)
      throws ExecutionException, InterruptedException {
    Collection<Future<Graph>> futures = new LinkedList<>();
    ArrayList<Graph> tempList = new ArrayList<>();

    while (graphs.size() > 1) {
      if (graphs.size() % 2 != 0) {
        tempList.add(graphs.remove(graphs.size() - 1));
      }

      for (int i = 0; i < graphs.size(); i += 2) {
        Graph a = graphs.get(i);
        Graph b = graphs.get(i + 1);
        futures.add(
            executorService.submit(
                () -> {
                  return Graph.mergeTwoGraphs(a, b);
                }));
      }

      for (Future<Graph> future : futures) {
        tempList.add(future.get());
      }

      graphs = tempList;
      tempList = new ArrayList<>();
      futures.clear();
    }
    return graphs.get(0);
  }

  public static int genRandDAG(int count, ArrayList<Graph> graphs) {
    Random random = new Random();
    int totalNodes = 0;
    for (int i = 0; i < count; i++) {
      int size = random.nextInt(9) + 2;
      Graph graph = new Graph();
      graph.setRandom(size);
      graph.sort();
      graphs.add(graph);
      totalNodes += size;
    }

    return totalNodes;
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    int graph_count = Integer.parseInt(args[0]);
    int batchSize = Integer.parseInt(args[1]);

    ArrayList<Graph> supergraphs = new ArrayList<>();
    ArrayList<Graph> graphs = new ArrayList<>();
    long totalNodes = 0;
    int totalTime = 0;
    int graphsLeft = graph_count;

    while (graphsLeft > 0) {
      int genCount = Math.min(graphsLeft, batchSize);
      System.out.println(
          new SimpleDateFormat("HH:mm:ss").format(new Date()) + " Creating batch size " + genCount);
      totalNodes += genRandDAG(genCount, graphs);
      System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " Batch created");
      graphsLeft -= genCount;

      System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " Starting merge");
      long startTime = System.currentTimeMillis();
      supergraphs.add(kWayMerge(graphs));
      long endTime = System.currentTimeMillis();
      totalTime += endTime - startTime;
      System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " Merge completed");
      graphs.clear();
    }

    if (supergraphs.size() > 1) {
      long startTime = System.currentTimeMillis();
      Graph finalGraph = kWayMerge(supergraphs);
      long endTime = System.currentTimeMillis();
      totalTime += endTime - startTime;
      finalGraph.print();
    } else {
      supergraphs.get(0).print();
    }

    long avgNodes = totalNodes / graph_count;
    executorService.shutdown();

    System.out.println(
        (new SimpleDateFormat("HH:mm:ss").format(new Date()))
            + " Merging of "
            + graph_count
            + " DAGS with avg nodes: "
            + avgNodes
            + " took "
            + totalTime
            + " milliseconds");
  }
}
