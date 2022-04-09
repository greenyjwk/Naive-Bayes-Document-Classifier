package com.company;

import java.io.*;
import java.util.*;

/**
 * ISTE-612-2215 Lab #4
 * JI Woong Kim
 * April 06
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

        for (int i = 0; i < numClasses; i++) condProb[i] = new HashMap<String, Double>();

        for(int i=0;i<numClasses;i++){
//            String[] tokens = classStrings[i].split(" ");
            String[] tokens = classStrings[i].split("\\s+");
            classTokenCounts[i] = tokens.length;
            for(String token:tokens){
                vocabulary.add(token);
                if(condProb[i].containsKey(token)){
                    double count = condProb[i].get(token);
                    condProb[i].put(token, count+1);
                } else condProb[i].put(token, 1.0);
            }
        }


        for(int i=0;i<numClasses;i++){
            Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
            int vSize = vocabulary.size();
            while(iterator.hasNext()) {
                Map.Entry<String, Double> entry = iterator.next();
                String token = entry.getKey();
                Double count = entry.getValue();
                Double prob = (count+1)/(classTokenCounts[i]+vSize);
                condProb[i].put(token, prob);
            }
        }



        System.out.println( "This is count " + trainingDocs.length );
        System.out.println( "This is the number of strings in neg directory " + classStrings[0].length() );
        System.out.println( "This is the number of strings in pos directory " + classStrings[1].length() );
        System.out.println( "This is trainingClasses " + trainingClasses.entrySet() );
        System.out.println( "This is the number of classes " + numClasses );

        for(int i = 0 ;  i < classTokenCounts.length ; i++){
            System.out.println( "This is total number of tokens per class " + classTokenCounts[i] );
        }

        System.out.println("This is the entire number of words that appear " + vocabulary.size());

        System.out.println(condProb[0]);

    }


    /**
     * Classify a test doc
     *
     * @param doc test doc
     * @return class label
     */
    public int classify(String doc) {

        return 0;
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
            else{
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

        ArrayList<String> trainingDocsTemp = new ArrayList<>();
        for(int j = 0 ;  j < files.length ; j++){
            System.out.println("current directory is : " + files[j].toString());
            File[] temp = files[j].listFiles();

            for (int i = 0; i < temp.length; i++) {
                // ********* Read single file *****
                String singleDoc = new String();
                try (BufferedReader br = new BufferedReader(new FileReader(temp[i]))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        singleDoc += line;
                    }
                    trainingDocsTemp.add(singleDoc);
                    classStrings[j] += (" " + singleDoc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(classStrings[j].length());
        }

        trainingDocs = new String[trainingDocsTemp.size()];
        for(int i = 0 ;  i < trainingDocsTemp.size(); i++) trainingDocs[i] = trainingDocsTemp.get(i);

    }

    /**
     * Classify a set of testing documents and report the accuracy
     *
     * @param testDataFolder fold that contains the testing documents
     * @return classification accuracy
     */
    public double classifyAll(String testDataFolder) {

        return 0;
    }


    public static void main(String[] args) {

        String trainingDataFolder = "data/train";
        NBClassifier classifier = new NBClassifier(trainingDataFolder);


    }
}