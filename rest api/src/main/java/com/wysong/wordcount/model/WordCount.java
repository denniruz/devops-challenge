package com.wysong.wordcount.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.util.ObjectUtils;

import javax.persistence.Entity;

@Entity
@JsonIgnoreProperties(value = {"entityVersion", "entityId"})
public class WordCount extends PersistentEntity {

    private String word;
    private int count;

    public WordCount() {
    }

    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    protected boolean onEquals(PersistentEntity e) {
        if (e instanceof WordCount) {
            WordCount wc = (WordCount) e;
            return ObjectUtils.nullSafeEquals(getWord(), wc.getWord()) &&
                    ObjectUtils.nullSafeEquals(getCount(), wc.getCount());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(new Object[]{getWord(), getCount()});
    }

    @Override
    public StringBuilder toStringBuilder() {
        return new StringBuilder()
                .append("word=")
                .append(getWord())
                .append(", count=")
                .append(getCount());
    }
}
