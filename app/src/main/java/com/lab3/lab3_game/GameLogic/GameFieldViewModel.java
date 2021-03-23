package com.lab3.lab3_game.GameLogic;

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

import java.lang.reflect.Type;
import java.util.Objects;

public class GameFieldViewModel extends AndroidViewModel {

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
    private final MutableLiveData<Boolean> gameEnded = new MutableLiveData<Boolean>();
    private boolean started_game;
    private boolean secondPlayerJoined = false;
    private GameDB gameDB;
    private GameController gameController;


    public GameFieldViewModel(@NonNull Application application) {
        super(application);
    }

    public void Inicialize(String _gameId, boolean _started_game)
    {
        this.gameId = _gameId;
        this.started_game  = _started_game;
        gameController = new GameController(_gameId);
        gameDB = gameController.getGameDB();
    }

    public void saveStats()
    {
        DatabaseReference stats = gameDB.getStat(this.gameId);
        stats.child("player_1").setValue(player_1_name.getValue());
        stats.child("score_1").setValue(gameController.getScore1());
        stats.child("player_2").setValue(player_2_name.getValue());
        stats.child("score_2").setValue(gameController.getScore2());
    }

    public void RemoveGame()
    {
        gameDB.RemoveGame(this.gameId);
    }

    public int getScore1()
    {
        return gameController.getScore1();
    }

    public int getScore2()
    {
        return gameController.getScore2();
    }
    public void setScore1(int score)
    {
        gameController.setScore1(score);
    }

    public void setScore2(int score)
    {
        gameController.setScore2(score);
    }


    public void addPlayer_2Listener(ValueEventListener player_2_listener){
        this.gameDB.addPlayer_2Listener(player_2_listener);
    }

    public void removePlayer2Listener(ValueEventListener player_2_listener){
        this.gameDB.removePlayer_2field(player_2_listener);
    }

    public void addMoveChangesListener(ValueEventListener moveChangesListener){
        this.gameDB.addMove(moveChangesListener);
    }

    public void updatingMove(MoveResult moveResult, boolean started_game)
    {
        if (moveResult == MoveResult.HIT) {
            game_holder.postValue("You can hit one more time. :p");
        }
        Gson gson = new Gson();
        String jsonField1 = gson.toJson(player_1_field.getValue().getGameField());
        String jsonField2 = gson.toJson(player_2_field.getValue().getGameField());
        gameController.updatingMove(moveResult, started_game, jsonField1, jsonField2);
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
        gameDB.addPlayer_1Listener(scoreView_1_listener);

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
        gameDB.addPlayer_2Listener(scoreView_2_listener);
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

    void trackScore1Update() {
        gameController.trackScore1Update(started_game);
        if (!started_game && gameController.getGameEnded() != null)
            if (gameController.getGameEnded() == true) {
                gameEnded.postValue(true);
            } else {
                gameEnded.postValue(false);
            }
    }

    public LiveData<Boolean> isGameEnded()
    {
        return gameEnded;
    }

    void trackScore2Update()
    {
        gameController.trackScore2Update(started_game);
        if (!started_game && gameController.getGameEnded() != null)
            if (gameController.getGameEnded() == true) {
                gameEnded.postValue(true);
            } else {
                gameEnded.postValue(false);
            }
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
        gameDB.addPlayer_1field(field_1_listener);
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
        gameDB.addPlayer_2field(field_2_listener);

        gameDB.addPlayer_2field(new ValueEventListener() {
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
                    gameDB.removePlayer_2field(this);
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
