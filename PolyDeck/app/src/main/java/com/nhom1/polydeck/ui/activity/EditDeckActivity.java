package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.BoTu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDeckActivity extends AppCompatActivity {

    private static final String TAG = "EditDeckActivity";
    private static final int PICK_IMAGE_REQUEST = 3;

    private Toolbar toolbar;
    private EditText etEditDeckName;
    private ImageView ivEditIconPreview;
    private TextView btnChangeImage;
    private Button btnSaveChangesDeck;

    private APIService apiService;
    private String deckId;
    private BoTu currentDeck;
    private Uri newImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        deckId = getIntent().getStringExtra("DECK_ID");
        if (deckId == null || deckId.isEmpty()) {
            Toast.makeText(this, "ID bộ từ không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        initViews();
        setupToolbar();
        fetchDeckDetails();

        btnSaveChangesDeck.setOnClickListener(v -> saveChanges());
        btnChangeImage.setOnClickListener(v -> openFileChooser());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_edit_deck);
        etEditDeckName = findViewById(R.id.etEditDeckName);
        ivEditIconPreview = findViewById(R.id.ivEditIconPreview);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSaveChangesDeck = findViewById(R.id.btnSaveChangesDeck);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void fetchDeckDetails() {
        apiService.getChuDeDetail(deckId).enqueue(new Callback<BoTu>() {
            @Override
            public void onResponse(Call<BoTu> call, Response<BoTu> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDeck = response.body();
                    populateDeckData(currentDeck);
                } else {
                    Toast.makeText(EditDeckActivity.this, "Không thể tải thông tin bộ từ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoTu> call, Throwable t) {
                Log.e(TAG, "API Error: " + t.getMessage());
            }
        });
    }

    private void populateDeckData(BoTu deck) {
        etEditDeckName.setText(deck.getTenChuDe());
        Glide.with(this)
                .load(deck.getLinkAnhIcon())
                .error(R.drawable.ic_default_deck_icon)
                .into(ivEditIconPreview);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

     @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            ivEditIconPreview.setImageURI(newImageUri);
        }
    }

    private void saveChanges() {
        String newDeckName = etEditDeckName.getText().toString().trim();
        if (newDeckName.isEmpty()) {
            etEditDeckName.setError("Tên không được để trống");
            etEditDeckName.requestFocus();
            return;
        }
        
        // **IMPORTANT**: If newImageUri is not null, you need to implement
        // a multipart API call to upload the new image and update the name.
        // For now, we only update the name using the existing endpoint.
        if (newImageUri != null) {
            Toast.makeText(this, "Chức năng thay đổi ảnh đang được phát triển", Toast.LENGTH_SHORT).show();
            // Call a new multipart update method here
            return; // Exit for now
        }

        currentDeck.setTenChuDe(newDeckName);

        apiService.updateChuDe(deckId, currentDeck).enqueue(new Callback<BoTu>() {
            @Override
            public void onResponse(Call<BoTu> call, Response<BoTu> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditDeckActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditDeckActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoTu> call, Throwable t) {
                 Log.e(TAG, "API Error: " + t.getMessage());
            }
        });
    }
}
