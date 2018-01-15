package com.wysong.wordcount.eis;

import com.wysong.wordcount.model.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface that extends the Spring JpaRepository that gives us CRUD operations and more.
 *
 */
public interface WordCountRepository extends JpaRepository<WordCount, Long> {
    /**
     * Finds the {@link WordCount} that has the provided word.  Returns an
     * {@link Optional} that has a null value if the provided word is not found.
     * @param word the word to find in repository of {@link WordCount}s.
     * @return  Returns the {@link Optional} object that either contains the
     * WorkCount that has the provided word or a null value.
     */

    Optional<WordCount> findByWord(String word);
}
