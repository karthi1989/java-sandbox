package javabridge;

import java.util.ArrayList;

public class VectorMachine {
	public static double[][] vectors;
	
	public static void init(){
		vectors = new double[5000][100];
		for(int i = 0; i < vectors.length; i++){
			for(int j = 0; j < vectors[i].length; j++){
				vectors[i][j] = (i * j * 1487) % 2729;
			}
		}
	}
	
	public static int size(){
		return vectors.length;
	}
	
	public static int vectorSize(){
		return vectors[0].length;
	}
	
	public static double cosine(double[] v1, ArrayList<Double> v2){
		if(v1.length != v2.size())
			throw new RuntimeException("Mismatching vectors: " + v1.length + " " + v2.size());
		
		double length1 = 0.0;
		double length2 = 0.0;
		double dotProduct = 0.0;
		
		for(int i = 0; i < v1.length; i++){
			dotProduct += v1[i] * v2.get(i);
			length1 += v1[i] * v1[i];
			length2 += v2.get(i) * v2.get(i);
		}

		double result;
		if(length1 == 0.0 || length2 == 0.0)
			result = 0.0;
		else
			result = dotProduct / Math.sqrt(length1 * length2);

		return result;
	}
	
	public int findMostSimilar(ArrayList<Double> v){
		double maxSim = -Double.MAX_VALUE;
		int maxIndex = -1;
		
		for(int i = 0; i < vectors.length; i++){
			double sim = cosine(vectors[i], v);
			if(sim > maxSim){
				maxSim = sim;
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	
	public static double test(int iterations){
		VectorMachine.init();
		VectorMachine vm = new VectorMachine();
		double timeTotal = 0.0;
		
		for(int k = 0; k < iterations; k++){
			ArrayList<Double> v = new ArrayList<Double>();
			for(int i = 0; i < vectors[0].length; i++)
				v.add((double)(((i + k) * 3821) % 1483));
			
			double time1 = System.nanoTime() / 1000000.0;
			int answer = vm.findMostSimilar(v);
			//System.out.println(answer);
			double time2 = System.nanoTime() / 1000000.0;
			
			double diffMilliseconds = time2 - time1;
			timeTotal += diffMilliseconds;
			System.out.println("Java time: " + diffMilliseconds);
		}
		
		return timeTotal / (double) iterations;
	}
	
	public static void main(String[] args) {
		System.out.println("Average Java time: " + test(100));
	}

}
