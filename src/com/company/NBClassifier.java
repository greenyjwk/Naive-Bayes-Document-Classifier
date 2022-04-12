package com.company;

import java.io.*;
import java.util.*;

/**
 * ISTE-612-2215 Lab #4
 * JI Woong Kim
 * April 13
 */
public class NBClassifier {

    private String[] trainingDocs;         //training data (1)
    private HashMap<String, Integer> trainingClasses; //training class values (2)
    private int numClasses;                //2 (0 & 1)
    private int[] classDocCounts;          //number of docs per class (4)
    private String[] classStrings;         //concatenated string for a given class (5)
    private int[] classTokenCounts;        //total number of tokens per class (6)
    private HashMap<String, Double>[] condProb; //term conditional prob (7)
    private HashSet<String> vocabulary;    //entire vocabulary (8)

    /**
     * Build a Naive Bayes classifier using a training document set
     *
     * @param trainDataFolder the training document folder
     */
    public NBClassifier(String trainDataFolder) {

        preprocess(trainDataFolder);

        //Create the condProb intance
        for (int i = 0; i < numClasses; i++) condProb[i] = new HashMap<String, Double>();

        // Create ClassTokensCounts (Total number of tokens per class)
        // Create classStrings (concatenated string for a given class)
        for (int i = 0; i < numClasses; i++) {
            String[] tokens = classStrings[i].split("\\s+");
            classTokenCounts[i] = tokens.length;
            for (String token : tokens) {
                vocabulary.add(token);
                if (condProb[i].containsKey(token)) {
                    double count = condProb[i].get(token); //Create the Hash Table that has key as token and value as frequeny
                    condProb[i].put(token, count + 1);
                } else condProb[i].put(token, 1.0);
            }
        }


        for (int i = 0; i < numClasses; i++) {
            Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
            int vSize = vocabulary.size();
            while (iterator.hasNext()) {
                Map.Entry<String, Double> entry = iterator.next();
                String token = entry.getKey();
                Double count = entry.getValue();
//                Double prob = (count + 1) / (classTokenCounts[i] + vSize); // This is the original version -> denominator would be wrong
                Double prob = (count + 1) / ( count + vSize);
                condProb[i].put(token, prob);
            }
        }

        System.out.println("This is class 1 doccount " + classDocCounts[0]);
        System.out.println("This is class 2 doccount " + classDocCounts[1]);
        System.out.println("This is count " + trainingDocs.length);
        System.out.println("This is the number of strings in neg directory " + classStrings[0].length());
        System.out.println("This is the number of strings in pos directory " + classStrings[1].length());
        System.out.println("This is trainingClasses " + trainingClasses.entrySet());
        System.out.println("This is the number of classes " + numClasses);
        for (int i = 0; i < classTokenCounts.length; i++) System.out.println("This is total number of tokens per class " + classTokenCounts[i]);
        System.out.println("This is the entire number of words that appear " + vocabulary.size());
    }

