package uk.ac.wlv.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER = "uk.ac.wlv.geoquiz.answer";
    private static final String EXTRA_ANSWER_SHOWN = "uk.ac.wlv.geoquiz.answer_shown";
    private boolean answer;
    private TextView answerTextView;
    private Button showAnswerButton;

    public static Intent createIntent(Context packageContext, boolean answer) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER, answer);
        return i;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        this.answer = getIntent().getBooleanExtra(EXTRA_ANSWER, false);
        this.answerTextView = findViewById(R.id.answer_text_view);
        this.showAnswerButton = findViewById(R.id.show_answer_button);

        this.showAnswerButton.setOnClickListener(view -> {
            if (answer) {
                this.answerTextView.setText(R.string.true_button);
            } else {
                this.answerTextView.setText(R.string.false_button);
            }

            this.setAnswerShownResult(true);
        });
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }
}