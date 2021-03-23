package com.lab3.lab3_game.DataBase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ModelFirebaseStorage {
    private final FirebaseStorage firebaseStorage;

    public ModelFirebaseStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public StorageReference getRef(String path) {
        return firebaseStorage.getReference(path);
    }
}
