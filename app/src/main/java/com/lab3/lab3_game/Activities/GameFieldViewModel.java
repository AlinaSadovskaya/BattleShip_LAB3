package com.lab3.lab3_game.Activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lab3.lab3_game.CreateGameField.CurrentGameFieldMode;
import com.lab3.lab3_game.CreateGameField.GameFieldView;
import com.lab3.lab3_game.Structures.GameField;
import com.lab3.lab3_game.Structures.MoveResult;
import com.lab3.lab3_game.DataBase.ModelFirebaseDatabase;
import com.lab3.lab3_game.DataBase.RegistrateUser;

import java.lang.reflect.Type;
import java.util.Objects;

public class GameFieldViewModel extends AndroidViewModel {
    private RegistrateUser firebaseAuth;
    private ModelFirebaseDatabase firebaseDatabase;
    private DatabaseReference player_1_field_db;
    private DatabaseReference player_2_field_db;
    private DatabaseReference player_1;
    private DatabaseReference player_2;
    private DatabaseReference player_1_score;
    private DatabaseReference player_2_score;
    private DatabaseReference currentMove;

    private final MutableLiveData<String> player_1_name = new MutableLiveData<>();
    private final MutableLiveData<String> player_2_name = new MutableLiveData<>();
    private final MutableLiveData<String> player_1_name_field = new MutableLiveData<>();
    private final MutableLiveData<String> player_2_name_field = new MutableLiveData<>();
    private final MutableLiveData<String> game_holder = new MutableLiveData<>();
    private final MutableLiveData<String> status = new MutableLiveData<>();
    private final MutableLiveData<Integer> player_1_scoreView = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> player_2_scoreView = new MutableLiveData<Integer>();
    private final MutableLiveData<GameFieldView> player_1_field = new MutableLiveData<GameFieldView>();
    private final MutableLiveData<GameFieldView> player_2_field = new MutableLiveData<GameFieldView>();
    private String gameId;
    private final MutableLiveData<Boolean> gameEnded = new MutableLiveData<Boolean>();;
    private boolean started_game;
    private boolean secondPlayerJoined = false;

    private int score1 = 0;
    private int score2 = 0;

