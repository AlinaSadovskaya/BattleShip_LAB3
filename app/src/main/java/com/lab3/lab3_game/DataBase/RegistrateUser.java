package com.lab3.lab3_game.DataBase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrateUser {
    private final FirebaseAuth firebaseAuth;

    public RegistrateUser() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getFirebaseUser()
    {
        return this.firebaseAuth.getCurrentUser();
    }
    public String getUIDUser() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public String getEmail() {
        return firebaseAuth.getCurrentUser().getEmail();
    }
}
