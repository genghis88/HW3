package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Bhattacharyya {
  
  private static class ExpandedTerm {
    private String term;
    private double probability;
    public String getTerm() {
      return term;
    }
    public void setTerm(String term) {
      this.term = term;
    }
    public double getProbability() {
      return probability;
    }
    public void setProbability(double probability) {
      this.probability = probability;
    }
  }
  
  public static void main(String[] args) {
    System.out.println(args[0]);
    System.out.println(args[1]);
    try(BufferedReader br1 = new BufferedReader(new FileReader(args[0]))) {
      Writer writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(args[1]), "utf-8"));
      String line1 = br1.readLine();
      HashMap<String,List<ExpandedTerm>> queryExpansions = 
          new HashMap<String,List<ExpandedTerm>>();
      while (line1 != null) {
        String query = line1.split(":")[0];
        String fileName = line1.split(":")[1];
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        List<ExpandedTerm> expandedTerms = new ArrayList<ExpandedTerm>();
        while (line != null) {
          String[] lineComps = line.split("\t");
          ExpandedTerm expTerm = new ExpandedTerm();
          expTerm.setTerm(lineComps[0]);
          expTerm.setProbability(Double.parseDouble(lineComps[1]));
          expandedTerms.add(expTerm);
          line = br.readLine();
        }
        queryExpansions.put(query,expandedTerms);
        line1 = br1.readLine();
      }
      List<String> queries = new ArrayList(queryExpansions.keySet());
      for(int i=0;i<queries.size();i++) {
        for(int j=i+1;j<queries.size();j++) {
          double coeff = 0.0;
          List<ExpandedTerm> queryITerms = queryExpansions.get(queries.get(i));
          List<ExpandedTerm> queryJTerms = queryExpansions.get(queries.get(j));
          Set<String> allTerms = new HashSet<String>();
          for(ExpandedTerm eTerm:queryITerms) {
            if(!allTerms.contains(eTerm.getTerm())) {
              coeff += Math.sqrt(a);
            }
          }
          for(ExpandedTerm eTerm:queryJTerms) {
            
          }
        }
      }
      writer.close();
    }
    catch(IOException ioe) {
      System.out.println("IO Exception");
      ioe.printStackTrace();
    }
  }
}