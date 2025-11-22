package com.nhom1.polydeck.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.BoTu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDeckActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddDeckActivity";

    private Toolbar toolbar;
    private EditText etDeckName;
    private TextView btnSelectImage;
    private ImageView ivIconPreview;
    private Button btnCreateDeck;

    private Uri imageUri;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deck);

        apiService = RetrofitClient.getApiService();

        toolbar = findViewById(R.id.toolbar_add_deck);
        etDeckName = findViewById(R.id.etDeckName);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivIconPreview = findViewById(R.id.ivIconPreview);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnCreateDeck.setOnClickListener(v -> createDeckWithImage());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivIconPreview.setImageURI(imageUri);
            ivIconPreview.setVisibility(View.VISIBLE);
            btnSelectImage.setVisibility(View.GONE);
        }
    }

    private void createDeckWithImage() {
        String deckName = etDeckName.getText().toString().trim();
        if (deckName.isEmpty()) {
            etDeckName.setError("Tên bộ từ không được để trống");
            etDeckName.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh icon", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnCreateDeck.setText("Đang tạo...");
        btnCreateDeck.setEnabled(false);

        File file = createTempFileFromUri(imageUri);
        if (file == null) {
            Toast.makeText(this, "Không thể tạo tệp từ ảnh đã chọn", Toast.LENGTH_SHORT).show();
            resetButton();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody tenChuDe = RequestBody.create(MediaType.parse("multipart/form-data"), deckName);

        apiService.createChuDeWithImage(body, tenChuDe).enqueue(new Callback<BoTu>() {
            @Override
            public void onResponse(Call<BoTu> call, Response<BoTu> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddDeckActivity.this, "Tạo bộ từ thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddDeckActivity.this, AddVocabularyActivity.class);
                    intent.putExtra("DECK_ID", response.body().getId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddDeckActivity.this, "Tạo bộ từ thất bại", Toast.LENGTH_SHORT).show();
                }
                resetButton();
            }

            @Override
            public void onFailure(Call<BoTu> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(AddDeckActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetButton();
            }
        });
    }

    private File createTempFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            String fileName = getFileName(uri);
            File tempFile = File.createTempFile("upload_temp", getFileExtension(fileName), getCacheDir());
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot);
        }
        return null;
    }

    private void resetButton() {
        btnCreateDeck.setText("Tạo và thêm từ vựng");
        btnCreateDeck.setEnabled(true);
    }
}
