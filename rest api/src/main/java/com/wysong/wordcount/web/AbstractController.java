package com.wysong.wordcount.web;

import com.wysong.wordcount.model.WordCount;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractController {

    protected Map<String, Integer> massageOutput(WordCount wordCount) {
        Map<String, Integer> toreturn = new LinkedHashMap<>();
        toreturn.put(wordCount.getWord(), wordCount.getCount());
        return toreturn;
    }

    protected Map<String, Integer> massageOutput(List<WordCount> wordCounts) {
        Map<String, Integer> toreturn = new LinkedHashMap<>();
        for (WordCount wordCount : wordCounts) {
            toreturn.put(wordCount.getWord(), wordCount.getCount());
        }
        return toreturn;
    }

}
