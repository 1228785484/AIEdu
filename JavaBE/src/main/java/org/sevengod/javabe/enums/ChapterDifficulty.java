package org.sevengod.javabe.enums;

import lombok.Getter;

@Getter
public enum ChapterDifficulty {
    EASY(3),
    MEDIUM(9),
    HARD(18);

    private final int value;

    ChapterDifficulty(int value) {
        this.value = value;
    }

}
