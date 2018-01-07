package com.wysong.wordcount.web;

import com.wysong.wordcount.eis.WordCountRepository;
import com.wysong.wordcount.model.WordCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/words")
public class WordCountController extends AbstractController {

    private final WordCountRepository wordCountRepository;

    @Autowired
    WordCountController(WordCountRepository wordCountRepository) {
        this.wordCountRepository = wordCountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Integer> getAllWords() {
        List<WordCount> wordCounts = wordCountRepository.findAll();
        return massageOutput(wordCounts);
    }

    @RequestMapping(value = "/{word}", method = RequestMethod.GET)
    public Map<String, Integer> readWordCount(@PathVariable String word) {
        Optional<WordCount> optionalWordCount = wordCountRepository.findByWord(word);
        WordCount wordCount = optionalWordCount.orElseThrow(() -> new WordNotFoundException(word));
        return massageOutput(wordCount);
    }
}
