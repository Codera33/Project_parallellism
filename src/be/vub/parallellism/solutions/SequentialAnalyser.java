package be.vub.parallellism.solutions;


import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.util.ScoreType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

public class SequentialAnalyser {

	int database;

	public SequentialAnalyser(int database) {
		this.database = database;
	}

	public static float analyze_seq_by_subreddit(List<Comment> comments, String subReddit,
			SentimentAnalyzer sentimentAnalyzer) {

		try {

			float totalCompoundScore = 0;

			long t_before_count = System.nanoTime();

			for (Comment comment : comments) {
				if ((comment.subreddit).equals(subReddit)) {
					sentimentAnalyzer.setInputString(comment.body);
					sentimentAnalyzer.setInputStringProperties();
					sentimentAnalyzer.analyze();

					Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
					float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

					totalCompoundScore += commentCompoundScore;
				}
			}
			

			return totalCompoundScore;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return 0;
	}
	
	public static float analyze_seq_by_substring(List<Comment> comments, String subString,
			SentimentAnalyzer sentimentAnalyzer) {

		try {

			float totalCompoundScore = 0;

			long t_before_count = System.nanoTime();

			for (Comment comment : comments) {
				if ((comment.body).contains(subString)) {
					sentimentAnalyzer.setInputString(comment.body);
					sentimentAnalyzer.setInputStringProperties();
					sentimentAnalyzer.analyze();

					Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
					float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

					totalCompoundScore += commentCompoundScore;
				}
			}
			

			return totalCompoundScore;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return 0;
	}
}
