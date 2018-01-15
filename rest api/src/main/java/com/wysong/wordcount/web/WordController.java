package com.wysong.wordcount.web;

import com.wysong.wordcount.business.WordCountManager;
import com.wysong.wordcount.model.Word;
import com.wysong.wordcount.model.WordCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller to handle the endpoints with the root of /word.
 */
@RestController
@RequestMapping("/word")
public class WordController extends AbstractController {

    private final WordCountManager wordCountManager;

    @Autowired
    public WordController(WordCountManager wordCountManager) {
        this.wordCountManager = wordCountManager;
    }

    /**
     * Endpoint to handle PUT /word/{word}.  This endpoint should be idempotent,
     * but for this exercise we will have a different server state for each call.
     *
     * This endpoint will increment the word counter of the word found in the
     * request body.  Note that the word provided on the endpoint is ignored.  If the
     * provided word does not exist, the word will be added to the repository
     * with the count of one.
     *
     * @param word The desired word found in the request body to increment the word count for.
     * @return a map containing the word and the word's new count.
     */
    @RequestMapping(value = "/{ignoredWord}", method = RequestMethod.PUT)
    public Map<String, Integer> createOrReplace(@RequestBody Word word) {
        WordCount wordCount = wordCountManager.addWord(word);
        return massageOutput(wordCount);
    }

    /**
     * Endpoint to handle POST /word/{word}.
     *
     * This endpoint will increment the provided request body word's counter by one.
     * If the provided word does not exist, the word will be added to the repository
     * with the count of one.
     *
     * @param word Should be the desired word to increment the word count,
     *            but is ignored for this exercise.
     * @return a map containing the persited word and the word's new count.
     */
    @RequestMapping(value = "/{word}", method = RequestMethod.POST)
    public Map<String, Integer> createOrUpdate(@PathVariable String word, @RequestBody Word wordBody) {

        WordCount wordCount = wordCountManager.addWord(wordBody);
        return massageOutput(wordCount);
    }

    /**
     * Deletes the provided word from the repository.
     * @param word the word desired to be removed from the repository.
     * @return either a {@link HttpStatus} of NO_CONTENT (204) if the word was removed,
     * otherwise return a NOT_FOUND (404).
     */
    @RequestMapping(value = "/{word}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String word) {
        WordCount wordCount = wordCountManager.removeWord(new Word(word));
        if (wordCount != null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

}
