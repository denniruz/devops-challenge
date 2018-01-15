package com.wysong.wordcount;

import com.wysong.wordcount.eis.WordCountRepository;
import com.wysong.wordcount.model.Word;
import com.wysong.wordcount.model.WordCount;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class WordCountApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordCountApplication.class, args);
	}

/*
	@Bean
	CommandLineRunner init(WordCountRepository wordCountRepository) {
		return (evt) -> Arrays.asList(
				"bob,store,weave,poopy,hungry,food,beef,humbug,goofy,terrible".split(","))
				.forEach(w -> {
					WordCount wordCount = wordCountRepository.save(new WordCount(w, 2));
				});
	}
*/
}
