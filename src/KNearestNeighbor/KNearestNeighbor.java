/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KNearestNeighbor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author user
 */
public class KNearestNeighbor {
    private static final String DATASET_FILES = "C:\\WORK\\SEMESTER 5\\MACHINE LEARNING\\Datasets\\Diabetes2.txt";
    
    private static double[][] rawDataset, trainingData, testingData;
    private static double[] rawDataLabel;
    private static double[] mean,std;
    private static String[] classLabel;
    
    //private static List<List<Integer>> 
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        rawDataset = setData(DATASET_FILES);
        //print2D(rawDataset);
        rawDataLabel = getLabel(rawDataset);        
        print1D(rawDataLabel);        
        //print2D(classifiedData);
        //splitTrainTest(rawDataset,80);
        trainingData = rawDataset;
        
        System.out.println("==== TRAINING DATA ====");
        print2D(trainingData);
        int dataWidth = trainingData[0].length - 1;
        testingData = new double[1][dataWidth];
        
        for(int i=0; i < dataWidth;i++){
            System.out.print("Atribut ke - " +(i+1)+ " = ");
            testingData[0][i] = input.nextDouble();
        }
        int k = 5;
        System.out.println("K = "+k);
        for(double t:testingData[0])
            System.out.print(t+" ");
        
        mean = getColumnMean(trainingData);
        std = getColumnStd(trainingData, mean);
        
        trainingData = normalize(trainingData);
        testingData = normalize(testingData);
        
        double prediction = getPrediction(sortAllDistance(getAllDistances(testingData[0], trainingData)) ,k);
        
