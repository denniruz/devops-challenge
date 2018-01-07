package com.wysong.wordcount.web;

import com.wysong.wordcount.eis.WordCountRepository;
import com.wysong.wordcount.model.WordCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/words")
public class WordCountController {

    private final WordCountRepository wordCountRepository;

    @Autowired
    WordCountController(WordCountRepository wordCountRepository) {
        this.wordCountRepository = wordCountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<WordCount> getAllWords() {
        return wordCountRepository.findAll();
    }

    @RequestMapping(value = "/{word}", method = RequestMethod.GET)
    public WordCount readWordCount(@PathVariable String word) {
        Optional<WordCount> optionalWordCount = wordCountRepository.findByWord(word);
        return optionalWordCount.orElseThrow(() -> new WordNotFoundException(word));
    }
}