    /**
     * Load the training documents
     *
     * @param trainDataFolder
     */
    public void preprocess(String trainDataFolder) {

        File directoryPath = new File(trainDataFolder);
        ArrayList<String> subDirectoryTemp = new ArrayList<>();
        for (String str : directoryPath.list()) {
            if (str.equals(".DS_Store")) continue;
            else {
                subDirectoryTemp.add(str);
                numClasses++;
            }
        }

        classDocCounts = new int[numClasses];     // number of docs per class (4)
        classStrings = new String[numClasses];    // concatenated string for a given class (5)
        classTokenCounts = new int[numClasses];   // total number of tokens per class (6)
        condProb = new HashMap[numClasses];       // term conditional prob (7)
        vocabulary = new HashSet<String>();       // entire vocabulary (8)
        trainingClasses = new HashMap<>();

        File[] files = directoryPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(".DS_Store");
            }
        });

        for (int i = 0; i < subDirectoryTemp.size(); i++) {
            trainingClasses.put(subDirectoryTemp.get(i), i);
        }

        int docCount;
        ArrayList<String> trainingDocsTemp = new ArrayList<>();
        for (int j = 0; j < files.length; j++) {
            docCount = 0;
            System.out.println("current directory is : " + files[j].toString());
            File[] temp = files[j].listFiles();

            for (int i = 0; i < temp.length; i++) {
                docCount++;
                // ********* Read single file *****
                String singleDoc = new String();
                try (BufferedReader br = new BufferedReader(new FileReader(temp[i]))) {
                    String line;
                    while ((line = br.readLine()) != null) singleDoc += line;
                    trainingDocsTemp.add(singleDoc);
                    classStrings[j] += (" " + singleDoc); // classStrings: concatenated string for a given class
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            classDocCounts[j] = docCount;
            System.out.println(classStrings[j].length());
        }
        trainingDocs = new String[trainingDocsTemp.size()];
        for (int i = 0; i < trainingDocsTemp.size(); i++) trainingDocs[i] = trainingDocsTemp.get(i);
    }


    /**
     * Classify a test doc
     * @param doc test doc
     * @return class label
     */
    public int classify(String doc) {
        int label = 0;
        int vsize = vocabulary.size();
        double[] score = new double[numClasses];

        // prior probability
        for (int i = 0; i < score.length; i++) {
            score[i] = classDocCounts[i] * 1.0 / trainingDocs.length;
        }

        String[] tokens = doc.split("\\s+");

        for (int i = 0; i < numClasses; i++) {
            for (String token : tokens) {
                if (condProb[i].containsKey(token)){
                    score[i] *= Math.abs(Math.log10(condProb[i].get(token)));
                }else{// This is the case that search term does not exist in the termlist
                    score[i] *= 1.0 / (classTokenCounts[i] + vsize);  // denominator would be wrong
//                    score[i] *= 1.0 / ( condProb[i].get(token) + vsize);
                }
            }
        }

        double maxScore = score[0];
        System.out.println("Score[0]: " + score[0]);

        for (int i = 1; i < score.length; i++) {
            System.out.println("Score[" + i + "]: " + score[i]);
            if (score[i] > maxScore) {
                maxScore = score[i];
                label = i;
            }
        }
        return label;
    }


    /**
     * Classify a set of testing documents and report the accuracy
     * @param testDataFolder fold that contains the testing documents
     * @return classification accuracy
     */
    public double classifyAll(String testDataFolder) {
        double label;

        File directoryPath = new File(testDataFolder);
        File filesList[] = directoryPath.listFiles();

        double total = 0 ;
        double correct = 0 ;




        filesList = directoryPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(".DS_Store");
            }
        });

        for (int j = 0; j < filesList.length; j++) {
            File[] file = filesList[j].listFiles();
            for (int i = 0; i < file.length; i++) {
                // ********* Read single file *****
                String singleDoc = new String();
                try (BufferedReader br = new BufferedReader(new FileReader(file[i]))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        singleDoc += line;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                total++;
                label = classify(singleDoc);
                if(label == j) correct++;
                System.out.println(correct);
                System.out.println("j is : " + j);
                System.out.println("final output is : " + label + "\n");
            }
        }

        double outcome = correct/total;
        return outcome;
    }


    public static void main(String[] args) {
        String trainingDataFolder = "data/train";
        NBClassifier classifier = new NBClassifier(trainingDataFolder);

        System.out.println("Single Document Classification Test 1");
        String testPath = "data/test/neg/cv904_25663.txt";
        File directoryPath = new File(testPath);

        String cv904_25663 ="";
        try (BufferedReader br = new BufferedReader(new FileReader(directoryPath))) {
            String line;
            while ((line = br.readLine()) != null) cv904_25663 += line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        int doc1Result = classifier.classify(cv904_25663);
        System.out.println(doc1Result);


        System.out.println("\nSingle Document Classification Test 2");
        String testPath2 = "data/test/neg/cv920_29423.txt";
        File directoryPath2 = new File(testPath2);

        String cv920_29423 ="";
        try (BufferedReader br = new BufferedReader(new FileReader(directoryPath2))) {
            String line;
            while ((line = br.readLine()) != null) cv920_29423 += line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        int doc2Result = classifier.classify(cv920_29423);
        System.out.println(doc2Result);

        System.out.println("\nAll Documents Classification Test");

        double classificationAllOutcome = classifier.classifyAll("data/test");
        System.out.println(classificationAllOutcome);
    }
}