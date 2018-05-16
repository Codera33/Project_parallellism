package be.vub.parallellism.solutions;

import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.util.ScoreType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.BasicConfigurator;

public class ParallelAnalyser {
	

	int dataSetNr;
	String subreddit;
	int nrOfProcessors;
	int sequentialCutOff;
	
	public ParallelAnalyser (int database, String subreddit, int sequentialCutOff) {

		this.dataSetNr = database;
		this.subreddit = subreddit;
		this.sequentialCutOff = sequentialCutOff;
		
	}

	public static float analyze_by_subreddit(List<Comment> comments, String subReddit, int nrOfProcessors, int sequentialCutOff) {
		

		long t_before_count = System.nanoTime();

		// create a fork join (thread) pool with p workers
		ForkJoinPool forkJoinPool = new ForkJoinPool(nrOfProcessors);

		float res = forkJoinPool.invoke(new CommentArrayAnalyser(comments, subReddit, sequentialCutOff, true));

		// return the result

		double d_filtering = (System.nanoTime()-t_before_count)/1000000.0;
		System.out.println(d_filtering);
		System.out.println(res / comments.size());
		
		return res;
	}
	
	public static float analyze_by_substring_seq(List<Comment> comments, String substring, int nrOfProcessors, int sequentialCutOff) {
		

		long t_before_count = System.nanoTime();

		// create a fork join (thread) pool with p workers
		ForkJoinPool forkJoinPool = new ForkJoinPool(nrOfProcessors);

		float res = forkJoinPool.invoke(new CommentArrayAnalyser(comments, substring, sequentialCutOff, false));

		// return the result

		double d_filtering = (System.nanoTime()-t_before_count)/1000000.0;
		System.out.println(d_filtering);
		System.out.println(res / comments.size());
		
		return res;
	}
	
public static float analyze_by_substring_par_alltrue(List<Comment> comments, String substring, int nrOfProcessors, int sequentialCutOff) {
		

		long t_before_count = System.nanoTime();

		// create a fork join (thread) pool with p workers
		ForkJoinPool forkJoinPool = new ForkJoinPool(nrOfProcessors);

		float res = forkJoinPool.invoke(new ParallelSubstringAnalyser(comments, substring, nrOfProcessors, sequentialCutOff, false));

		// return the result

		double d_filtering = (System.nanoTime()-t_before_count)/1000000.0;
		System.out.println(d_filtering);
		System.out.println(res / comments.size());
		
		return res;
	}
	
public static float analyze_by_substring_par(List<Comment> comments, String substring, int nrOfProcessors, int sequentialCutOff) {
	

	long t_before_count = System.nanoTime();

	// create a fork join (thread) pool with p workers
	ForkJoinPool forkJoinPool = new ForkJoinPool(nrOfProcessors);

	float res = forkJoinPool.invoke(new ParallelSubstringAnalyser(comments, substring, nrOfProcessors, sequentialCutOff, true));

	// return the result

	double d_filtering = (System.nanoTime()-t_before_count)/1000000.0;
	System.out.println(d_filtering);
	System.out.println(res / comments.size());
	
	return res;
}
}
