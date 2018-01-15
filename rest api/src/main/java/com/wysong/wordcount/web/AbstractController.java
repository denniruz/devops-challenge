package com.wysong.wordcount.web;

import com.wysong.wordcount.model.WordCount;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractController {

    /**
     * Convenience method to provide the output desired instead of providing the
     * normal REST attributes.
     *
     * @param wordCount the {@link WordCount} object to get data from.
     * @return a {@link Map} of data desired.
     */
    protected Map<String, Integer> massageOutput(WordCount wordCount) {
        Map<String, Integer> toreturn = new LinkedHashMap<>();
        toreturn.put(wordCount.getWord(), wordCount.getCount());
        return toreturn;
    }

    /**
     * Convenience method to provide the output desired instead of providing the
     * normal REST attributes.
     *
     * @param wordCountst a List of {@link WordCount}s to get data from.
     * @return a {@link Map} of data desired.
     */
    protected Map<String, Integer> massageOutput(List<WordCount> wordCounts) {
        Map<String, Integer> toreturn = new LinkedHashMap<>();
        for (WordCount wordCount : wordCounts) {
            toreturn.put(wordCount.getWord(), wordCount.getCount());
        }
        return toreturn;
    }

}
