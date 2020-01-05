package com.example.fajar.popular_movie_stage_2;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by fajar on 21.06.2018.
 */

class CustomLinearLayoutManager extends LinearLayoutManager {
    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
