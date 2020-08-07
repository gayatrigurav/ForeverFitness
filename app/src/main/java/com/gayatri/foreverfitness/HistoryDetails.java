package com.gayatri.foreverfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class HistoryDetails extends LinearLayout {

    public HistoryDetails(Context context) {
        super(context);

    }
    @Override
    protected void removeDetachedView(View child, boolean animate) {
        super.removeDetachedView(child, false);
    }
}