package com.wysong.wordcount.business;

import com.wysong.wordcount.eis.WordCountRepository;
import com.wysong.wordcount.model.Word;
import com.wysong.wordcount.model.WordCount;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultWordCountManager implements WordCountManager {

    private final WordValidator wordValidator;
    private final WordCountRepository wordCountRepository;

    public DefaultWordCountManager(WordValidator wordValidator, WordCountRepository wordCountRepository) {
        this.wordValidator = wordValidator;
        this.wordCountRepository = wordCountRepository;
    }

    @Override
    public WordCount addWord(Word word) throws InvalidWordException {
        wordValidator.validate(word);
        Optional<WordCount> optionalWordCount = wordCountRepository.findByWord(word.getWord());
        if (optionalWordCount.isPresent()) {
            WordCount wordCount = optionalWordCount.get();
            wordCount.setCount(wordCount.getCount()+1);
            wordCountRepository.save(wordCount);
            return wordCount;
        } else {
            WordCount wordCount = new WordCount(word.getWord(), 1);
            wordCountRepository.save(wordCount);
            return wordCount;
        }
    }

    @Override
    public WordCount removeWord(Word word) {
        Optional<WordCount> optionalWordCount = wordCountRepository.findByWord(word.getWord());
        if (optionalWordCount.isPresent()) {
            WordCount wordCount = optionalWordCount.get();
            wordCountRepository.delete(wordCount);
            return wordCount;
        }
        return null;
    }


}
