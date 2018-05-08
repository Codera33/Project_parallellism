package be.vub.parallellism.solutions;



import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.util.ScoreType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;


import java.util.concurrent.RecursiveTask;




public class CommentArrayAnalyser extends RecursiveTask<Float> {
	private static final long serialVersionUID = 1L;

    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

	private final List<Comment> comments;
	private final int nrOfProcessors;
	private final int sequentialCutOff;
	private final int low;
	private final int high;
	private float result;

	
	CommentArrayAnalyser(List<Comment> comments, int nrOfProcessors, int sequentialCutOff) {
		
		this(comments, nrOfProcessors, sequentialCutOff, 0, comments.size());
		
	}
	
	CommentArrayAnalyser(List<Comment> comments, int nrOfProcessors, int sequentialCutOff, int low, int high) {

		this.comments = comments;
		this.nrOfProcessors = nrOfProcessors;
		this.sequentialCutOff = sequentialCutOff;
		this.low = low;
		this.high = high;
		
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
			CommentArrayAnalyser left_task = new CommentArrayAnalyser(comments,nrOfProcessors,sequentialCutOff,low,pivot);
			CommentArrayAnalyser right_task = new CommentArrayAnalyser(comments,nrOfProcessors,sequentialCutOff,pivot,high);
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
            sentimentAnalyzer.setInputString(c.body);
            sentimentAnalyzer.setInputStringProperties();
            sentimentAnalyzer.analyze();

            Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
            float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

            totalCompoundScore += commentCompoundScore;
        }
		
		return totalCompoundScore;
	}
}