    public GameFieldViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new RegistrateUser();
        firebaseDatabase = new ModelFirebaseDatabase();

    }

    public void Inicialize(String _gameId, boolean _started_game)
    {
        this.gameId = _gameId;
        this.started_game  = _started_game;
        DatabaseReference game = firebaseDatabase.getRef("games").child(_gameId);
        currentMove = game.child("currentMoveByPlayer");
        player_1_field_db = game.child("player_1_field");
        player_2_field_db = game.child("player_2_field");
        player_1_score = game.child("score_1");
        player_2_score = game.child("score_2");
        player_1 = game.child("player_1");
        player_2 = game.child("player_2");
    }

    public void RemoveGame()
    {
        firebaseDatabase.getRef("games").child(this.gameId).removeValue();
    }

    public int getScore1()
    {
        return score1;
    }
    public int getwinPoints()
    {
        int winPoints = 20;
        return winPoints;
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
    public void saveStats()
    {
        DatabaseReference stats = firebaseDatabase.getRef("stats").child(this.gameId);
        stats.child("player_1").setValue(player_1_name.getValue());
        stats.child("score_1").setValue(score1);
        stats.child("player_2").setValue(player_2_name.getValue());
        stats.child("score_2").setValue(score2);
    }

    public void addPlayer_2Listener(ValueEventListener player_2_listener){
        this.player_2.addValueEventListener(player_2_listener);
    }
    public void addMoveChangesListener(ValueEventListener moveChangesListener){
        this.currentMove.addValueEventListener(moveChangesListener);
    }

    public void removePlayer2Listener(ValueEventListener field_2_listener){
        this.player_2_field_db.removeEventListener(field_2_listener);
    }

    public void updatingMove(MoveResult moveResult, boolean started_game)
    {
        if (moveResult == MoveResult.MISS)
        {
            if (started_game)
                currentMove.setValue("p_2_move");
            else
                currentMove.setValue("p_1_move");
        }
        else if (moveResult == MoveResult.HIT)
        {
            game_holder.postValue("You can hit one more time. :p");
            if (started_game) {
                score1++;
                player_1_score.setValue(score1);
            }
            else {
                score2++;
                player_2_score.setValue(score2);
            }
        }
        else return;
        Gson gson = new Gson();
             String jsonField1 = gson.toJson(player_1_field.getValue().getGameField());
             String jsonField2 = gson.toJson(player_2_field.getValue().getGameField());
        if (started_game)
        {
            player_1_field_db.setValue(jsonField1);
            player_2_field_db.setValue(jsonField2);
         }
        else {
            player_1_field_db.setValue(jsonField2);
            player_2_field_db.setValue(jsonField1);
        }

    }

    public void setMessage(boolean your)
    {
        if (your)
            game_holder.postValue("Your move!");
        else
            game_holder.postValue("Second player's move!");
    }

    public void setRes(boolean didWin)
    {
        if (didWin)
            status.postValue("CONGRATULATIONS! YOU WIN!");
        else
            status.postValue("Unfortunately, you lost...");
    }

    public void setName(int user, String name)
    {
        if (user == 1)
        {
            player_1_name.postValue(name);
        }
        else
        {
            player_2_name.postValue(name);
        }
    }

    public LiveData<String> returnName(int user)
    {
        if(user == 1)
        {
            return player_1_name;
        }
        else
        {
            return player_2_name;
        }
    }

    public void setName_field(int user, String name)
    {
        if (user == 1)
        {
            player_1_name_field.postValue(name);
        }
        else
        {
            player_2_name_field.postValue(name);
        }
    }

    public LiveData<String> returnName_field(int user)
    {
        if(user == 1)
        {
            return player_1_name_field;
        }
        else
        {
            return player_2_name_field;
        }
    }


    public LiveData<String> returnStatus()
    {
        return status;
    }

    public LiveData<String> returnMes()
    {
        return game_holder;
    }

    void initStatsView()
    {
        ValueEventListener scoreView_1_listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    return;
                }
                if (started_game) {
                    setName(1, value);
                    setName_field(1, value);
                } else {
                    setName(2, value);
                    setName_field(2, value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        player_1.addValueEventListener(scoreView_1_listener);

        ValueEventListener scoreView_2_listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    return;
                }
                if (started_game) {
                    setName(2, value);
                    setName_field(2, value);

                } else {
                    setName(1, value);
                    setName_field(1, value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        player_2.addValueEventListener(scoreView_2_listener);
    }

    public void setScore(int user, int Score)
    {
        if (user == 1)
        {
            player_1_scoreView.setValue(Score);
        }
        else
        {
            player_2_scoreView.setValue(Score);
        }
    }

    public LiveData<Integer> returnScore(int user)
    {
        if(user == 1)
        {
            return player_1_scoreView;
        }
        else
        {
            return player_2_scoreView;
        }
    }

    void trackScore1Update()
    {
        ValueEventListener score_1_changedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(int.class) == null) {
                    return;
                }
                int value = dataSnapshot.getValue(int.class);
                setScore1(value);
                if (started_game) {
                    setScore(1, getScore1());
                } else {
                    setScore2(getScore1());
                }
                if (value == getwinPoints()) {
                    if (started_game)
                        gameEnded.postValue(true);
                    else
                        gameEnded.postValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        player_1_score.addValueEventListener(score_1_changedListener);
    }

    public LiveData<Boolean> isGameEnded()
    {
        return gameEnded;
    }

    void trackScore2Update()
    {
        ValueEventListener score_2_changedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(int.class) == null) {
                    return;
                }
                int value = dataSnapshot.getValue(int.class);
                setScore2(value);
                if (started_game) {
                    setScore(2, getScore2());
                } else {
                    setScore(2, getScore1());
                }
                if (value == getwinPoints()) {
                    if (!started_game)
                        gameEnded.postValue(true);
                    else
                        gameEnded.postValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        player_2_score.addValueEventListener(score_2_changedListener);
    }

    public void setSecondPlayerJoined(boolean _secondPlayerJoined)
    {
        this.secondPlayerJoined = _secondPlayerJoined;
    }

    public boolean getSecondPlayerJoined()
    {
        return this.secondPlayerJoined;
    }

    void initFirstPlayerField()
    {
        ValueEventListener field_1_listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null) {
                    return;
                }
                if (Objects.requireNonNull(dataSnapshot.getValue(String.class)).isEmpty()) {
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<GameField>() {
                }.getType();
                String value = dataSnapshot.getValue(String.class);
                if (started_game)
                    player_1_field.getValue().updateField((GameField) gson.fromJson(value, type));
                else
                    player_2_field.getValue().updateField((GameField) gson.fromJson(value, type));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        player_1_field_db.addValueEventListener(field_1_listener);
    }


    void initSecondPlayerField()
    {
        ValueEventListener field_2_listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null) {
                    return;
                }
                String value = dataSnapshot.getValue(String.class);
                if (!Objects.requireNonNull(value).isEmpty()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<GameField>() {
                    }.getType();
                    setSecondPlayerJoined(true);
                    if (started_game)
                        player_2_field.getValue().updateField((GameField) gson.fromJson(value, type));
                    else
                        player_1_field.getValue().updateField((GameField) gson.fromJson(value, type));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        player_2_field_db.addValueEventListener(field_2_listener);

        player_2_field_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null)
                {
                    return;
                }
                String value = dataSnapshot.getValue(String.class);
                if (!Objects.requireNonNull(value).isEmpty() && started_game)
                {
                    setSecondPlayerJoined(true);
                    player_2_field.getValue().setFieldMode(CurrentGameFieldMode.PLAYER2);
                    player_2_field_db.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setStatusGame(CurrentGameFieldMode mode)
    {
         player_2_field.getValue().setFieldMode(mode);
    }

    public LiveData<GameFieldView> getGameFieldView(int user)
    {
        if(user == 1)
        {
            return player_1_field;
        }
        else
        {
            return player_2_field;
        }
    }
}
