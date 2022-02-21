package me.rgunny.study.javaenum;

import lombok.Getter;

@Getter
public enum SocialType {
    GOOGLE("GOOGLE"), GITHUB("GITHUB");

    private String socialType;

    SocialType(String socialType) {
        this.socialType = socialType;
    }
}