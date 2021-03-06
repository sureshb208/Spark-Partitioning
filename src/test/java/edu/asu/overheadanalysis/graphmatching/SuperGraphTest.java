package edu.asu.overheadanalysis.graphmatching;

import edu.asu.overheadanalysis.supergraph.Color;
import edu.asu.overheadanalysis.supergraph.Graph;
import edu.asu.overheadanalysis.supergraph.Node;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;

public class SuperGraphTest {
  @Test
  public void testRandomGraphDAG() {
    Graph g = new Graph();
    g.setRandom(9);

    for (int i = 0; i < 9; i++) {
      for (int j = i; j < 9; j++) {
        Assert.assertEquals(0, g.getMapping(i, j));
      }
    }
  }

  @Test
  public void testRandomGraphColors() {
    Graph g = new Graph();
    g.setRandom(9);

    HashSet<Color> colors = new HashSet<>();
    for (int i = 0; i < g.size(); i++) {
      colors.add(g.get(i).getColor());
    }
    Assert.assertEquals(colors.size(), g.size());
    Assert.assertTrue(g.size() <= Color.values().length);
  }

  @Test
  public void testGraphSort() {
    Color[] order = {Color.BLUE, Color.PINK, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.BROWN};

    Graph g1 = new Graph();
    g1.addNode(Color.BROWN);
    g1.addNode(Color.YELLOW).connect(Color.BROWN);
    g1.addNode(Color.BLUE).connect(Color.GREEN).connect(Color.ORANGE);
    g1.addNode(Color.ORANGE).connect(Color.YELLOW);
    g1.addNode(Color.PINK).connect(Color.BROWN).connect(Color.ORANGE).connect(Color.BLUE);
    g1.addNode(Color.GREEN).connect(Color.BROWN);

    boolean comp = true;
    for (int i = 0; i < g1.size(); i++) {
      Node node = g1.get(i);
      comp &= node.getColor().equals(order[i]);
    }
    Assert.assertFalse(comp);

    g1.sort();

    comp = true;
    for (int i = 0; i < g1.size(); i++) {
      Node node = g1.get(i);
      comp &= node.getColor().equals(order[i]);
    }
    Assert.assertTrue(comp);
  }

  @Test
  public void testGraphMappingAccess() {
    Graph g1 = new Graph();
    g1.addNode(Color.BLUE).connect(Color.GREEN).connect(Color.ORANGE);
    g1.addNode(Color.PINK).connect(Color.BROWN).connect(Color.ORANGE).connect(Color.BLUE);
    g1.addNode(Color.YELLOW).connect(Color.BROWN);
    g1.addNode(Color.GREEN).connect(Color.BROWN);
    g1.addNode(Color.ORANGE).connect(Color.YELLOW);
    g1.addNode(Color.BROWN);

    int[][] verifyMapping = {
      {0, 0, 0, 1, 1, 0},
      {1, 0, 0, 0, 1, 1},
      {0, 0, 0, 0, 0, 1},
      {0, 0, 0, 0, 0, 1},
      {0, 0, 1, 0, 0, 0},
      {0, 0, 0, 0, 0, 0}
    };

    for (int i = 0; i < g1.size(); i++) {
      for (int j = 0; j < i; j++) {
        Assert.assertEquals(verifyMapping[i][j], g1.getMapping(i, j));
      }
    }
  }

  @Test
  public void testGraphMerge() {
    Graph g1 = new Graph();
    g1.addNode(Color.BLUE).connect(Color.GREEN).connect(Color.ORANGE);
    g1.addNode(Color.PINK).connect(Color.BROWN).connect(Color.ORANGE).connect(Color.BLUE);
    g1.addNode(Color.YELLOW).connect(Color.BROWN);
    g1.addNode(Color.GREEN).connect(Color.BROWN);
    g1.addNode(Color.ORANGE).connect(Color.YELLOW);
    g1.addNode(Color.BROWN);

    Graph g2 = new Graph();
    g2.addNode(Color.BLUE).connect(Color.GOLD).connect(Color.GREEN).connect(Color.ORANGE);
    g2.addNode(Color.PINK).connect(Color.BLUE).connect(Color.BROWN).connect(Color.ORANGE);
    g2.addNode(Color.YELLOW).connect(Color.BROWN);
    g2.addNode(Color.GREEN).connect(Color.GOLD).connect(Color.BROWN);
    g2.addNode(Color.GOLD);
    g2.addNode(Color.ORANGE).connect(Color.YELLOW);
    g2.addNode(Color.BROWN);

    Graph g3 = new Graph();
    g3.addNode(Color.BLUE).connect(Color.GOLD).connect(Color.GREEN).connect(Color.ORANGE);
    g3.addNode(Color.PINK).connect(Color.BLUE).connect(Color.BROWN).connect(Color.ORANGE);
    g3.addNode(Color.YELLOW).connect(Color.BROWN);
    g3.addNode(Color.GREEN).connect(Color.GOLD).connect(Color.BROWN);
    g3.addNode(Color.GOLD);
    g3.addNode(Color.ORANGE).connect(Color.YELLOW);
    g3.addNode(Color.BROWN);

    Graph merged = Graph.mergeTwoGraphs(g1, g2);

    Assert.assertEquals(g3.size(), merged.size());
    for (int i = 0; i < g3.size(); i++) {
      for (int j = 0; j < g3.size(); j++) {
        Assert.assertEquals(g3.getMapping(i, j), merged.getMapping(i, j));
      }
    }
  }
}
