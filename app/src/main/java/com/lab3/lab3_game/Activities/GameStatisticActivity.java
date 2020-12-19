package com.lab3.lab3_game.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import com.lab3.lab3_game.Adapters.StatisticAdapter;
import com.lab3.lab3_game.Structures.Statistic;
import com.lab3.lab3_game.R;
import java.util.ArrayList;
import java.util.Objects;

public class GameStatisticActivity extends AppCompatActivity {


    private ArrayList<Statistic> statistics;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private PopupWindow mPopupWindow;
    private StatisticsViewModel statisticsViewModel;
    private Integer isNull = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_statistic);
        mProgressBar = findViewById(R.id.progressBarStats);
        mRecyclerView = findViewById(R.id.stats_list);
        statistics = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statisticsViewModel = ViewModelProviders.of(this).get(StatisticsViewModel.class);

        statisticsViewModel.returnStatistics().observe(this, new Observer<ArrayList<Statistic>>() {
            @Override
            public void onChanged(ArrayList<Statistic> value) {
                statistics = value;
            }
        });

        statisticsViewModel.returnFlag().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                isNull = value;
            }
        });

        if (isNull != 1) {
            mRecyclerView.setAdapter(new StatisticAdapter(statistics));
            mProgressBar.setVisibility(View.GONE);
        }
        else
            showNoStats();

    }

    private void showNoStats()
    {
        Context mContext = getApplicationContext();
        // popup window for entering rss
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.without_statistic, null);
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        mPopupWindow.setElevation(5.0f);
        findViewById(R.id.stats_holder).post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.showAtLocation(findViewById(R.id.stats_holder), Gravity.CENTER,0,0);
            }
        });
        Button okButton = customView.findViewById(R.id.ok_no_stats);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                finish();
            }
        });
    }
}

