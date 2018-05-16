package be.vub.parallellism.solutions;

import be.vub.parallellism.data.models.Comment;
import be.vub.parallellism.data.readers.RedditCommentLoader;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.util.ScoreType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.BasicConfigurator;


import java.util.concurrent.RecursiveTask;




public class substringAnalyser extends RecursiveTask<Boolean> {
	private static final long serialVersionUID = 1L;

    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

	private final List<String> wordArray;
	private final int sequentialCutOff;
	private final int first;
	private final int last;
	private final boolean check_for_word;
	private final String word;

	
	public substringAnalyser(String str, String word, int sequentialCutOff, boolean check_for_word) {
		
		this.wordArray = (Arrays.asList(str.split("\\s+")));
		this.sequentialCutOff = sequentialCutOff;
		this.first = 0;
		this.last = this.wordArray.size();
		this.check_for_word = check_for_word;
		this.word = word;
		
	}
	
	public substringAnalyser(List<String> wordArray, String word, int sequentialCutOff, boolean check_for_word, int first, int last) {

		this.wordArray = wordArray;
		this.sequentialCutOff = sequentialCutOff;
		this.first = first;
		this.last = last;
		this.check_for_word = check_for_word;		
		this.word = word;
		
	}

	@Override
	protected Boolean compute() {
		if(last-first < 2){
			//single region
			return check_for_word ? (wordArray.get(first).equals(word)) : true;
		}else if(last-first < sequentialCutOff){
			//don't split tasks filtering less than T persons
			return checkWords(wordArray.subList(first, last));
		}else{
			int pivot = (first+last)/2;
			substringAnalyser left_task = new substringAnalyser(wordArray, word, sequentialCutOff, check_for_word, first, pivot);
			substringAnalyser right_task = new substringAnalyser(wordArray, word, sequentialCutOff, check_for_word, pivot, last);
			right_task.fork();
			boolean left_count = left_task.compute();
			boolean right_count = right_task.join();
			return (left_count||right_count);
		}
	}

	private boolean checkWords(List<String> wordArray) {

		boolean contains = false;

		for (String word : wordArray) {
			if (word.equals(word)) {
				contains = true;
			}
		}
		return contains;
	}

}
