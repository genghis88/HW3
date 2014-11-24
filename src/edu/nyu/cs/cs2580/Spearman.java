package edu.nyu.cs.cs2580;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class Spearman {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    String pageRankFile = args[0];
    String numViewsFile = args[1];
    ObjectInputStream reader = new ObjectInputStream(new FileInputStream(pageRankFile));
    final ArrayList<Double> pageranks = (ArrayList<Double>) reader.readObject();
    reader.close();
    reader = new ObjectInputStream(new FileInputStream(numViewsFile));
    final ArrayList<Integer> numviews = (ArrayList<Integer>) reader.readObject();
    reader.close();
    
//    final ArrayList<Double> pageranks = new ArrayList<Double>();
//    final ArrayList<Double> numviews = new ArrayList<Double>();
//    pageranks.add(0.2);
//    pageranks.add(0.4);
//    pageranks.add(0.3);
//    pageranks.add(0.5);
//    pageranks.add(0.1);
//    //pageranks.add(0.2);
//    pageranks.add(0.6);
//    numviews.add(0.1);
//    numviews.add(0.2);
//    numviews.add(0.3);
//    numviews.add(0.4);
//    numviews.add(0.5);
//    //numviews.add(0.1);
//    numviews.add(0.6);
    
//    System.out.println(pageranks);
//    System.out.println(numviews);
    
    ArrayList<Integer> rankingOnPR = new ArrayList<Integer>();
    ArrayList<Integer> rankingOnNV = new ArrayList<Integer>();
    ArrayList<Integer> numbersOnPR = new ArrayList<Integer>();
    ArrayList<Integer> numbersOnNV = new ArrayList<Integer>();
    
    for(int i=0;i<pageranks.size();i++) {
      rankingOnPR.add(i);
      rankingOnNV.add(i);
      numbersOnPR.add(i);
      numbersOnNV.add(i);
    }
    
    Comparator<Integer> cmp1 = new Comparator<Integer>() {
      @Override
      public int compare(Integer arg0, Integer arg1) {
        // TODO Auto-generated method stub
        double diff = pageranks.get(arg0) - pageranks.get(arg1);
        if(diff > 0) {
          return -1;
        }
        else if(diff < 0) {
          return 1;
        }
        return 0;
        //return pageranks.get(arg0).compareTo(pageranks.get(arg1));
      }
    };
    
    Comparator<Integer> cmp2 = new Comparator<Integer>() {
      @Override
      public int compare(Integer arg0, Integer arg1) {
        // TODO Auto-generated method stub
        int diff = numviews.get(arg0) - numviews.get(arg1);
        if(diff > 0) {
          return -1;
        }
        else if(diff < 0) {
          return 1;
        }
        return 0;
      }
    };
    
    Collections.sort(rankingOnPR,cmp1);
    Collections.sort(rankingOnNV,cmp2);
    
//    System.out.println(rankingOnPR);
//    System.out.println(rankingOnNV);
    
    int n = pageranks.size();
    int s = 0;
    for(int rank:rankingOnPR) {
      s += rank;
    }
    double z = s * 1.0/ n;
    //System.out.println(z);
    
    //System.out.println(pageranks.size());
    //System.out.println(numviews.size());
    
    for(int i=0;i<pageranks.size();i++) {
      int pos1 = rankingOnPR.get(i);
      numbersOnPR.set(pos1, i+1);
      
      int pos2 = rankingOnNV.get(i);
      numbersOnNV.set(pos2, i+1);
    }
    
//    System.out.println(numbersOnPR);
//    System.out.println(numbersOnNV);
    
    double coefficient = 0.0;
    double numerator = 0.0;
    double denominatorPart1 = 0.0;
    double denominatorPart2 = 0.0;
    for(int i=0;i<pageranks.size();i++) {
      numerator += (numbersOnPR.get(i) - z) * (numbersOnNV.get(i) - z);
      denominatorPart1 += (numbersOnPR.get(i) - z) * (numbersOnPR.get(i) - z);
      denominatorPart2 += (numbersOnNV.get(i) - z) * (numbersOnNV.get(i) - z);
    }
    coefficient = numerator / Math.sqrt(denominatorPart1 * denominatorPart2);
    //System.out.println(numerator);
    //System.out.println(denominatorPart1);
    //System.out.println(denominatorPart2);
    System.out.println(coefficient);
  }
}