        System.out.println(prediction);
        
//        System.out.print("Expected target = ");
//        String target = input.nextLine();
//        
//        for(String t: test){
//            System.out.print(t+" ");
//        }
        //KNNAlgorithm(5,true);
        //System.out.print("\nIs " +target+ " ? " +findTarget(test,target)+ "\n");
        
//        System.out.println("==== TESTING DATA ====");
//        print2D(testingData);
        //mean = getColumnMean(trainingData);
        //System.out.println("==== MEAN ====");
        //print1D(mean);
        //std = getColumnStd(trainingData, mean);
        //System.out.println("==== PREDICTION ====");
//        for(int i=1; i<=10; i++)
            //KNNAlgorithm(10, true);
        //KFoldValidation(215);
    }
    private static void print2D(double[][] data){
        for(int i = 0; i< data.length; i++){
            System.out.print(i+" = ");
            System.out.println(Arrays.toString(data[i]));
        }
    }
    private static void print1D(double[] data){
        for(int i=0; i< data.length; i++)
            System.out.println(i+" = "+data[i]);
    }
    private static void printMap(Map<Double,Double> data){
        System.out.println(data);
    }
    private static double[][] setData(String filePath) {
        ArrayList<String[]> resultList = new ArrayList<>(); // temporary saving the data file because we need split function
        String[][] data = null; // Array of string represent whole data
        try {
            /**
             * Reading file
             */
            File file = new File(filePath); 
            BufferedReader br = new BufferedReader(new FileReader(file));            

            /*
             * Reading all data in file based on its line
             */
            String str = "";
            while (str != null) {
                str = br.readLine(); // fetch every lines of data

                if (str != null)
                    resultList.add(str.split(" ")); // Separating every word based on space, on csv file it is based on commas
            }
        } catch (FileNotFoundException e) { 
            System.out.println("File tidak ditemukan");
        } catch (IOException e) {
            System.out.println("Kesalahan saat membaca file");
        } finally {
            if (resultList.size() > 0) { // Jika file tidak kosong
//                dataLength = resultList.size();
//                dataWidth= resultList.get(0).length;
                
                //labelData = getLabel(resultList);
                
                data = new String[resultList.size()][resultList.get(0).length]; //instanciation of atributes                               
                data = resultList.toArray(data); // Casting to basic array
            }
        }
        
        return dataToInt(data); 
    }   
    private static double[][] dataToInt(String[][] data){
        //String[][] rawData = setData(filePath);
        double[][] convertedData = new double[data.length][data[0].length];        
        classLabel = new String[data.length];
        
        for(int i=0; i<data.length; i++){
            for(int j=0; j<data[0].length-1; j++){
                convertedData[i][j] = Double.parseDouble(data[i][j]);    
                
            }
            classLabel[i] = data[i][data[i].length - 1];
        }
        
        
        return convertedData;
    }
    private static double[] getLabel(double[][] data){
        
        double[] sourceArray = new double[data.length];        
        Set<Double> targetSet = new HashSet<Double>();
        
        for(int i=0; i< data.length; i++){
            targetSet.add(data[i][data[0].length - 1]);
        }
        
        double[] result = new double[targetSet.size()];
        int i=0;
        
        // iterating over the hashset
        for(double ele: targetSet){
          result[i++] = ele;
        }
        return result;
    }
    private static double[][] classifyingData(double[][] data, double key){
        double[][] result;
        int count=0;
        int targetIndex = data[0].length - 1;
        for (double[] data1 : data) {
            if (data1[targetIndex] == key) {
                count++;
            }
        }
        
        int j = 0;
        result = new double[count][data[0].length];
        for(int i=0 ;i < data.length; i++){
            if(data[i][targetIndex] == key){
                System.arraycopy(data[i], 0, result[j], 0, targetIndex + 1);
                j++;
            }           
                
        }
        return result;
    }
    private static void splitTrainTest(double[][] data, int ratio){
        int trainLength = ratio * data.length/ 100;
       
        trainingData = new double[trainLength][data[0].length];
        testingData = new double[data.length - trainLength][data[0].length];
        double[][] classifiedData;
        int j=0,k=0;
        for(double label : rawDataLabel){
            classifiedData = classifyingData(rawDataset, label);
            int dataLength = (int)classifiedData.length * ratio / 100;

            for(int i=0; i<classifiedData.length;i++){
                if(i<dataLength){
                    System.arraycopy(classifiedData[i], 0, trainingData[k], 0, classifiedData[0].length);
                    //System.out.println(k+" = "+Arrays.toString(trainingData[i]));
                    k++;
                }                    
                else{
                    System.arraycopy(classifiedData[i], 0, testingData[j], 0, classifiedData[0].length);
                    j++;
                }
            }
        }
        
        
    }
    private static double[] getColumnMean(double[][] data){
        double sum;
        double[] result = new double[data[0].length-1];
        for(int j=0; j<data[0].length - 1; j++){
            sum=0;
            for(int i=0; i< data.length; i++)
                sum+=data[i][j];
            result[j] = sum/data.length;
        }
        return result;
    }
    private static double[] getColumnStd(double[][] data, double[] avg){
        double std;
        double[] result = new double[avg.length];        
        for(int j=0; j<data[0].length - 1; j++){
            std = 0.d;
            for(int i=0; i< data.length; i++){
                std += Math.pow(data[i][j] - avg[j], 2);;
            }
            result[j] = Math.sqrt(std/data.length);
        }
        return result;
    }
    private static Map<Integer, ArrayList<Double>> getAllDistances(double[] test, double[][] train){
        Map<Integer, ArrayList<Double>> temp = new HashMap<>();
        ArrayList<Double> val = new ArrayList();
        
            for(int j = 0; j < train.length; j++){   
                val = new ArrayList();
                double targetLabel = train[j][train[0].length - 1];
                double distance = getDistance(test, train[j]);
                
                val.add(distance);
                val.add(targetLabel);
                temp.put(j, val);
            }
        return temp;
    }
    private static double getDistance(double[] test, double[] train){
        double sum=0;
        for(int i=0; i<train.length - 1; i++){
           sum+=Math.pow((test[i] - train[i]), 2);
        } 
        return Math.sqrt(sum);
    }
    private static Map<Integer, ArrayList<Double>> sortAllDistance(Map<Integer, ArrayList<Double>> data){       
        
        Map<Integer, ArrayList<Double>> result = new HashMap<>();
        ArrayList<Double> temp = new ArrayList();
        int mapSize = data.size();
        
        for(int i=0;i<mapSize;i++){
            for(int j = i+1; j<mapSize;j++){
                if(data.get(i).get(0) > data.get(j).get(0)){
                    
                    temp = data.get(j);
                    data.replace(j, data.get(i));
                    data.replace(i, temp);
                }
            }
        }
        
        return data;
    }
    private static double getPrediction(Map<Integer, ArrayList<Double>> data,int k){
        double[] sum = new double[rawDataLabel.length];
        for(int i=0; i< sum.length; i++)
            sum[i] = 0.0;
        for(int i=0;i< k;i++){
            for(int j=0; j<rawDataLabel.length; j++){                
                if(data.get(i).get(1) == rawDataLabel[j])
                    sum[j] = sum[j] + 1;
            }                
        }
        double max = 0;
        double index=0;
        for(int i=0; i< sum.length; i++){
            if(sum[i] > max){
                max = sum[i];
                index = (double) i;
            } 
        }
        return index+1;
    }
    private static double KNNAlgorithm(int k,boolean normalization){
        double prediction, error=0, accuracy=0;
        
        if(normalization == true){
            trainingData = normalize(trainingData);
            testingData = normalize(testingData);
        }
        
        for(int i=0; i< testingData.length;i++){
            prediction = getPrediction(sortAllDistance(getAllDistances(testingData[i], trainingData)) ,k);
            if(prediction != testingData[i][testingData[i].length -1])
                error++;            
        }        
        error = error/testingData.length;        
        accuracy = 1.0 - error;
        
        System.out.println("\nk\t\t: "+k);
        System.out.println("Normalization\t: "+normalization);
        System.out.println("Accuracy\t: "+accuracy*100+" %");
        System.out.println("Error\t\t: "+error*100+" %");
        
        return error*100;
    }
    private static double[][] normalize(double[][] data){
        for(int i=0; i< data.length;i++){
            for(int j =0 ; j< data[i].length - 1; j++){
                data[i][j] = (data[i][j] - mean[j])/std[j];
            }
        }
        return data;
    }

    private static void KFoldValidation(int fold){
        int ratio = 100/fold;
        double error=0.0d;
        
        for(int i=1; i<=fold; i++){
            System.out.println("\n==================");
            System.out.println("Experiment = "+i);
            //splitTrainTestForKfold(rawDataset, ratio,i);            
            splitTrainTestForLOO(rawDataset, fold, i);
            error += KNNAlgorithm(10, true);            
        }
        
        System.out.println("\n==================");
        System.out.println("MeanError = "+error/fold);
    }
