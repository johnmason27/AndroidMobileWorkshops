package uk.ac.wlv.geoquiz;

public class Question {
    private int textResId;
    private boolean answer;

    public Question(int textResId, boolean answer) {
        this.textResId = textResId;
        this.answer = answer;
    }

    public int getTextResId() {
        return this.textResId;
    }

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }

    public boolean getAnswer() {
        return this.answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
