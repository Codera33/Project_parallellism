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
import java.util.*;
import java.util.concurrent.*;

public class ParallelSubstringAnalyser extends RecursiveTask<Float> {
	
	String keyword;
	int nr_of_processors;
	List<Comment> comments;
	int sequentialCutOff;
	boolean check_for_word;
	private final int low;
	private final int high;
	private float result;
    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

	public ParallelSubstringAnalyser (List<Comment> comments, String keyword, int nrOfProcessors, int sequentialCutOff, boolean check_for_word) {
		
		this(comments,keyword, nrOfProcessors, sequentialCutOff,check_for_word , 0, comments.size());
		
	}
	

	
	public ParallelSubstringAnalyser (List<Comment> comments, String keyword, int nrOfProcessors, int sequentialCutOff, boolean check_for_word, int low, int high) {
		
		this.keyword = keyword;
		this.nr_of_processors = nrOfProcessors;
		this.comments = comments;
		this.sequentialCutOff = sequentialCutOff;
		this.low = low;
		this.high = high;
		this.check_for_word = check_for_word;
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
			ParallelSubstringAnalyser left_task = new ParallelSubstringAnalyser(comments,keyword,nr_of_processors,sequentialCutOff,check_for_word,low,pivot);
			ParallelSubstringAnalyser right_task = new ParallelSubstringAnalyser(comments,keyword,nr_of_processors,sequentialCutOff,check_for_word,pivot,high);
			right_task.fork();
			float left_count = left_task.compute();
			float right_count = right_task.join();
			return left_count + right_count;
		}
		return result;
	}

	private float analyseChunkOfArray(List<Comment> comments) throws IOException {

		float totalCompoundScore = 0;

		for (Comment c : comments) {
			if (new substringAnalyser(c.body, keyword, 2, check_for_word).compute()) {
				sentimentAnalyzer.setInputString(c.body);
				sentimentAnalyzer.setInputStringProperties();
				sentimentAnalyzer.analyze();

				Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
				float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

				totalCompoundScore += commentCompoundScore;
			}

		}
		return totalCompoundScore;
	}

}
