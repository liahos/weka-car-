package com.example.mav3rick.readwrite;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;
import weka.core.Instance;
import weka.filters.unsupervised.attribute.StringToWordVector;

import android.util.Log;

/**
 * Created by Sohail on 1/10/2015.
 */
public class Tranny {


    Instances instances;
    //FilteredClassifier classifier = new FilteredClassifier();
    AdaBoostM1 classifier = new AdaBoostM1();
    public Tranny() {

    }

    //Builds Classifier
    public int build(String fname) {
        int flag = 0;
        Instances traindata = null;

        ArffLoader loader = new ArffLoader();
        try {
            loader.setFile(new File("/sdcard/" + fname + ".arff"));
            traindata = loader.getDataSet();
            traindata.setClassIndex(traindata.numAttributes() - 1);
        } catch (IOException e) {
            flag = 1;
            e.printStackTrace();
        }

        try {
            classifier.buildClassifier(traindata);
        } catch (Exception e) {

            flag = 2;
            e.printStackTrace();
        }

        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("/sdcard/model.txt"));
            out.writeObject(classifier);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    //Evalutes the built Classifier model
    public String evaluate (String fname) {

        String [] options = new String[2];
        options[0] = "-t";
        options[1] = "/sdcard/"+fname+".arff";

        String out = null;

        try {
            out = Evaluation.evaluateModel(classifier, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    //Classifies data
    public String classify(String fname, String [] filename) {

        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream("/sdcard/model.txt"));
            try {
                Object tmp = in.readObject();
                classifier = (AdaBoostM1) tmp;
                in.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String out = "";

        String text;

        try {
            //BufferedReader reader = new BufferedReader(new FileReader("/sdcard/"+fname+".arff"));
            ArffLoader arff= null;

            BufferedReader read = new BufferedReader(new FileReader("/sdcard/"+fname+".csv"));

            try {
                while((text = read.readLine())!=null) {

                    arff = new ArffLoader();
                    arff.setFile(new File("/sdcard/"+filename[0]+".arff"));
                    //arff.setFile(new File("/sdcard/cartest1.arff"));
                    instances = arff.getStructure();


                    //instances = new Instances("Test relation", (java.util.ArrayList<Attribute>) attributelist, 1);
                    instances.setClassIndex(instances.numAttributes()-1);

                    DenseInstance instance = new DenseInstance(29);
                    instance.setDataset(instances);

                    String [] stringvalues = text.split(",");

                    instance.setValue(0, Double.parseDouble(stringvalues[0]));
                    instance.setValue(1, stringvalues[1]);
                    instance.setValue(2, stringvalues[2]);
                    instance.setValue(3, stringvalues[3]);
                    instance.setValue(4, Double.parseDouble(stringvalues[4]));
                    instance.setValue(5, Double.parseDouble(stringvalues[5]));
                    instance.setValue(6, Double.parseDouble(stringvalues[6]));
                    instance.setValue(7, Double.parseDouble(stringvalues[7]));
                    instance.setValue(8, Double.parseDouble(stringvalues[8]));
                    instance.setValue(9, Double.parseDouble(stringvalues[9]));
                    instance.setValue(10, Double.parseDouble(stringvalues[10]));
                    instance.setValue(11, Double.parseDouble(stringvalues[11]));
                    instance.setValue(12, Double.parseDouble(stringvalues[12]));
                    instance.setValue(13, Double.parseDouble(stringvalues[13]));
                    instance.setValue(14, Double.parseDouble(stringvalues[14]));
                    instance.setValue(15, Double.parseDouble(stringvalues[15]));
                    instance.setValue(16, Double.parseDouble(stringvalues[16]));
                    instance.setValue(17, Double.parseDouble(stringvalues[17]));
                    instance.setValue(18, Double.parseDouble(stringvalues[18]));
                    instance.setValue(19, Double.parseDouble(stringvalues[19]));
                    instance.setValue(20, Double.parseDouble(stringvalues[20]));
                    instance.setValue(21, Double.parseDouble(stringvalues[21]));
                    instance.setValue(22, Double.parseDouble(stringvalues[22]));
                    instance.setValue(23, Double.parseDouble(stringvalues[23]));
                    instance.setValue(24, Double.parseDouble(stringvalues[24]));
                    instance.setValue(25, Double.parseDouble(stringvalues[25]));
                    instance.setValue(26, Double.parseDouble(stringvalues[26]));
                    instance.setValue(27, Double.parseDouble(stringvalues[27]));

                    instances.add(instance);


                    double pred = 0;
                    try {
                        pred = classifier.classifyInstance(instances.instance(0));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    out = out + instances.classAttribute().value((int) pred)+ "\n";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return out;
    }

}
