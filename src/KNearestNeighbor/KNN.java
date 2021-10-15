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
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author user
 */
public class KNN {
    
    /**
     * trainingAttributes merupakan data training untuk mencari hipotesa data
     * testingAttributes merupakan data testing untuk mencari kesimpulan berdasarkan hipotesa yang ditemukan
     */
    private static double[][]  dataset;
    
    private static List<double[]> testingData, trainingData;
    /**
     * hypotesis merupakan hipotesa data berdasarkan training data yang digunakan
     */
    private static String[] hypotesis;

    /**
     * TRAINING_DATA_FILES merupakan lokasi data training
     */
    private static final String DATASETS_FILES = "C:\\WORK\\SEMESTER 5\\MACHINE LEARNING\\Datasets\\thyroid.csv";
   

    
    /**
     * showing how much rows in a dataset
     */
    private static int dataLength;
        
    /**
     * showing how much columns in a dataset
     */
    private static int dataWidth; 
    /**
     * @param args the command line arguments
     */
    
    private static double[] labelData;
    private static double[] mean;
    private static double[] standarDeviation;
    /**
     * Merupakakan method main yang dijalankan saat program di jalankan
     * Method ini akan mengambil data training dan data testing dari lokasi data
     * Lalu mencari hipotesa berdasarkan data training dan mencari kesimpulan berdasarkan hipotesa dan data testing
     *
     * @param args merupakan parameter dari user saat program dijalankan
     */
    public static void main(String[] args) {
        dataset = dataToInt(DATASETS_FILES);
        
        labelData = getLabel(dataset);
        
        splitTrainTest(dataset,80);
        getMean();
        getStd();
        normalizeTrain();
        normalizeTest();
               
//        for(double i: mean)
//            System.out.println(i);
//        System.out.println("\nSTANDAR DEVIATION");
//        for(double i: standarDeviation)
//            System.out.println(i);
//        normalizeTrain();
//        for(double[] i: trainingData)
//            System.out.println(Arrays.toString(i));
        findPrediction();
            
    }
    
