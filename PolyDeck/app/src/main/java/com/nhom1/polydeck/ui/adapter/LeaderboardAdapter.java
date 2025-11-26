package com.nhom1.polydeck.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<User> userList;
    private String currentUserEmail;

    public LeaderboardAdapter(List<User> userList, String currentUserEmail) {
        this.userList = userList != null ? userList : new ArrayList<>();
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        int rank = position + 4; // Vì top 3 đã hiển thị riêng, nên bắt đầu từ rank 4
        holder.tvRank.setText("#" + rank);
        holder.tvAvatar.setText(user.getInitials());
        
        // Kiểm tra nếu là user hiện tại
        boolean isCurrentUser = currentUserEmail != null && 
                                user.getEmail() != null && 
                                user.getEmail().equalsIgnoreCase(currentUserEmail);
        
        if (isCurrentUser) {
            // Highlight user hiện tại
            holder.itemContainer.setBackgroundResource(R.drawable.bg_leaderboard_item_current);
            holder.tvRank.setTextColor(0xFF2563EB); // Màu xanh
            holder.tvName.setText(user.getHoTen() + " (Bạn)");
            holder.tvScore.setTextColor(0xFF2563EB); // Màu xanh cho XP
            holder.tvAvatar.setBackgroundResource(R.drawable.bg_circle_purple); // Màu tím cho avatar
        } else {
            // User thường
            holder.itemContainer.setBackgroundResource(R.drawable.bg_leaderboard_item);
            holder.tvRank.setTextColor(0xFF9CA3AF); // Màu xám
            holder.tvName.setText(user.getHoTen());
            holder.tvScore.setTextColor(0xFF111827); // Màu đen
            holder.tvAvatar.setBackgroundResource(R.drawable.bg_circle_gray_light); // Màu xám cho avatar
        }
        
        holder.tvScore.setText(String.valueOf(user.getXp()));
        
        // Hiển thị streak
        int streak = user.getChuoiNgayHoc();
        if (streak > 0) {
            holder.tvStreak.setText(streak + " days");
            holder.tvStreak.setVisibility(View.VISIBLE);
        } else {
            holder.tvStreak.setText("0 days");
            holder.tvStreak.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateData(List<User> newList) {
        if (this.userList == null) {
            this.userList = new ArrayList<>();
        }
        this.userList.clear();
        if (newList != null) {
            this.userList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvAvatar, tvName, tvScore, tvStreak;
        RelativeLayout itemContainer;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            itemContainer = itemView.findViewById(R.id.itemContainer);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvStreak = itemView.findViewById(R.id.tvStreak);
        }
    }
}

