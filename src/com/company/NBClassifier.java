package com.company;


import java.util.HashMap;
import java.util.HashSet;

/**
 * ISTE-612-2215 Lab #4
 * JI Woong Kim
 * April 06
 */
public class NBClassifier {


        private String[] trainingDocs;         //training data (1)
        private int[] trainingClasses;         //training class values (2)
        private int numClasses;                //2 (0 & 1)
        private int[] classDocCounts;          //number of docs per class (4)
        private String[] classStrings;         //concatenated string for a given class (5)
        private int[] classTokenCounts;        //total number of tokens per class (6)
        private HashMap<String,Double>[] condProb; //term conditional prob (7)
        private HashSet<String> vocabulary;    //entire vocabulary (8)


        /**
         * Build a Naive Bayes classifier using a training document set
         * @param trainDataFolder the training document folder
         */
        public NBClassifier(String trainDataFolder) {


        }

        /**
         * Classify a test doc
         * @param doc test doc
         * @return class label
         */
        public int classify(String doc){

                return 0;
        }

        /**
         * Load the training documents
         * @param trainDataFolder
         */
        public void preprocess(String trainDataFolder)
        {

        }

        /**
         * Classify a set of testing documents and report the accuracy
         * @param testDataFolder fold that contains the testing documents
         * @return classification accuracy
         */
        public double classifyAll(String testDataFolder)
        {

                return 0;
        }


        public static void main(String[] args){

        }
}