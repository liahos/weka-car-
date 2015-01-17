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
    NaiveBayes classifier = new NaiveBayes();
    public Tranny() {

    }

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
        //Stores the model of the classifier built
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

    public String classify(String fname) {
        
        //Loads the stored model of the classifier
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream("/sdcard/model.txt"));
            try {
                Object tmp = in.readObject();
                classifier = (NaiveBayes) tmp;
                in.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       
        //Setting of attributes 
        List attributelist = new ArrayList(7);

        List values = new ArrayList(4);
        values.add("vhigh");
        values.add("high");
        values.add("med");
        values.add("low");
        Attribute attribute1 = new Attribute("buying", values);
        attributelist.add(attribute1);

        Attribute attribute2 = new Attribute("maint", values);
        attributelist.add(attribute2);


        values = new ArrayList(4);
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5more");
        Attribute attribute3 = new Attribute("doors", values);
        attributelist.add(attribute3);

        values = new ArrayList(3);
        values.add("2");
        values.add("4");
        values.add("more");
        Attribute attribute4 = new Attribute("persons", values);
        attributelist.add(attribute4);

        values = new ArrayList(3);
        values.add("small");
        values.add("med");
        values.add("big");
        Attribute attribute5 = new Attribute("lugboot", values);
        attributelist.add(attribute5);

        values = new ArrayList(3);
        values.add("low");
        values.add("med");
        values.add("high");
        Attribute attribute6 = new Attribute("safety", values);
        attributelist.add(attribute6);

        values = new ArrayList(4);
        values.add("unacc");
        values.add("acc");
        values.add("good");
        values.add("vgood");
        Attribute attribute7 = new Attribute("class", values);
        attributelist.add(attribute7);

        String text = null;

        //Reading of test dataset and its classification
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sdcard/"+fname+".arff"));
            String line;

            text = "";

            try {
                while((text = reader.readLine())!=null) {
                    instances = new Instances("Test relation", (java.util.ArrayList<Attribute>) attributelist, 1);
                    instances.setClassIndex(instances.numAttributes()-1);

                    DenseInstance instance = new DenseInstance(7);
                    instance.setDataset(instances);

                    String [] stringvalues = text.split(",");

                    instance.setValue(attribute1, stringvalues[0]);
                    instance.setValue(attribute2, stringvalues[1]);
                    instance.setValue(attribute3, stringvalues[2]);
                    instance.setValue(attribute4, stringvalues[3]);
                    instance.setValue(attribute5, stringvalues[4]);
                    instance.setValue(attribute6, stringvalues[5]);
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
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return out;
    }

}
