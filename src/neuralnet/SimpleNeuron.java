package neuralnet;

public class SimpleNeuron {
	private double[] weights;
	
	public SimpleNeuron(double[] weights){
		this.weights = weights;
	}
	
	public double classify(double[] input){
		double value = 0.0;
		
		// Adding the bias
		value += weights[0] * 1.0;
		
		// Adding the rest of the weights
		for(int i = 0; i < input.length; i++)
			value += weights[i + 1] * input[i];
		
		// Passing the value through the sigmoid activation function
		value = 1.0 / (1.0 + Math.exp(-1.0 * value));
		
		return value;
	}
	
	public static void main(String[] args) {
		// Creating data structures to hold the data
		String[] names = new String[5];
		double[][] vectors = new double[5][2];
		
		// Inserting the data
		names[0] = "London";
		vectors[0][0] = 0.86; 
		vectors[0][1] = 0.09;
		names[1] = "Paris";
		vectors[1][0] = 0.74; 
		vectors[1][1] = 0.11;
		names[2] = "Tuesday";
		vectors[2][0] = 0.15; 
		vectors[2][1] = 0.77;
		names[3] = "Friday";
		vectors[3][0] = 0.05; 
		vectors[3][1] = 0.82;
		names[4] = "???";
		vectors[4][0] = 0.59; 
		vectors[4][1] = 0.19;
		
		// Initialising the weights
		double[] weights = {0.0, 100.0, -100.0};
		SimpleNeuron neuron = new SimpleNeuron(weights);
		
		// Classifying each of the data points
		for(int i = 0; i < names.length; i++){
			double prediction = neuron.classify(vectors[i]);
			System.out.println(names[i] + " : " + (int)prediction);
		}
	}
}
