package com.lab3.lab3_game.GameLogic;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lab3.lab3_game.DataBase.ModelFirebaseDatabase;
import com.lab3.lab3_game.DataBase.RegistrateUser;
import com.lab3.lab3_game.Structures.MoveResult;

public class GameController {
    private GameDB game_db;
    private Boolean gameEnded;
    private int score1 = 0;
    private int score2 = 0;

    public GameController(String gameId)
    {
        game_db = new GameDB();
        game_db.Inicialize(gameId);
    }
    public GameDB getGameDB()
    {
        return game_db;
    }

    void trackScore1Update(final Boolean started_game)
    {
        ValueEventListener score_1_changedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(int.class) == null) {
                    return;
                }
                int value = dataSnapshot.getValue(int.class);
                setScore1(value);
                if (!started_game) {
                    setScore2(getScore1());
                }
                if (value == 20) {
                    if (started_game)
                        gameEnded = true;
                    else
                        gameEnded = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        game_db.addPlayer_1Score(score_1_changedListener);
    }

    void trackScore2Update(final Boolean started_game)
    {
        ValueEventListener score_2_changedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(int.class) == null) {
                    return;
                }
                int value = dataSnapshot.getValue(int.class);
                setScore2(value);
                if (!started_game){
                    setScore2(getScore1());
                }
                if (value == 20) {
                    if (!started_game)
                        gameEnded = true;
                    else
                        gameEnded = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        game_db.addPlayer_2Score(score_2_changedListener);
    }

    public Boolean getGameEnded()
    {
        return gameEnded;
    }

    public void updatingMove(MoveResult moveResult, boolean started_game,String jsonField1, String jsonField2)
    {
        if (moveResult == MoveResult.MISS)
        {
            if (started_game)
                game_db.setMove("p_2_move");
            else
                game_db.setMove("p_1_move");
        }
        else if (moveResult == MoveResult.HIT)
        {
            if (started_game) {
                score1++;
                game_db.setPlayer_1_score(score1);
            }
            else {
                score2++;
                game_db.setPlayer_2_score(score2);
            }
        }
        else return;

        if (started_game)
        {
            game_db.setPlayer_1_field(jsonField1);
            game_db.setPlayer_2_field(jsonField2);
        }
        else {
            game_db.setPlayer_1_field(jsonField2);
            game_db.setPlayer_2_field(jsonField1);
        }

    }

    public int getScore1()
    {
        return score1;
    }

    public int getScore2()
    {
        return score2;
    }
    public void setScore1(int score)
    {
        this.score1 = score;
    }

    public void setScore2(int score)
    {
        this.score2=score;
    }


}
