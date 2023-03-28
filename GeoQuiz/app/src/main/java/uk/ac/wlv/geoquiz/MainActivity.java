package uk.ac.wlv.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_INDEX = "index";
    private static final String KEY_RATING = "rating";
    private static final String KEY_CHEATED = "cheated";
    private static final String KEY_CHEATED_ARRAY = "cheated_array";
    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button trueButton;
    private Button falseButton;
    private Button cheatButton;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private TextView questionTextView;
    private TextView ratingTextView;
    private Question[] questions = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),
        new Question(R.string.question_europe, true),
        new Question(R.string.question_new_zealand, false),
        new Question(R.string.question_england, false),
        new Question(R.string.question_australia, true),
        new Question(R.string.question_antarctica, false)
    };
    private ArrayList cheatedIndexes = new ArrayList<Integer>();
    private int currentQuestionIndex = 0;
    private int currentSuccessRating = 0;
    private boolean isCheater;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }

            this.isCheater = CheatActivity.wasAnswerShown(data);

            if (this.isCheater) {
                this.cheatedIndexes.add(this.currentQuestionIndex);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            this.currentQuestionIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            this.currentSuccessRating = savedInstanceState.getInt(KEY_RATING, 0);
            this.isCheater = savedInstanceState.getBoolean(KEY_CHEATED, false);
            // TODO Potential null pointer
            this.cheatedIndexes = savedInstanceState.getIntegerArrayList(KEY_CHEATED_ARRAY);
        }

        this.questionTextView = findViewById(R.id.question_test_view);
        this.updateQuestion();

        this.ratingTextView = findViewById(R.id.rating_text_view);
        this.ratingTextView.setText(String.format("Success rate is %d%%", this.currentSuccessRating));

        this.trueButton = findViewById(R.id.true_button);
        this.falseButton = findViewById(R.id.false_button);
        this.previousButton = findViewById(R.id.previous_button);
        this.nextButton = findViewById(R.id.next_button);
        this.cheatButton = findViewById(R.id.cheat_button);

        this.trueButton.setOnClickListener(view -> this.checkAnswer(true));
        this.falseButton.setOnClickListener(view -> this.checkAnswer(false));
        this.previousButton.setOnClickListener(view -> {
            if (this.currentQuestionIndex - 1 < 0) {
                this.currentQuestionIndex = this.questions.length - 1;
            } else {
                this.currentQuestionIndex = (this.currentQuestionIndex - 1) % this.questions.length;
            }
            this.isCheater = false;
            this.updateQuestion();
        });
        this.nextButton.setOnClickListener(view -> {
            this.currentQuestionIndex = (this.currentQuestionIndex + 1) % this.questions.length;
            this.isCheater = false;
            this.updateQuestion();
        });
        this.cheatButton.setOnClickListener(view -> {
            boolean answer = this.questions[this.currentQuestionIndex].getAnswer();
            Intent i = CheatActivity.createIntent(MainActivity.this, answer);
            // TODO Understand how to replace with the none deprecated function.
            startActivityForResult(i, REQUEST_CODE_CHEAT);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX, this.currentQuestionIndex);
        savedInstanceState.putInt(KEY_RATING, this.currentSuccessRating);
        savedInstanceState.putBoolean(KEY_CHEATED, this.isCheater);
        savedInstanceState.putIntegerArrayList(KEY_CHEATED_ARRAY, this.cheatedIndexes);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = this.questions[this.currentQuestionIndex].getTextResId();
        this.questionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressed) {
        boolean answer = this.questions[this.currentQuestionIndex].getAnswer();
        int messageResId = 0;

        if (this.isCheater || this.cheatedIndexes.contains(this.currentQuestionIndex)) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressed == answer) {
                messageResId = R.string.correct_toast;
                this.updateSuccessRating();
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void updateSuccessRating() {
        int part = 100 / this.questions.length;
        this.currentSuccessRating += part;
        if (this.currentSuccessRating == 100) {
            this.ratingTextView.setText("Congratulations you got all questions correct! 100%");
        } else {
            this.ratingTextView.setText(String.format("Success rate is %d%%", this.currentSuccessRating));
        }
    }
}