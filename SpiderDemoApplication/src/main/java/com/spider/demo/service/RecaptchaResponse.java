package com.spider.demo.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecaptchaResponse {
    private boolean success;
    private float score;
    @JsonProperty("challenge_ts")
    private String challengeTs;
    private String hostname;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    public void setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
