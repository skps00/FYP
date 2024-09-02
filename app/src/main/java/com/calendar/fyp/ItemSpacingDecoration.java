package com.calendar.fyp;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private int spacing;

    public ItemSpacingDecoration(Context context, int spacingInPixels) {
        this.spacing = spacingInPixels;
        // 将像素值转换为dp值（根据需要调整）
        float scale = context.getResources().getDisplayMetrics().density;
        this.spacing = (int) (spacingInPixels * scale + 0.5f);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 设置项间的间距（这里设置为相同的间距，您可以根据需要进行调整）
        outRect.top = spacing;
        outRect.bottom = spacing;
    }
}
