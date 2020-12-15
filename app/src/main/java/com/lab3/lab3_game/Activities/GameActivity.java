package com.lab3.lab3_game.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lab3.lab3_game.CreateGameField.CurrentGameFieldMode;
import com.lab3.lab3_game.Structures.MoveResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lab3.lab3_game.CreateGameField.GameFieldView;
import com.lab3.lab3_game.R;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private ValueEventListener moveChangesListener;

    private boolean started_game;
    private GameFieldViewModel gameFieldViewModel;
    private PopupWindow mPopupWindow;
    private PopupWindow endOfTheGameWindow;
    private ProgressBar mProgressBar;
    private GameFieldView player_1_field;
    private GameFieldView player_2_field;
    private TextView player_1_name;
    private TextView player_2_name;
    private TextView player_1_name_field;
    private TextView player_2_name_field;
    private TextView player_1_scoreView;
    private TextView player_2_scoreView;
    private View game_holder;
    private TextView myTurnView;
    private TextView waitForMyTurnView;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        started_game = getIntent().getBooleanExtra("start", false);
        mProgressBar = findViewById(R.id.progressBarGAMEWAIT);
        mProgressBar.setVisibility(View.VISIBLE);
        gameFieldViewModel = ViewModelProviders.of(this).get(GameFieldViewModel.class);
        myTurnView = findViewById(R.id.my_turn);
        waitForMyTurnView = findViewById(R.id.wait_for_my_turn);
        myTurnView.setVisibility(View.GONE);
        waitForMyTurnView.setVisibility(View.GONE);
        game_holder = findViewById(R.id.game_holder);

        player_1_name = findViewById(R.id.player1_name);
        player_1_name_field = findViewById(R.id.player_1_field_name);
        player_1_scoreView = findViewById(R.id.score_player1);
        player_2_name = findViewById(R.id.player2_name);
        player_2_name_field = findViewById(R.id.player_2_field_name);
        player_2_scoreView = findViewById(R.id.score_player2);

        player_1_field = findViewById(R.id.player1_field);
        player_2_field = findViewById(R.id.player2_field);
        player_1_field.initializeField(CurrentGameFieldMode.PLAYER1);
        player_2_field.initializeField(CurrentGameFieldMode.PLAYER2);
        player_2_field.setFieldMode(CurrentGameFieldMode.READONLY);

        gameFieldViewModel.Inicialize(getIntent().getStringExtra("id"), started_game);
        gameFieldViewModel.returnMes().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                Snackbar.make(game_holder, value, BaseTransientBottomBar.LENGTH_SHORT);
            }
        });
        gameFieldViewModel.returnName(1).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                player_1_name.setText(value);
            }
        });
        gameFieldViewModel.isGameEnded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value == false)
                {
                    gameEnded(false);
                }
                else
                {
                    gameEnded(true);
                }
            }
        });
        gameFieldViewModel.returnName(2).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                player_2_name.setText(value);
            }
        });
        gameFieldViewModel.returnName_field(1).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                player_1_name_field.setText(value);
            }
        });
        gameFieldViewModel.returnName_field(2).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                player_2_name_field.setText(value);
            }
        });
        gameFieldViewModel.returnScore(1).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                player_1_scoreView.setText(value);
            }
        });
        gameFieldViewModel.getGameFieldView(1).observe(this, new Observer<GameFieldView>() {
            @Override
            public void onChanged(GameFieldView value) {
                player_1_field = value;
            }
        });
        gameFieldViewModel.getGameFieldView(2).observe(this, new Observer<GameFieldView>() {
            @Override
            public void onChanged(GameFieldView value) {
                player_2_field = value;
            }
        });
        gameFieldViewModel.returnScore(2).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                player_2_scoreView.setText(value);
            }
        });
        gameFieldViewModel.addPlayer_2Listener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!Objects.requireNonNull(dataSnapshot.getValue(String.class)).equals("")) {
                    gameFieldViewModel.initFirstPlayerField();
                    gameFieldViewModel.initSecondPlayerField();
                    trackCurrentMove();
                    gameFieldViewModel.trackScore1Update();
                    gameFieldViewModel.trackScore2Update();
                    gameFieldViewModel.initStatsView();
                    mProgressBar.setVisibility(View.GONE);
                    gameFieldViewModel.removePlayer2Listener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FloatingActionButton showHintButton = findViewById(R.id.floatingActionButtonInfo);
        showHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHint();
            }
        });

    }


    private void trackCurrentMove()
    {
        moveChangesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!gameFieldViewModel.getSecondPlayerJoined())
                    return;
                if (dataSnapshot.getValue(String.class) == null)
                {
                    onBackPressed();
                    return;
                }
                String value = dataSnapshot.getValue(String.class);
                if (started_game)
                {
                    if (Objects.requireNonNull(value).equals("p_1_move"))
                    {
                        currentMoveMessage(true);
                        gameFieldViewModel.setStatusGame(CurrentGameFieldMode.PLAYER2);
                    }
                    else {
                        currentMoveMessage(false);
                        gameFieldViewModel.setStatusGame(CurrentGameFieldMode.READONLY);
                    }
                }
                else {
                    if (Objects.requireNonNull(value).equals("p_2_move"))
                    {
                        currentMoveMessage(true);
                        gameFieldViewModel.setStatusGame(CurrentGameFieldMode.PLAYER2);
                    }
                    else
                    {
                        currentMoveMessage(false);
                        gameFieldViewModel.setStatusGame(CurrentGameFieldMode.READONLY);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        gameFieldViewModel.addMoveChangesListener(moveChangesListener);
    }


    @Override
    public void onBackPressed() {
        gameFieldViewModel.RemoveGame();
        super.onBackPressed();
    }

    public void updatingMove(MoveResult moveResult)
    {
        gameFieldViewModel.updatingMove(moveResult, started_game);
    }

    private void currentMoveMessage(boolean your)
    {
        gameFieldViewModel.setMessage(your);
        if (your)
        {
            myTurnView.setVisibility(View.VISIBLE);
            waitForMyTurnView.setVisibility(View.GONE);
        }
        else {
            myTurnView.setVisibility(View.GONE);
            waitForMyTurnView.setVisibility(View.VISIBLE);
        }
    }

    private void gameEnded(boolean didWin)
    {
        gameFieldViewModel.setRes(didWin);
        gameFieldViewModel.saveStats();
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.end_page, null);
        endOfTheGameWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        endOfTheGameWindow.setElevation(5.0f);
        statusView = customView.findViewById(R.id.win_status);
        gameFieldViewModel.returnStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                statusView.setText(value);
            }
        });

        ImageView kittyView = customView.findViewById(R.id.imageViewEND_OF_THE_GAME);
        if (didWin)
            kittyView.setImageResource(R.drawable.fish_small);
        else
            kittyView.setImageResource(R.drawable.fish_bad);
        Button okButton = customView.findViewById(R.id.ok_end_of_the_game);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameFieldViewModel.RemoveGame();
                endOfTheGameWindow.dismiss();
                onBackPressed();
            }
        });
    }


    private void showHint()
    {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.rules, null);
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        mPopupWindow.setElevation(5.0f);
        Button okButton = customView.findViewById(R.id.rules_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
    }

}