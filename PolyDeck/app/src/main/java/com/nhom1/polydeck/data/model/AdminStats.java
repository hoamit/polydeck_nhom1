package com.nhom1.polydeck.data.model;
import com.google.gson.annotations.SerializedName;

public class AdminStats {
    @SerializedName("tongNguoiDung")
    private int tongNguoiDung;

    @SerializedName("tongBoTu")
    private int tongBoTu;

    @SerializedName("nguoiHoatDong")
    private int nguoiHoatDong;

    @SerializedName("tongTuVung")
    private int tongTuVung;

    // FIX: Added back the missing fields and methods
    @SerializedName("tyLeNguoiDung")
    private String tyLeNguoiDung;

    @SerializedName("tyLeBoTu")
    private String tyLeBoTu;

    @SerializedName("tyLeHoatDong")
    private String tyLeHoatDong;
    
    @SerializedName("tyLeTuVung")
    private String tyLeTuVung;

    // Getters and Setters
    public int getTongNguoiDung() { return tongNguoiDung; }
    public void setTongNguoiDung(int tongNguoiDung) { this.tongNguoiDung = tongNguoiDung; }

    public int getTongBoTu() { return tongBoTu; }
    public void setTongBoTu(int tongBoTu) { this.tongBoTu = tongBoTu; }

    public int getNguoiHoatDong() { return nguoiHoatDong; }
    public void setNguoiHoatDong(int nguoiHoatDong) { this.nguoiHoatDong = nguoiHoatDong; }

    public int getTongTuVung() { return tongTuVung; }
    public void setTongTuVung(int tongTuVung) { this.tongTuVung = tongTuVung; }

    public String getTyLeNguoiDung() { return tyLeNguoiDung; }
    public void setTyLeNguoiDung(String tyLeNguoiDung) { this.tyLeNguoiDung = tyLeNguoiDung; }

    public String getTyLeBoTu() { return tyLeBoTu; }
    public void setTyLeBoTu(String tyLeBoTu) { this.tyLeBoTu = tyLeBoTu; }

    public String getTyLeHoatDong() { return tyLeHoatDong; }
    public void setTyLeHoatDong(String tyLeHoatDong) { this.tyLeHoatDong = tyLeHoatDong; }

    public String getTyLeTuVung() { return tyLeTuVung; }
    public void setTyLeTuVung(String tyLeTuVung) { this.tyLeTuVung = tyLeTuVung; }
}