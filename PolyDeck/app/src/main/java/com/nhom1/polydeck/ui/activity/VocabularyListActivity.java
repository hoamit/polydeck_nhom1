package com.nhom1.polydeck.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.TuVung;
import com.nhom1.polydeck.ui.adapter.VocabularyAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyListActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "EXTRA_DECK_ID";
    public static final String EXTRA_DECK_NAME = "EXTRA_DECK_NAME";
    private static final String TAG = "VocabListActivity";

    private Toolbar toolbar;
    private RecyclerView rvVocabulary;
    private VocabularyAdapter adapter;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);

        String deckId = getIntent().getStringExtra(EXTRA_DECK_ID);
        String deckName = getIntent().getStringExtra(EXTRA_DECK_NAME);

        if (deckId == null || deckId.isEmpty()) {
            Toast.makeText(this, "Lỗi: ID bộ từ không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();

        initViews();
        setupToolbar(deckName);
        setupRecyclerView();
        fetchVocabulary(deckId);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_vocab_list);
        rvVocabulary = findViewById(R.id.rvVocabulary);
    }

    private void setupToolbar(String deckName) {
        toolbar.setTitle("Từ của bộ: " + (deckName != null ? deckName : ""));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        rvVocabulary.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VocabularyAdapter(new ArrayList<>());
        rvVocabulary.setAdapter(adapter);
    }

    private void fetchVocabulary(String deckId) {
        apiService.getTuVungByBoTu(deckId).enqueue(new Callback<List<TuVung>>() {
            @Override
            public void onResponse(@NonNull Call<List<TuVung>> call, @NonNull Response<List<TuVung>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                } else {
                    Toast.makeText(VocabularyListActivity.this, "Không thể tải danh sách từ vựng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TuVung>> call, @NonNull Throwable t) {
                Log.e(TAG, "API Error: " + t.getMessage());
                Toast.makeText(VocabularyListActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
