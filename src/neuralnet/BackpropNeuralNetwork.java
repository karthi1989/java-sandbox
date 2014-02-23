package neuralnet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Work in progress
 *
 */
public class BackpropNeuralNetwork {
	int M = 11;
	int H = 5;
	int seed = 1;
	int epochs = 20;
	double learningRate = 0.1;
	
	double[] input;
	double[] hidden;
	double output;
	
	double[][] weights1;
	double[] weights2;
	
	double outputError;
	double[] hiddenError;
	
	Random random = new Random(seed);
	
	public void initialiseRandomVector(double[] vector){
		for(int i = 0; i < vector.length; i++)
			vector[i] = random.nextGaussian() / 10.0;
	}
	
	public double sigmoid(double a){
		return 1.0 / (1.0 + Math.exp(-a));
	}
	
	public void initialiseNetwork(){
		input = new double[1 + M];
		hidden = new double[1 + H];
		weights1 = new double[1 + M][H];
		weights2 = new double[1 + H];
		hiddenError = new double[1 + H];
		
		for(int i = 0; i < weights1.length; i++)
			initialiseRandomVector(weights1[i]);
		initialiseRandomVector(weights2);
		
		input[0] = 1.0;
		hidden[0] = 1.0;
	}

	public void forwardPass(){
		for(int j = 1; j < hidden.length; j++){
			hidden[j] = 0.0;
			for(int i = 0; i < input.length; i++){
				hidden[j] += input[i] * weights1[i][j-1];
			}
			hidden[j] = sigmoid(hidden[j]);
		}
		
		output = 0.0;
		for(int i = 0; i < hidden.length; i++){
			output += hidden[i] * weights2[i];
		}
		output = sigmoid(output);
	}
	
	public void backPropagate(int correctLabel){
		outputError = output - (double)correctLabel;
		
		for(int j = 0; j < hidden.length; j++){
			hiddenError[j] = outputError * weights2[j] * hidden[j] * (1.0 - hidden[j]);
		}
		
		for(int i = 0; i < weights2.length; i++){
			weights2[i] -= learningRate * outputError * hidden[i];
		}
		
		for(int i = 0; i < weights1.length; i++){
			for(int j = 0; j < weights1[i].length; j++){
				weights1[i][j] -= learningRate * input[i] * hiddenError[j+1];
			}
		}
	}
	
	public void train(String trainingFile){
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new FileReader(trainingFile));
			while ((line = br.readLine()) != null) {
				String[] lineParts = line.split("\\s+");
				int label = Integer.parseInt(lineParts[0]);
				for(int i = 0; i < M; i++)
					input[i+1] = Double.parseDouble(lineParts[i+1]);
				
				forwardPass();
				backPropagate(label);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double test(String testingFile){
		BufferedReader br;
		String line;
		int correct = 0, total = 0;
		
		try {
			br = new BufferedReader(new FileReader(testingFile));
			while ((line = br.readLine()) != null) {
				String[] lineParts = line.split("\\s+");
				int label = Integer.parseInt(lineParts[0]);
				for(int i = 0; i < M; i++)
					input[i+1] = Double.parseDouble(lineParts[i+1]);
				
				forwardPass();

				if((label > 0 && output > 0.5) || (label <= 0 && output <= 0.5))
					correct++;
				total++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (double)correct / total;
	}
	
	public void saveModel(String file){
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.println(M + "\t" + H);
			for(int i = 0; i < weights1.length; i++){
				for(int j = 0; j < weights1[i].length; j++){
					writer.print(weights1[i][j] + "\t");
				}
				writer.println();
			}
			for(int i = 0; i < weights2.length; i++)
				writer.print(weights2[i] + "\t");
			writer.println();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	public void loadModel(String file){
		String line;
		String[] lineParts;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			line = br.readLine();
			lineParts = line.trim().split("\\s+");
			M = Integer.parseInt(lineParts[0]);
			H = Integer.parseInt(lineParts[1]);
			initialiseNetwork();
			for(int i = 0; i < 1 + M; i++){
				lineParts = br.readLine().trim().split("\\s+");
				for(int j = 0; j < H; j++)
					weights1[i][j] = Double.parseDouble(lineParts[j]);
			}
			lineParts = br.readLine().trim().split("\\s+");
			for(int i = 0; i < 1 + H; i++)
				weights2[i] = Double.parseDouble(lineParts[i]);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String trainingFile = "data/countries-classify-gdp-normalised.train.txt";
		String testingFile = "data/countries-classify-gdp-normalised.test.txt";

		BackpropNeuralNetwork network = new BackpropNeuralNetwork();
		network.initialiseNetwork();
		System.out.println("Accuracy before training: " + network.test(testingFile));
		for(int epoch = 1; epoch <= network.epochs; epoch++){
			System.out.print("Epoch: " + epoch + "\t");
			network.train(trainingFile);
			System.out.println("Accuracy: " + network.test(testingFile));
		}
		
		network.saveModel("data/neural-network-binary-model.txt");
		network.loadModel("data/neural-network-binary-model.txt");
		System.out.println("Accuracy: " + network.test(testingFile));
	}

}
