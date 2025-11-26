package com.nhom1.polydeck.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.LoginResponse;
import com.nhom1.polydeck.data.model.User;
import com.nhom1.polydeck.ui.adapter.LeaderboardAdapter;
import com.nhom1.polydeck.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvRank1Avatar, tvRank1Name, tvRank1XP;
    private TextView tvRank2Avatar, tvRank2Name, tvRank2XP;
    private TextView tvRank3Avatar, tvRank3Name, tvRank3XP;
    private LinearLayout rank1Container, rank2Container, rank3Container;
    private RecyclerView recyclerViewLeaderboard;
    private ProgressBar progressBar;
    private LeaderboardAdapter adapter;
    private APIService apiService;
    private List<User> allUsers = new ArrayList<>();
    private SessionManager sessionManager;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);
        LoginResponse currentUser = sessionManager.getUserData();
        currentUserEmail = currentUser != null ? currentUser.getEmail() : null;
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadLeaderboard();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        
        // Top 3
        tvRank1Avatar = findViewById(R.id.tvRank1Avatar);
        tvRank1Name = findViewById(R.id.tvRank1Name);
        tvRank1XP = findViewById(R.id.tvRank1XP);
        tvRank2Avatar = findViewById(R.id.tvRank2Avatar);
        tvRank2Name = findViewById(R.id.tvRank2Name);
        tvRank2XP = findViewById(R.id.tvRank2XP);
        tvRank3Avatar = findViewById(R.id.tvRank3Avatar);
        tvRank3Name = findViewById(R.id.tvRank3Name);
        tvRank3XP = findViewById(R.id.tvRank3XP);
        
        rank1Container = findViewById(R.id.rank1Container);
        rank2Container = findViewById(R.id.rank2Container);
        rank3Container = findViewById(R.id.rank3Container);
        
        recyclerViewLeaderboard = findViewById(R.id.recyclerViewLeaderboard);
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter(new ArrayList<>(), currentUserEmail);
        recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLeaderboard.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadLeaderboard() {
        showLoading(true);

        Call<List<User>> call = apiService.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    allUsers = response.body();
                    
                    // Lọc chỉ lấy user active và có XP
                    List<User> activeUsers = new ArrayList<>();
                    for (User user : allUsers) {
                        if (user.getTrangThai() != null && 
                            user.getTrangThai().equals("active") && 
                            user.getXp() >= 0) {
                            activeUsers.add(user);
                        }
                    }
                    
                    // Sắp xếp theo XP giảm dần
                    Collections.sort(activeUsers, (u1, u2) -> Integer.compare(u2.getXp(), u1.getXp()));
                    
                    displayLeaderboard(activeUsers);
                } else {
                    Toast.makeText(LeaderboardActivity.this, "Không thể tải bảng xếp hạng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(LeaderboardActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayLeaderboard(List<User> sortedUsers) {
        if (sortedUsers.isEmpty()) {
            // Ẩn top 3 nếu không có user
            rank1Container.setVisibility(View.GONE);
            rank2Container.setVisibility(View.GONE);
            rank3Container.setVisibility(View.GONE);
            return;
        }

        // Hiển thị top 3
        if (sortedUsers.size() >= 1) {
            User rank1 = sortedUsers.get(0);
            tvRank1Avatar.setText(rank1.getInitials());
            tvRank1Name.setText(rank1.getHoTen());
            tvRank1XP.setText(String.valueOf(rank1.getXp()));
            rank1Container.setVisibility(View.VISIBLE);
        } else {
            rank1Container.setVisibility(View.GONE);
        }

        if (sortedUsers.size() >= 2) {
            User rank2 = sortedUsers.get(1);
            tvRank2Avatar.setText(rank2.getInitials());
            tvRank2Name.setText(rank2.getHoTen());
            tvRank2XP.setText(String.valueOf(rank2.getXp()));
            rank2Container.setVisibility(View.VISIBLE);
        } else {
            rank2Container.setVisibility(View.GONE);
        }

        if (sortedUsers.size() >= 3) {
            User rank3 = sortedUsers.get(2);
            tvRank3Avatar.setText(rank3.getInitials());
            tvRank3Name.setText(rank3.getHoTen());
            tvRank3XP.setText(String.valueOf(rank3.getXp()));
            rank3Container.setVisibility(View.VISIBLE);
        } else {
            rank3Container.setVisibility(View.GONE);
        }

        // Hiển thị rank 4+ trong RecyclerView
        if (sortedUsers.size() > 3) {
            List<User> remainingUsers = sortedUsers.subList(3, sortedUsers.size());
            adapter = new LeaderboardAdapter(remainingUsers, currentUserEmail);
            recyclerViewLeaderboard.setAdapter(adapter);
        } else {
            adapter = new LeaderboardAdapter(new ArrayList<>(), currentUserEmail);
            recyclerViewLeaderboard.setAdapter(adapter);
        }
    }

    private void showLoading(boolean show) {
        if (progressBar == null) {
            progressBar = findViewById(R.id.progressBar);
        }
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}