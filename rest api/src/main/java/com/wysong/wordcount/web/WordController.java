package com.wysong.wordcount.web;

import com.wysong.wordcount.business.WordCountManager;
import com.wysong.wordcount.model.Word;
import com.wysong.wordcount.model.WordCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/word")
public class WordController {

    private final WordCountManager wordCountManager;

    @Autowired
    public WordController(WordCountManager wordCountManager) {
        this.wordCountManager = wordCountManager;
    }


    @RequestMapping(value = "/{word}", method = RequestMethod.PUT)
    public WordCount createOrReplace(@RequestBody Word word) {
        return wordCountManager.addWord(word);
    }

    @RequestMapping(value = "/{word}", method = RequestMethod.POST)
    public WordCount createOrUpdate(@PathVariable String word, @RequestBody Word wordBody) {

        return wordCountManager.addWord(wordBody);
    }

    @RequestMapping(value = "/{word}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String word) {
        WordCount wordCount = wordCountManager.removeWord(new Word(word));
        if (wordCount != null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);

    }
}