    private static String[][] setData(String filePath) {
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
                    resultList.add(str.split(";")); // Separating every word based on space, on csv file it is based on commas
            }
        } catch (FileNotFoundException e) { 
            System.out.println("File tidak ditemukan");
        } catch (IOException e) {
            System.out.println("Kesalahan saat membaca file");
        } finally {
            if (resultList.size() > 0) { // Jika file tidak kosong
                dataLength = resultList.size();
                dataWidth= resultList.get(0).length;
                
                //labelData = getLabel(resultList);
                
                data = new String[dataLength][dataWidth]; //instanciation of atributes                               
                data = resultList.toArray(data); // Casting to basic array
            }
        }
        
        return data; 
    }
        
    private static double[][] dataToInt(String filePath){
        String[][] rawData = setData(filePath);
        double[][] convertedData = new double[dataLength][dataWidth];
        
        
        for(int i=0; i<dataLength; i++){
            for(int j=0; j<dataWidth; j++){
                convertedData[i][j] = Double.parseDouble(rawData[i][j]);                
            }
        }
        
        
        return convertedData;
    }
    
    private static void splitTrainTest(double[][] dataset, int splitRatio){
        double[][] classifiedData;
        trainingData = new ArrayList<>();
        testingData = new ArrayList<>();
        
        for (double label : labelData){      
            classifiedData = classifyingData(label);
            
            int splittedData = (int)(splitRatio * classifiedData.length)/100;
            
            for(int i=0; i < splittedData; i++){ 
                trainingData.add(classifiedData[i]);                                                 
            }
            for(int i = splittedData; i < classifiedData.length; i++)
                testingData.add(classifiedData[i]);
            
            classifiedData = null;
        } 
        
        for(int i=0; i<testingData.size();i++){
            System.out.println(Arrays.toString(testingData.get(i)));
        }
        
    }
    private static void getMean(){
        double sum;
        mean = new double[dataWidth-1];
        for(int j=0; j<trainingData.get(0).length - 1; j++){
            sum = 0.d;
            for(int i=0; i< trainingData.size() ; i++){
                sum = sum + trainingData.get(i)[j];
            }
            mean[j] = sum/trainingData.size();
        }
    }
    private static void getStd(){
       
        double std;
        standarDeviation = new double[mean.length];        
        for(int j=0; j<trainingData.get(0).length - 1; j++){
            std = 0.d;
            for(int i=0; i< trainingData.size() ; i++){
                std = std + Math.pow(trainingData.get(i)[j] - mean[j], 2);;
            }
            standarDeviation[j] = Math.sqrt(std/trainingData.size());
        }       
        
    }
    private static void normalizeTrain(){
        for(int i=0; i<trainingData.size();i++){
            for(int j=0; j< trainingData.get(i).length - 1; j++){
                trainingData.get(i)[j] = (trainingData.get(i)[j] - mean[j])/standarDeviation[j];
            }
        }
    }
    private static void normalizeTest(){
        for(int i=0; i<testingData.size();i++){
            for(int j=0; j< testingData.get(i).length - 1; j++){
                testingData.get(i)[j] = (testingData.get(i)[j] - mean[j])/standarDeviation[j];
            }
        }
    }
    private static double[][] classifyingData(double key){
        double[][] result;
        int count=0, j=0;
        for(int i=0 ;i < dataLength; i++){
            if(dataset[i][dataWidth-1] == key)
                count++;
        }
        
        result = new double[count][dataWidth];
        for(int i=0 ;i < dataLength; i++){
            if(dataset[i][dataWidth-1] == key){
                System.arraycopy(dataset[i], 0, result[j], 0, dataWidth);
                j++;
            }           
                
        }
        return result;
    }
    
    private static double[] getLabel(double[][] data){
        double[] sourceArray = new double[dataLength];        
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
    
    private static void findPrediction(){
       
        List<List<Double>> distances = new ArrayList<>();
        
        ArrayList<Map<Double, Double>> knn = new ArrayList<>();
        Map<Double, Double> tempMap;
        
        for(int i =0; i < testingData.size(); i++){
            tempMap = new HashMap<>();
            for(int j = 0; j < trainingData.size(); j++){   
                List<Double> temp=new ArrayList<>();   
                //temp.add(distance(trainingData.get(j), testingData.get(j)));
                //temp.add((double)trainingData.get(j)[2]);
                //distances.add(temp);
                tempMap.put(distance(trainingData.get(j), testingData.get(i)), trainingData.get(j)[trainingData.get(j).length -1]);
            }            
            knn.add(i,tempMap);
        }
        int i =0;
        int j=0;
//        for(Map<Double, Double> data : knn){
//            for (Map.Entry<Double, Double> entry : data.entrySet()) {
//                System.out.println(i+ " " + entry.getKey() + ":" + entry.getValue().toString());
//                i++;
//            }
//        }
        i=0;
        ArrayList<TreeMap<Double, Double>> sorted = new ArrayList<>();
        
        for(Map<Double, Double> data : knn){                  
            TreeMap<Double, Double> t = new TreeMap<Double, Double>(data);
            sorted.add(i,t);
        }
        
//        for(Map<Double, Double> data : sorted){
//            for (Map.Entry<Double, Double> entry : data.entrySet()) {
//                System.out.println(i+ " " + entry.getKey() + ":" + entry.getValue().toString());
//                i++;
//            }
//        }
        
        //System.out.println(distances);
        
        int datalength = distances.size();
        List<Double> temp = new ArrayList<>();
        double[] prediction = new double[sorted.size()];
        i=0;
        
        System.out.println("PREDICTIONS");
        for(TreeMap<Double, Double> data : sorted){
            prediction[i] = getKNNPrediction(1, data);
            //System.out.println(prediction[i]);
            i++;
        }
        
        i = prediction.length - 1;
        int errorRate = 0;
        for(double[] data: testingData){
            if(data[trainingData.get(0).length - 1] != prediction[i])
                errorRate++;
            i--;
        }
        System.out.println("EROR RATE = " +errorRate/testingData.size()+ "%");
//        for(int i=0; i < datalength; i++){
//            for(int j=1; j < (datalength-i); j++){
//                if(distances.get(j-1).get(0)>distances.get(j).get(0)){
//                    temp = distances.get(j-1);
//                    distances.set(j-1,distances.get(j));
//                    distances.set(j,temp);
//                }
//            }
//        }
        
        //getKNN(5,distances, labelData);
//        List<List<Double>> result = new ArrayList<>();
//        for(int i = 0; i< 5; i++){
//            result.add(distances.get(i));
//        }
        
    }
    
    private static double getKNNPrediction(int k, TreeMap<Double,Double> dist){
        
        int sum1, sum2, sum3, sum4, result;
        sum1 = sum2 = sum3 = sum4 = result = 0;
        
        Set<Map.Entry<Double, Double>> entrySet = dist.entrySet();
        Map.Entry<Double, Double>[] entryArray = entrySet.toArray(new Map.Entry[entrySet.size()]);
        
        for(int i=0;i<k;i++){                
            if (entryArray[i].getValue() == 1)
                sum1++;
            else if (entryArray[i].getValue() == 2)
                sum2++;
            else if (entryArray[i].getValue() == 3)
                sum3++;
            else if (entryArray[i].getValue() == 4)
                sum4++;
        }
        
        if (sum1 >= sum2 && sum1 >= sum3 && sum1 >= sum4)
            result = 1;
        else if (sum2 >= sum1 && sum2 >= sum3 && sum2 >= sum4)
            result = 2;
        else if (sum3 >= sum1 && sum3 >= sum2 && sum3 >= sum4)
            result = 3;
        else if (sum4 >= sum1 && sum4 >= sum2 && sum4 >= sum3)
            result = 4;

        return (double)result;        
    }
    
    //private static void validatePrediction()
    public static int getKNN(int k, double[][] sortAllDistance, int[] trainingLabel) {
        int sum1, sum2, sum3, sum4, result;
        sum1 = sum2 = sum3 = sum4 = result = 0;
        
        for (int i = 0; i < k; i++) {
            if (trainingLabel[(int) sortAllDistance[i][1]] == 1)
                sum1++;
            else if (trainingLabel[(int) sortAllDistance[i][1]] == 2)
                sum2++;
            else if (trainingLabel[(int) sortAllDistance[i][1]] == 3)
                sum3++;
            else if (trainingLabel[(int) sortAllDistance[i][1]] == 4)
                sum4++;
        }
        if (sum1 >= sum2 && sum1 >= sum3 && sum1 >= sum4)
            result = 1;
        else if (sum2 >= sum1 && sum2 >= sum3 && sum2 >= sum4)
            result = 2;
        else if (sum3 >= sum1 && sum3 >= sum2 && sum3 >= sum4)
            result = 3;
        else if (sum4 >= sum1 && sum4 >= sum2 && sum4 >= sum3)
            result = 4;

        return result;
    }
    
    public static double getDistance(int[] testingdata_i, int[] trainingdata_j) {
        double sum = 0.0;
        for (int i = 0; i < testingdata_i.length; i++)
            sum += Math.pow((Double.valueOf(testingdata_i[i]) - Double.valueOf(trainingdata_j[i])), 2);
        double result = Math.sqrt(sum);
        return result;
    }

    public static double[] getAllDistance(int[] testingdata_i, int[][] trainingdata) {
        int total = trainingdata.length;
        double[] alldistance = new double[total];
        for (int i = 0; i < total; i++)
            alldistance[i] = getDistance(testingdata_i, trainingdata[i]);
        return alldistance;
    }

    public static double[][] sortData(double[] allDistance) {
        int total = allDistance.length;
        double[][] result = new double[total][2];
        for (int i = 0; i < total; i++) {
            result[i][0] = allDistance[i];
            result[i][1] = (double) i;
        }
        for (int i = 0; i < total; i++) {
            for (int j = i + 1; j < total; j++) {
                    if (result[i][0] > result[j][0])
                        result = swap(result, i, j);
            }
        }
        return result;
    }

    public static double[][] swap(double[][] sortAllDistance, int i, int j) {
        double[][] temp = new double[1][2];

        temp[0][0] = sortAllDistance[i][0];
        temp[0][1] = sortAllDistance[i][1];

        sortAllDistance[i][0] = sortAllDistance[j][0];
        sortAllDistance[i][1] = sortAllDistance[j][1];

        sortAllDistance[j][0] = temp[0][0];
        sortAllDistance[j][1] = temp[0][1];

        return sortAllDistance;
    }
            
    private static double distance (double[] train,double[] test){
        double sum=0;
        for(int i=0; i<train.length - 1; i++){
           sum+=Math.pow((train[i] - test[i]), 2) + Math.pow((train[i] - test[i]), 2);
        } 
        return Math.sqrt(sum);
    } 
    
}

