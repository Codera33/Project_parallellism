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
	
	public ParallelAnalyser (int database, String subreddit, int nrOfProcessors, int sequentialCutOff) {

		this.dataSetNr = database;
		this.subreddit = subreddit;
		this.nrOfProcessors = nrOfProcessors;
		this.sequentialCutOff = sequentialCutOff;
		
	}

	public double analyze() {
		

        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        BasicConfigurator.configure();


		try {

			String[] data = new String[] { "./files/dataset_" + Integer.toString(dataSetNr) + ".json", };

			List<Comment> comments = RedditCommentLoader.readData(data);
			

			long t_before_count = System.nanoTime();

			// create a fork join (thread) pool with p workers
			ForkJoinPool forkJoinPool = new ForkJoinPool(nrOfProcessors);

			float res = forkJoinPool.invoke(new CommentArrayAnalyser(comments, nrOfProcessors, sequentialCutOff));

			// return the result

			double d_filtering = (System.nanoTime()-t_before_count)/1000000.0;
			System.out.println(d_filtering);
			System.out.println(res / comments.size());
			
			return d_filtering;

		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return sequentialCutOff;
	}
}
