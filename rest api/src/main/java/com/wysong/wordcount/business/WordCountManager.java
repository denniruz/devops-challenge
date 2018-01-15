package com.wysong.wordcount.business;

import com.wysong.wordcount.model.Word;
import com.wysong.wordcount.model.WordCount;

public interface WordCountManager {
    /**
     * Either creates or updates the WordCount for the provided word.
     *
     * @param word the word to add to the WordCount
     * @return the updated WordCount for the provided word.
     * @throws InvalidWordException if the provided word is not valid.
     */
    WordCount addWord(Word word) throws InvalidWordException;

    /**
     * Removes the provided word from existence.
     *
     * @param word the word to remove.
     * @return the {@link WordCount} of the provided word if found, otherwise returns null.
     */
    WordCount removeWord(Word word);
}
