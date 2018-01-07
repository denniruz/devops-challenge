package com.wysong.wordcount.eis;

import com.wysong.wordcount.model.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordCountRepository extends JpaRepository<WordCount, Long> {
    Optional<WordCount> findByWord(String word);
}
