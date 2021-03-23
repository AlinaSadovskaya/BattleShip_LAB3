package com.lab3.lab3_game.DataBase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
public class ModelFirebaseDatabase {

    private final FirebaseDatabase database;

    public ModelFirebaseDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public void updateChild(String path, Map<String, Object> values) {
        database.getReference(path).updateChildren(values);
    }

    public void setValue(String path, Object value) {
        database.getReference(path).setValue(value);
    }

    public DatabaseReference getRef(String path) {
        return database.getReference(path);
    }



}
