/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conceptlearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class ConceptLearning {
    
    /**
     * saving all the data to training and testing data 
     */
    private static String[][] trainingData, testingData;

    /**
     * hypotesis is variable saving the current hypothesis until final hypothesis
     */
    private static String[] hypothesis;

    /**
     * TRAINING_DATA_FILES data training filepath
     */
    private static final String TRAINING_DATA_FILES = "C:\\WORK\\SEMESTER 5\\MACHINE LEARNING\\Datasets\\DiabetesTraining.txt";

    /**
     * TESTING_DATA_FILES data testing file path
     */
    private static final String TESTING_DATA_FILES = "C:\\WORK\\SEMESTER 5\\MACHINE LEARNING\\Datasets\\CuacaTesting.txt";
    
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
    
    
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        /**
         * Split data to training and testing
         */
        trainingData = setData(TRAINING_DATA_FILES);
                
        // minused by 1 so the target data wil not assigned on hypothesis
        dataWidth--;
                
//        String[] test = new String[dataWidth];
//        
//        for(int i=0; i < dataWidth;i++){
//            System.out.print("Atribut ke - " +(i+1)+ " = ");
//            test[i] = input.nextLine();
//        }
//        
//        System.out.print("Expected target = ");
//        String target = input.nextLine();
//        
//        for(String t: test){
//            System.out.print(t+" ");
//        }
//        System.out.print("\nIs " +target+ " ? " +findTarget(test,target)+ "\n");
        
        getHypothesis("N");
        getHypothesis("Y");
        
        //showHypothesis();
        
        /**
         * showing the hypothesis
         */
        
    }
    
    public static String[][] setData(String filePath){
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
                dataLength = resultList.size();
                dataWidth= resultList.get(0).length;

                data = new String[dataLength][dataWidth]; //instanciation of atributes                               
                data = resultList.toArray(data); // Casting to basic array
            }
        }
        
        return data; 
    }
    
    public static void getHypothesis(String target){         
        int i;
        /**
         * finding the first data matching with the target
         */
        hypothesis = new String[dataWidth]; 
        for(i=0; i< dataLength;i++){
            if(trainingData[i][dataWidth].equalsIgnoreCase(target)){
                System.arraycopy(trainingData[i], 0, hypothesis, 0, (dataWidth));
                break;
            }                             
        }
        
        /**
         * Creating the hypothesis based on FindS algorithm
         */
        for(int j = i ; j<dataLength ; j++){
            if(trainingData[j][dataWidth].equalsIgnoreCase(target)){
                /**
                 * checking every attributes in a row. 
                 * if unmatched with current hypothesis change to "?"
                 * So every instance can assign the hypothesis
                 */
                for(int x = 0; x < dataWidth; x++){
                    if(!(hypothesis[x].equalsIgnoreCase(trainingData[j][x]))){
                        hypothesis[x] = "?";
                    }
                }                
            }            
        }
        showHypothesis();
        System.out.println(target);
    }
    
    public static void showHypothesis(){
        for(String h : hypothesis)
            System.out.print(h+" ");
        //System.out.println("");
    }
    
    public static boolean findTarget(String[] test,String target){
        boolean isSame = true;
        
        getHypothesis(target);
        
        for(int i = 0; i<dataWidth;i++){
            if(!hypothesis[i].equals("?") && !(test[i].equalsIgnoreCase(hypothesis[i])))
                isSame = false;
        }
        return isSame;
    }
}


