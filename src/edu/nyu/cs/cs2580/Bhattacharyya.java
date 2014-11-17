package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bhattacharyya {
  public static void main(String[] args) {
    System.out.println(args[0]);
    System.out.println(args[1]);
    try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
      String line = br.readLine();
      List<String> words = new ArrayList<String>();
      List<Double> prfs = new ArrayList<Double>();
      while (line != null) {
        String[] lineComps = line.split("\t");
        words.add(lineComps[0]);
        prfs.add(Double.parseDouble(lineComps[1]));
        line = br.readLine();
      }
      List<Double> coeffs = new ArrayList<Double>();
      Writer writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(args[1]), "utf-8"));
      for(int i=0;i<words.size();i++) {
        for(int j=i+1;j<words.size();j++) {
          Double coeff = Math.sqrt(prfs.get(i) * prfs.get(j));
          coeffs.add(coeff);
          writer.write(words.get(i)+"\t"+words.get(j)+"\t"+coeff+"\r\n");
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
