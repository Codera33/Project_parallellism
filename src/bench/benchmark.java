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
import be.vub.parallellism.solutions.ParallelAnalyser;
import be.vub.parallellism.solutions.SequentialAnalyser;
import be.vub.parallellism.solutions.substringAnalyser;

public class benchmark {


	public static void main(String[] args) throws IOException {

		benchmark_f1d1();
		benchmark_f1d2();
		benchmark_f2d1();
		benchmark_f2d2();

	}

	private static void benchmark_f1d1() throws IOException {

		File f1d1_file = new File("runtimes_f1d1.csv");
		List<Integer> p_values = Arrays.asList(1, 2, 4, 8, 16);
		int nr_of_reps = 20;

		String[] data = new String[]{"./files/dataset_1.json", };
        List<Comment> comments = RedditCommentLoader.readData(data);
		
		List<Long> seqRuntimes = new ArrayList<Long>(nr_of_reps);

		for (int i = 0; i < nr_of_reps; i++) {
			System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
							// of the code
			long before = System.nanoTime(); // time is measured in ns
			SequentialAnalyser.analyze_seq_by_subreddit(comments, "AskReddit");
			seqRuntimes.add(System.nanoTime() - before);
		}

		write2file(f1d1_file, "SEQ", seqRuntimes);

		for (int p : p_values) {

			List<Long> paraRuntimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_subreddit(comments, "AskReddit", p, 250);
				paraRuntimes.add(System.nanoTime() - before);
			}

			write2file(f1d1_file, "PAR with P = " + p, paraRuntimes);
		}

		return;
	}

	private static void benchmark_f1d2() throws IOException {

		File f1d2_file = new File("runtimes_f1d2.csv");
		
		List<Integer> p_values = Arrays.asList(1, 2, 4, 8, 16);
		int nr_of_reps = 20;

		String[] data = new String[]{"./files/dataset_2.json", };
        List<Comment> comments = RedditCommentLoader.readData(data);

		List<Long> seqRuntimes = new ArrayList<Long>(nr_of_reps);

		for (int i = 0; i < nr_of_reps; i++) {
			System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
							// of the code
			long before = System.nanoTime(); // time is measured in ns
			SequentialAnalyser.analyze_seq_by_substring(comments, "BMW");
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
	
	private static void benchmark_f2d1() throws IOException {

		File f2d1_file = new File("runtimes_f2d1.csv");
		
		List<Integer> t_values = Arrays.asList(10, 100, 250, 500, 1000, 2000, 5000, 1000);
		int nr_of_reps = 20;

		String[] data = new String[]{"./files/dataset_1.json", };
        List<Comment> comments = RedditCommentLoader.readData(data);
		
		for (int t : t_values) {

			List<Long> Runtimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_substring_par_alltrue(comments, "BMW", 4, t);
				Runtimes.add(System.nanoTime() - before);
			}

			write2file(f2d1_file, "PAR with T = " + t, Runtimes);
		}
	}
	
	private static void benchmark_f2d2() throws IOException {

		File f2d2_file = new File("runtimes_f2d2.csv");
		
		List<Integer> t_values = Arrays.asList(10, 100, 250, 500, 1000, 2000, 5000, 1000);
		int nr_of_reps = 20;

		String[] data = new String[]{"./files/dataset_2.json", };
        List<Comment> comments = RedditCommentLoader.readData(data);
		
		for (int t : t_values) {

			List<Long> Runtimes = new ArrayList<Long>(nr_of_reps);

			for (int i = 0; i < nr_of_reps; i++) {
				System.gc(); // a heuristic to avoid Garbage Collection (GC) to take place in timed portion
								// of the code
				long before = System.nanoTime(); // time is measured in ns
				ParallelAnalyser.analyze_by_substring_par(comments, "BMW", 4, t);
				Runtimes.add(System.nanoTime() - before);
			}

			write2file(f2d2_file, "PAR with T = " + t, Runtimes);
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