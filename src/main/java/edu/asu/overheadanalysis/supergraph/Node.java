package edu.asu.overheadanalysis.supergraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Node {
  HashMap<Color, Integer> adj;
  int pos;
  Color color;

  Node(int m) {
    pos = m;
    adj = new HashMap<>();
  }

  public Node connect(Color color) {
    adj.put(color, 1);
    return this;
  }

  public int getMapping(Color color) {
    Integer val = adj.get(color);
    return val == null ? 0 : val;
  }

  public void merge(HashMap<Color, Integer> newAdj) {
    adj.putAll(newAdj);
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  public void print() {
    System.out.print(color.toString() + ": ");
    for (Color color : adj.keySet()) {
      if (adj.get(color) == 1) System.out.print(color.toString() + ", ");
    }
    System.out.print("\n");
  }

  public void setRandom(ArrayList<Color> colorList) {
    Random random = new Random();
    boolean allZeros = true;
    for (int j = 0; j < pos; j++) {
      if (random.nextInt(2) == 1) {
        adj.put(colorList.get(j), 1);
        allZeros = false;
      } else {
        allZeros = true;
      }
    }

    if (allZeros && pos > 0) {
      int j = random.nextInt(pos);
      adj.put(colorList.get(j), 1);
    }
  }
}
