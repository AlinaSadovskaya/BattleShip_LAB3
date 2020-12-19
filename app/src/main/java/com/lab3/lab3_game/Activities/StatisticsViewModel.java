package com.lab3.lab3_game.Activities;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.lab3.lab3_game.DataBase.ModelFirebaseDatabase;
import com.lab3.lab3_game.DataBase.RegistrateUser;
import com.lab3.lab3_game.Structures.Statistic;

import java.util.ArrayList;
import java.util.Objects;

public class StatisticsViewModel extends AndroidViewModel {
    private RegistrateUser firebaseAuth;
    private final MutableLiveData<ArrayList<Statistic>> statistics = new MutableLiveData<>();
    private final MutableLiveData<Integer> isNull = new MutableLiveData<>();

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new RegistrateUser();
        ModelFirebaseDatabase database = new ModelFirebaseDatabase();
        DatabaseReference statsRef = database.getRef("stats");
        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Statistic value = ds.getValue(Statistic.class);
                    if (Objects.requireNonNull(value).getPlayer_1().equals(firebaseAuth.getFirebaseUser().getDisplayName()) ||
                            value.getPlayer_2().equals(firebaseAuth.getFirebaseUser().getDisplayName()))
                        statistics.getValue().add(value);

                }
                if (statistics.getValue() != null && statistics.getValue().size() != 0) {
                    isNull.setValue(0);
                }
                else
                    isNull.setValue(1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<ArrayList<Statistic>> returnStatistics()
    {
        return statistics;
    }

    public LiveData<Integer> returnFlag()
    {
        return isNull;
    }

}
