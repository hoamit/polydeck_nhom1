package com.nhom1.polydeck.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.TuVung;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVocabularyActivity extends AppCompatActivity {

    private static final String TAG = "AddVocabularyActivity";
    private static final int PICK_EXCEL_FILE_REQUEST = 2;

    private Toolbar toolbar;
    private LinearLayout btnImportExcel;
    private TextInputEditText etEnglishWord, etPronunciation, etVietnameseMeaning, etExample;
    private Button btnAddVocabulary;
    private ImageView btnSaveAndExit;

    private APIService apiService;
    private String deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vocabulary);

        apiService = RetrofitClient.getApiService();

        deckId = getIntent().getStringExtra("DECK_ID");
        if (deckId == null || deckId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID bộ từ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_add_vocab);
        btnImportExcel = findViewById(R.id.btnImportExcel);
        etEnglishWord = findViewById(R.id.etEnglishWord);
        etPronunciation = findViewById(R.id.etPronunciation);
        etVietnameseMeaning = findViewById(R.id.etVietnameseMeaning);
        etExample = findViewById(R.id.etExample);
        btnAddVocabulary = findViewById(R.id.btnAddVocabulary);
        btnSaveAndExit = findViewById(R.id.btnSaveAndExit);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnAddVocabulary.setOnClickListener(v -> addVocabularyManually(false));
        btnImportExcel.setOnClickListener(v -> openFileChooser());
        btnSaveAndExit.setOnClickListener(v -> addVocabularyManually(true));
    }

    private void addVocabularyManually(boolean shouldExit) {
        String englishWord = etEnglishWord.getText().toString().trim();
        String vietnameseMeaning = etVietnameseMeaning.getText().toString().trim();

        // If user clicks "Save and Exit" but fields are empty, just exit.
        if (shouldExit && englishWord.isEmpty() && vietnameseMeaning.isEmpty()) {
            finish();
            return;
        }

        if (englishWord.isEmpty() || vietnameseMeaning.isEmpty()) {
            Toast.makeText(this, "Từ vựng và nghĩa không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        TuVung newWord = new TuVung();
        newWord.setTuTiengAnh(englishWord);
        newWord.setNghiaTiengViet(vietnameseMeaning);
        newWord.setPhienAm(etPronunciation.getText().toString().trim());
        newWord.setCauViDu(etExample.getText().toString().trim());

        apiService.addTuVungToChuDe(deckId, newWord).enqueue(new Callback<TuVung>() {
            @Override
            public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                if (response.isSuccessful()) {
                    if (shouldExit) {
                        Toast.makeText(AddVocabularyActivity.this, "Đã lưu và thoát!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddVocabularyActivity.this, "Thêm từ vựng thành công!", Toast.LENGTH_SHORT).show();
                        etEnglishWord.setText("");
                        etPronunciation.setText("");
                        etVietnameseMeaning.setText("");
                        etExample.setText("");
                        etEnglishWord.requestFocus();
                    }
                } else {
                    Toast.makeText(AddVocabularyActivity.this, "Thêm từ vựng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TuVung> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(AddVocabularyActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        // ... (existing code for file chooser)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ... (existing code for onActivityResult)
    }
}
