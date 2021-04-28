package com.tejMa.mypreparation.pojo;

import com.tejMa.mypreparation.R;

public class Question {

    private String question;
    private String[] options;
    private int tickedAns;
    private int correctAns;
    private String img;
    private int background;
    private float maxMarks;

    public Question(String question, String[] options, int correctAns, float maxMarks, String img) {
        this.question = question;
        this.options = options;
        this.correctAns = correctAns;
        this.maxMarks = maxMarks;
        this.tickedAns = -1;
        background = R.color.purple_light;
        this.img = img;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getTickedAns() {
        return tickedAns;
    }

    public void setTickedAns(int tickedAns) {
        this.tickedAns = tickedAns;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public float getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(float maxMarks) {
        this.maxMarks = maxMarks;
    }
}
