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

/**
 * Controller to handle the endpoints with the root of /words.
 */
@RestController
@RequestMapping("/words")
public class WordCountController extends AbstractController {

    private final WordCountRepository wordCountRepository;

    @Autowired
    WordCountController(WordCountRepository wordCountRepository) {
        this.wordCountRepository = wordCountRepository;
    }

    /***
     * Endpoint to handle GET /words.  Will return all words provided
     * as a map of key value pairs with the key being the word and the
     * value being the number of times that word has been added to the api.
     * @return Map of words and how many times the word has been submitted.
     */
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Integer> getAllWords() {
        List<WordCount> wordCounts = wordCountRepository.findAll();
        return massageOutput(wordCounts);
    }

    /**
     * Endpoint to handle GET /words/{word}.  Will return the provided
     * word and how many times that word was submitted to the REST api or will
     * throw an {@link WordNotFoundException} if the provided word is not found.
     * @param word The desired word to get.
     * @return The word and it's count as a key value pair in a map.
     */
    @RequestMapping(value = "/{word}", method = RequestMethod.GET)
    public Map<String, Integer> readWordCount(@PathVariable String word) {
        Optional<WordCount> optionalWordCount = wordCountRepository.findByWord(word);
        WordCount wordCount = optionalWordCount.orElseThrow(() -> new WordNotFoundException(word));
        return massageOutput(wordCount);
    }
}
