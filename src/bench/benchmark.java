package bench;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import com.vader.sentiment.analyzer.SentimentAnalyzer;

import org.apache.log4j.BasicConfigurator;

import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;
import be.vub.parallellism.solutions.CommentArrayAnalyser;
import be.vub.parallellism.solutions.Filtering;
import be.vub.parallellism.solutions.ParallelAnalyser;
import be.vub.parallellism.solutions.SequentialAnalyser;
import be.vub.parallellism.solutions.substringAnalyser;

public class benchmark {

	static int fase;
	static int database;
	static int nr_of_reps;
	static boolean Serenity = false;
	SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

	List<Integer> p_values = Arrays.asList(2, 4, 8, 16);

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Please provide '# repetitions to perform' as commandline argument.");
			return;
		}

		fase = Integer.parseInt(args[0]);
		database = Integer.parseInt(args[1]);
		nr_of_reps = Integer.parseInt(args[2]);
		List<Integer> p_values;

		p_values = new ArrayList<Integer>(args.length - 3);
		for (int i = 3; i < args.length; i++) {
			p_values.add(Integer.parseInt(args[i]));
		}
		org.apache.log4j.BasicConfigurator.configure();

		System.out.println(p_values);

		try {
			benchmark cls = new benchmark();
			Class c = cls.getClass();
			String methodName = new String("benchmark_f" + Integer.toString(fase) + "d" + Integer.toString(database));
			Method method = c.getDeclaredMethod(methodName, null);
			method.invoke(cls, null);
		} catch (NoSuchMethodException e) {
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	private void benchmark_f1d1() throws IOException {

		File f1d1_file = new File("runtimes_f1d1.csv");

		List<Comment> comments = Filtering.no_filter(1);

		List<Long> seqRuntimes = new ArrayList<Long>(nr_of_reps);

		for (int i = 0; i < nr_of_reps; i++) {
			System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
							// of the code
			long before = System.nanoTime(); // time is measured in ns
			SequentialAnalyser.analyze_seq_by_subreddit(comments, "AskReddit", sentimentAnalyzer);
			seqRuntimes.add(System.nanoTime() - before);
		}

		write2file(f1d1_file, "SEQ", seqRuntimes);

		for (int p : p_values) {

			List<Long> paraRuntimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_subreddit(comments, "AskReddit", p, 2000);
				paraRuntimes.add(System.nanoTime() - before);
			}

			write2file(f1d1_file, "PAR with P = " + p, paraRuntimes);
		}

		return;
	}

	private void benchmark_f1d2() throws IOException {

		File f1d2_file = new File("runtimes_f1d2.csv");

		List<Comment> comments = Filtering.no_filter(3);

		List<Long> seqRuntimes = new ArrayList<Long>(nr_of_reps);

		for (int i = 0; i < 1; i++) {
			System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
							// of the code
			long before = System.nanoTime(); // time is measured in ns
			SequentialAnalyser.analyze_seq_by_substring(comments, "BMW", sentimentAnalyzer);
			seqRuntimes.add(System.nanoTime() - before);
		}

		write2file(f1d2_file, "SEQ", seqRuntimes);

		for (int p : p_values) {

			List<Long> paraRuntimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_substring_seq(comments, "BMW", p, 200);
				paraRuntimes.add(System.nanoTime() - before);
			}

			write2file(f1d2_file, "PAR with P = " + p, paraRuntimes);
		}

		return;

	}
	
	private void benchmark_f2d1() throws IOException {

		File f2d1_file = new File("runtimes_f2d1.csv");

		List<Comment> comments = Filtering.no_filter(3);
		
		int sequentialCutOff = 2;
		
		for (int p : p_values) {

			List<Long> paraRuntimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_substring_par_alltrue(comments, "BMW", p, sequentialCutOff);
				paraRuntimes.add(System.nanoTime() - before);
			}

			write2file(f2d1_file, "PAR with P = " + p, paraRuntimes);
		}
	}
	
	private void benchmark_f2d2() throws IOException {

		File f2d2_file = new File("runtimes_f2d2.csv");

		List<Comment> comments = Filtering.no_filter(3);
		
		int sequentialCutOff = 5;
		
		for (int p : p_values) {

			List<Long> paraRuntimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_substring_par(comments, "BMW", p, sequentialCutOff);
				paraRuntimes.add(System.nanoTime() - before);
			}

			write2file(f2d2_file, "PAR with P = " + p, paraRuntimes);
		}
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