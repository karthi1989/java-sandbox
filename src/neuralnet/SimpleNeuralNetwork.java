package neuralnet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * This is a very simple neural network implementation, to demonstrate the basic principles.
 * It has no learning capabilities, and uses a pre-trained model to perform binary classification.
 * You are free to use this code, but please reference me or the original article.
 * @author Marek Rei (marek@marekrei.com)
 *
 */
public class SimpleNeuralNetwork {
	int M; // Number of features (dimensions) in the feature vectors
	int H; // Number of neurons in the hidden layers

	double[] input; // Input layer
	double[] hidden; // Hidden layer
	double output; // Output layer / value
	
	double[][] weights1; // Weights input->hidden
	double[] weights2; // Weights hidden->output
	
	public double sigmoid(double a){
		return 1.0 / (1.0 + Math.exp(-a));
	}

	/**
	 * Create necessary structures for the network
	 */
	public void initialiseNetwork(){
		input = new double[1 + M]; // 1 is for the bias
		hidden = new double[1 + H];
		weights1 = new double[1 + M][H];
		weights2 = new double[1 + H];
		
		input[0] = 1.0; // Setting the bias
		hidden[0] = 1.0;
	}

	/**
	 * Perform a forward pass of the network, input->hidden->output.
	 * Assumes that the input has been initialised with the correct values.
	 */
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

	/**
	 * Perform a test using the current model.
	 * Returns system accuracy.
	 * 
	 * @param testingFile Input file of vectors for testing
	 * @param verbose Verbose output
	 * @return Accuracy
	 */
	public double test(String testingFile, boolean verbose){
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
				
				if(verbose)
					System.out.println("Label: " + label + "\tPrediction: " + String.format("%.2f", output));

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
	
	/**
	 * Load the model from a file
	 * @param file Model file
	 */
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
		if(args.length != 2){
			System.err.println("Usage: SimpleNeuralNetwork <modelfile> <testfile>");
			System.exit(1);
		}
		
		String modelFile = args[0];
		String testFile = args[1];
		
		SimpleNeuralNetwork network = new SimpleNeuralNetwork();
		network.initialiseNetwork();
		network.loadModel(modelFile);
		System.out.println("Accuracy: " + network.test(testFile, true));
	}

}
