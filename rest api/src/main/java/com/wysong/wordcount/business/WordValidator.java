package com.wysong.wordcount.business;

import com.wysong.wordcount.model.Word;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Assuming an invalid word is defined as containing no whitespace characters in-between other characters.
 *
 * Note that this validator <b>will</b> accept {@code null} and the empty String as valid.
 * <p/>
 * This is because validating null or the empty string is not the responsibility of this validator,
 * but of others dedicated for those purposes.  That is, if constraint users require a non-null word,
 * they should specify the additional respective annotations as necessary.  For example:
 * <p/>
 * <code>@NotNull <br/>private String word;</code>
 * <p/>
 *
 */
@Component
public class WordValidator implements Validator<Word> {

    @Override
    public void validate(Word word) throws RuntimeException {
        String cleanWord = StringUtils.trimWhitespace(word.getWord());
        if (StringUtils.isEmpty(cleanWord)) {
            //clearly a null or empty string is NOT a valid word - see the class-level JavaDoc for
            //why we still consider them 'valid'.
            return;
        }

        if (StringUtils.containsWhitespace(cleanWord)) {
            throw new InvalidWordException(cleanWord);
        }
    }
}