//    private static void splitTrainTestForKfold(double[][] data, int ratio, int indexFold){
//        int trainLength = ratio * data.length/ 100;        
//        trainingData = new double[data.length - trainLength][data[0].length];
//        testingData = new double[trainLength][data[0].length];
//        double[][] classifiedData;
//        int j=0,k=0;
//        for(double label : rawDataLabel){
//            classifiedData = classifyingData(rawDataset, label);
//            int dataLength = (int)classifiedData.length * ratio / 100;            
//            for(int i=0; i<classifiedData.length;i++){
//                if(indexFold == 1){
//                    if(i<dataLength){
//                    System.arraycopy(classifiedData[i], 0, testingData[k], 
//                            0, classifiedData[0].length);                    
//                    k++;
//                    }                    
//                    else{
//                        System.arraycopy(classifiedData[i], 0, trainingData[j], 
//                                0, classifiedData[0].length);
//                        j++;
//                    }
//                }
//                else{
//                    if(i<dataLength*indexFold && i>=dataLength*(indexFold-1)){
//                        System.arraycopy(classifiedData[i], 0, testingData[k], 
//                                0, classifiedData[0].length);                        
//                        k++;
//                    }                    
//                    else{
//                        System.arraycopy(classifiedData[i], 0, trainingData[j], 
//                                0, classifiedData[0].length);
//                        j++;
//                    }
//                }                
//            }
//        }
//        
//        
//    }
//    
    private static void splitTrainTestForLOO(double[][] data, int fold, 
            int indexFold){
        
        int ratio = data.length/fold;
        
        trainingData = new double[data.length - ratio][data[0].length];
        testingData = new double[ratio][data[0].length];
        int j=0,k=0;
        int dataLength = testingData.length;
        for(int i=0; i<data.length; i++){
            if(i<dataLength*indexFold && i>=dataLength*(indexFold-1)){
                System.arraycopy(data[i], 0, testingData[k], 
                                0, data[0].length);                        
                k++;
            }else{
                System.arraycopy(data[i], 0, trainingData[j], 
                                0, data[0].length);
                j++;
            }
                
        }
//        for(double label : rawDataLabel){
//            classifiedData = classifyingData(rawDataset, label);
//            int dataLength = data.length - ratio;            
//            for(int i=0; i<classifiedData.length;i++){
//                if(indexFold == 1){
//                    if(i<dataLength){
//                    System.arraycopy(classifiedData[i], 0, testingData[k], 
//                            0, classifiedData[0].length);                    
//                    k++;
//                    }                    
//                    else{
//                        System.arraycopy(classifiedData[i], 0, trainingData[j], 
//                                0, classifiedData[0].length);
//                        j++;
//                    }
//                }                             
//            }
//        }        
    }   
}
