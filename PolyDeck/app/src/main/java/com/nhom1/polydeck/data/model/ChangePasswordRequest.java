package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("mat_khau_cu")
    private String matKhauCu;

    @SerializedName("mat_khau_moi")
    private String matKhauMoi;

    public ChangePasswordRequest(String email, String matKhauCu, String matKhauMoi) {
        this.email = email;
        this.matKhauCu = matKhauCu;
        this.matKhauMoi = matKhauMoi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhauCu() {
        return matKhauCu;
    }

    public void setMatKhauCu(String matKhauCu) {
        this.matKhauCu = matKhauCu;
    }

    public String getMatKhauMoi() {
        return matKhauMoi;
    }

    public void setMatKhauMoi(String matKhauMoi) {
        this.matKhauMoi = matKhauMoi;
    }
}



