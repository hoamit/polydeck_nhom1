package com.nhom1.polydeck.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.AdminStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelActivity extends AppCompatActivity {

    private static final String TAG = "AdminPanelActivity";

    private TextView tvTotalUsersStat, tvTotalDecksStat, tvNewUsersStat, tvTotalVocabStat;
    private CardView menuUserManagement, menuDeckManagement, menuSendNotification, menuSupportRequests;
    private ImageView btnLogout;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        apiService = RetrofitClient.getApiService();

        initViews();
        setupNavigation();
        fetchAdminStats();
    }

    private void initViews() {
        tvTotalUsersStat = findViewById(R.id.tvTotalUsersStat);
        tvTotalDecksStat = findViewById(R.id.tvTotalDecksStat);
        tvNewUsersStat = findViewById(R.id.tvNewUsersStat);
        tvTotalVocabStat = findViewById(R.id.tvTotalVocabStat);

        menuUserManagement = findViewById(R.id.menuUserManagement);
        menuDeckManagement = findViewById(R.id.menuDeckManagement);
        menuSendNotification = findViewById(R.id.menuSendNotification);
        menuSupportRequests = findViewById(R.id.menuSupportRequests);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupNavigation() {
        menuUserManagement.setOnClickListener(v -> 
            startActivity(new Intent(this, UserManagementActivity.class)));

        menuDeckManagement.setOnClickListener(v -> 
            startActivity(new Intent(this, DeckManagementActivity.class)));

        menuSendNotification.setOnClickListener(v -> 
            startActivity(new Intent(this, SendNotificationActivity.class)));

        menuSupportRequests.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // TODO: Clear user session/token here
                    Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void fetchAdminStats() {
        apiService.getAdminStats().enqueue(new Callback<AdminStats>() {
            @Override
            public void onResponse(@NonNull Call<AdminStats> call, @NonNull Response<AdminStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminStats stats = response.body();
                    tvTotalUsersStat.setText(String.valueOf(stats.getTongNguoiDung()));
                    tvTotalDecksStat.setText(String.valueOf(stats.getTongBoTu()));
                    tvNewUsersStat.setText(String.valueOf(stats.getNguoiHoatDong()));
                    tvTotalVocabStat.setText(String.valueOf(stats.getTongTuVung()));
                } else {
                    Toast.makeText(AdminPanelActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminStats> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch stats: " + t.getMessage());
                Toast.makeText(AdminPanelActivity.this, "Error loading stats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
