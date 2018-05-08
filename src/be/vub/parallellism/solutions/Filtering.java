package be.vub.parallellism.solutions;

import java.io.IOException;
import java.util.List;

import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;

public interface Filtering {
	

	public static List<Comment> no_filter (int dataset) throws IOException {
		

        // Multiple files can be provided. These will be read one after the other.
		String[] data = new String[]{"./files/dataset_" + Integer.toString(dataset) + ".json", };
        List<Comment> comments = RedditCommentLoader.readData(data);
		
		return comments;
	}
	
	public static List<Comment> filter_seq_by_subreddit (int dataset, String subreddit) throws IOException {
		
		String[] data = new String[]{"./files/dataset_" + Integer.toString(dataset) + ".json", };
		List<Comment> comments = RedditCommentLoader.readData(data, comment -> comment.subreddit.contains(subreddit));
		
		return comments;
	}
	
	
}
