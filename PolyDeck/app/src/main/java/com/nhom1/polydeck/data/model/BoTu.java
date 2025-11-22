package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BoTu {
    @SerializedName("_id")
    private String id;

    @SerializedName("ten_chu_de")
    private String tenChuDe;

    @SerializedName("link_anh_icon")
    private String linkAnhIcon;

    @SerializedName("so_luong_quiz")
    private int soLuongQuiz;

    // This field might be populated by the server via aggregation. Required for UI.
    @SerializedName("so_nguoi_dung")
    private int soNguoiDung;

    @SerializedName("ngay_tao")
    private Date ngayTao;

    public BoTu() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenChuDe() {
        return tenChuDe;
    }

    public void setTenChuDe(String tenChuDe) {
        this.tenChuDe = tenChuDe;
    }

    public String getLinkAnhIcon() {
        return linkAnhIcon;
    }

    public void setLinkAnhIcon(String linkAnhIcon) {
        this.linkAnhIcon = linkAnhIcon;
    }

    public int getSoLuongQuiz() {
        return soLuongQuiz;
    }

    public void setSoLuongQuiz(int soLuongQuiz) {
        this.soLuongQuiz = soLuongQuiz;
    }

    public int getSoNguoiDung() {
        return soNguoiDung;
    }

    public void setSoNguoiDung(int soNguoiDung) {
        this.soNguoiDung = soNguoiDung;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }
}
