package com.wysong.wordcount.model;

import org.springframework.util.ObjectUtils;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Word extends PersistentEntity {

    @NotNull
    private String word;

    public Word() {
    }

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    @Override
    protected boolean onEquals(PersistentEntity e) {
        if (e instanceof Word) {
            Word w = (Word) e;
            return ObjectUtils.nullSafeEquals(getWord(), w.getWord());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(getWord());
    }

    @Override
    public StringBuilder toStringBuilder() {
        return new StringBuilder().append("word=").append(getWord());
    }
}
