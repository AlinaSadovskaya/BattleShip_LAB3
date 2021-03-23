package com.lab3.lab3_game.GameLogic;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lab3.lab3_game.DataBase.ModelFirebaseDatabase;

public class GameDB {
    private DatabaseReference player_1_field_db;
    private DatabaseReference player_2_field_db;
    private DatabaseReference player_1;
    private DatabaseReference player_2;
    private DatabaseReference player_1_score;
    private DatabaseReference player_2_score;
    private DatabaseReference currentMove;
    private DatabaseReference stat;
    private ModelFirebaseDatabase firebaseDatabase;

    public GameDB(){
        firebaseDatabase = new ModelFirebaseDatabase();
    }

    public void Inicialize(String _gameId){
        DatabaseReference game = firebaseDatabase.getRef("games").child(_gameId);
        currentMove = game.child("currentMoveByPlayer");
        player_1_field_db = game.child("player_1_field");
        player_2_field_db = game.child("player_2_field");
        player_1_score = game.child("score_1");
        player_2_score = game.child("score_2");
        player_1 = game.child("player_1");
        player_2 = game.child("player_2");
    }



    public DatabaseReference getStat(String gameId){
        return firebaseDatabase.getRef("stats").child(gameId);
    }

    public void RemoveGame(String gameId)
    {
        firebaseDatabase.getRef("games").child(gameId).removeValue();
    }

    public void setMove(String currentMove){
        this.currentMove.setValue(currentMove);
    }

    public void setPlayer_1_field(String currentMove){
        this.player_1_field_db.setValue(currentMove);
    }
    public void setPlayer_2_field(String currentMove){
        this.player_2_field_db.setValue(currentMove);
    }

    public void setPlayer_1_score(Integer currentMove){
        this.player_1_score.setValue(currentMove);
    }
    public void setPlayer_2_score(Integer currentMove){
        this.player_2_score.setValue(currentMove);
    }

    public void addPlayer_2Listener(ValueEventListener player_2_listener){
        this.player_2.addValueEventListener(player_2_listener);
    }

    public void addPlayer_1Listener(ValueEventListener player_1_listener){
        this.player_1.addValueEventListener(player_1_listener);
    }

    public void addPlayer_1Score(ValueEventListener player_1_listener){
        this.player_1_score.addValueEventListener(player_1_listener);
    }

    public void addPlayer_2Score(ValueEventListener player_2_listener){
        this.player_2_score.addValueEventListener(player_2_listener);
    }

    public void addPlayer_1field(ValueEventListener player_1_listener){
        this.player_1_field_db.addValueEventListener(player_1_listener);
    }

    public void addPlayer_2field(ValueEventListener player_2_listener){
        this.player_2_field_db.addValueEventListener(player_2_listener);
    }

    public void removePlayer_2field(ValueEventListener player_2_listener){
        this.player_2_field_db.removeEventListener(player_2_listener);
    }

    public void addMove(ValueEventListener player_listener){
        this.currentMove.removeEventListener(player_listener);
    }
}
