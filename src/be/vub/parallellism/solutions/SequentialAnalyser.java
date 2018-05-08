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
	
	
	public SequentialAnalyser (int database) {
		this.database = database;
	}

    public double analyze() {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        BasicConfigurator.configure();

        try {

            List<Comment> comments = Filtering.no_filter(database);
            // If you want to sequentially filter the data based on the comment (to reduce a data set), use a lambda
            // List<Comment> comments = RedditCommentLoader.readData(data, comment -> comment.body.contains("BMW"));

            // System.out.println(comments.size());

            float totalCompoundScore = 0;
            

    		long t_before_count = System.nanoTime();

            for (Comment comment : comments) {
                sentimentAnalyzer.setInputString(comment.body);
                sentimentAnalyzer.setInputStringProperties();
                sentimentAnalyzer.analyze();

                Map<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
                float commentCompoundScore = inputStringPolarity.get(ScoreType.COMPOUND);

                totalCompoundScore += commentCompoundScore;
            }
            
    		double d_filtering = (System.nanoTime()-t_before_count)/1000000.0;
            
            return d_filtering;
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
		return 0;
    }
}
