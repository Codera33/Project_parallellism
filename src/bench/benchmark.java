package bench;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import be.vub.parallellism.solutions.ParallelAnalyser;
import be.vub.parallellism.solutions.SequentialAnalyser;

public class benchmark {
	

	static int fase;
	static int database;
	static int nr_of_reps;
	List<Integer> p_values;
	

	public static void main(String[] args) {
		

		if (args.length < 1) {
			System.out.println("Please provide '# repetitions to perform' as commandline argument.");
			return;
		}

		fase = Integer.parseInt(args[1]);
		database = Integer.parseInt(args[0]);
		nr_of_reps = Integer.parseInt(args[2]);
		List<Integer> p_values;

		p_values = new ArrayList<Integer>(args.length - 1);
		for (int i = 3; i < args.length; i++) {
			p_values.add(Integer.parseInt(args[i]));
		}
		
	      try {          
	  		benchmark cls = new benchmark();
	  	    Class c = cls.getClass();
	  		String methodName = new String ("benchmark_f" + Integer.toString(fase) + "d" + Integer.toString(database));
	  		Method method = c.getDeclaredMethod(methodName, null);
	          method.invoke(cls, null);
	       } catch(NoSuchMethodException e) {
	          System.out.println(e.toString());
	       } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (InvocationTargetException e) {
	            e.printStackTrace();
	        }

	}
	
	private void benchmark_f1d1() {

		File res_file = new File("runtimes_f1d1.csv");
		

		List<Long> runtimes = new ArrayList<Long>(nr_of_reps);

		for(int i = 0; i < nr_of_reps; i++){
			SequentialAnalyser seqAnalyser = new SequentialAnalyser(6);
			System.gc(); //a heuristic to avoid Garbage Collection (GC) to take place in timed portion of the code
			long before = System.nanoTime(); //time is measured in ns
			seqAnalyser.analyze();
			runtimes.add(System.nanoTime()-before);
		}
		
		write2file(res_file, "SEQ", runtimes);

		List<Long> runtimes2 = new ArrayList<Long>(nr_of_reps);


		for(int i = 0; i < nr_of_reps; i++){
			ParallelAnalyser ParAnalyser = new ParallelAnalyser(6, "BMW", 4, 2000);
			System.gc(); //a heuristic to avoid Garbage Collection (GC) to take place in timed portion of the code
			long before = System.nanoTime(); //time is measured in ns
			ParAnalyser.analyze();
			runtimes2.add(System.nanoTime()-before);
		}
		
		write2file(res_file, "PAR with P = 4", runtimes2);
		
		return;
		
		
	}
	
	static private void benchmark_f1d2() {
		System.out.println("f1d2");
	}
	
	static private void benchmark_f2d1() {
		System.out.println("f2d1");
	}
	
	static private void benchmark_f2d2() {
		System.out.println("f2d2");
	}
	

	static private void write2file(File f, String s, List<Long> runtimes){
		PrintWriter csv_writer;
		try {
			csv_writer = new PrintWriter(new FileOutputStream(f,true));
			String line = ""+s;
			for(Long rt : runtimes){
				line += ","+rt;
			}
			csv_writer.println(line);
			csv_writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}