package be.vub.parallellism.solutions;



import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.util.ScoreType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.BasicConfigurator;


import java.util.concurrent.RecursiveTask;




public class CommentArrayAnalyser extends RecursiveTask<Float> {
	private static final long serialVersionUID = 1L;

	private final List<Comment> comments;
	private final int sequentialCutOff;
	private int low;
	private int high;
	private float result;
	private final String keyword;
	private final boolean look_by_subreddit;

	
	CommentArrayAnalyser(List<Comment> comments, String subReddit, int sequentialCutOff, boolean look_by_subreddit) {
		
		this(comments, subReddit, sequentialCutOff,look_by_subreddit, 0, comments.size());
		
	}
	
	CommentArrayAnalyser(List<Comment> comments, String keyword, int sequentialCutOff, boolean look_by_subreddit, int low, int high) {

		this.comments = comments;;
		this.sequentialCutOff = sequentialCutOff;
		this.low = low;
		this.high = high;
		this.keyword = keyword;
		this.look_by_subreddit = look_by_subreddit;
		
	}
	
	protected Float compute() {
		if(high-low < sequentialCutOff){
			try {
				return analyseChunkOfArray(comments.subList(low, high));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			int pivot = (low + high)/2;
			CommentArrayAnalyser left_task = new CommentArrayAnalyser(comments,keyword,sequentialCutOff,look_by_subreddit,low,pivot);
			CommentArrayAnalyser right_task = new CommentArrayAnalyser(comments,keyword,sequentialCutOff,look_by_subreddit,pivot,high);
			right_task.fork();
			float left_count = left_task.compute();
			float right_count = right_task.join();
			return left_count + right_count;
		}
		return result;
	}

	private float analyseChunkOfArray(List<Comment> comments) throws IOException {

		float totalCompoundScore = 0;
		


	    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

		for (Comment c : comments) {
			if (look_by_subreddit) {
					sentimentAnalyzer.setInputString(c.body);
					sentimentAnalyzer.setInputStringProperties();
					sentimentAnalyzer.analyze();

					Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
					float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

					totalCompoundScore += commentCompoundScore;
			} else {
				if ((c.body).contains(keyword)) {
					sentimentAnalyzer.setInputString(c.body);
					sentimentAnalyzer.setInputStringProperties();
					sentimentAnalyzer.analyze();

					Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
					float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

					totalCompoundScore += commentCompoundScore;

				}

			}
		}
		return totalCompoundScore;
	}
}
