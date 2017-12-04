package com.example.stevendeweille_pset5;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class RestoAdapter extends ResourceCursorAdapter {

    public RestoAdapter(Context context, Cursor cursor) {
        super(context, R.layout.row_resto, cursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = view.findViewById(R.id.nameView);
        TextView priceView = view.findViewById(R.id.priceView);
        TextView amountView = view.findViewById(R.id.amountView);

        int amount = cursor.getInt(cursor.getColumnIndex("amount"));
        Double totalPrice = cursor.getDouble(cursor.getColumnIndex("price")) * amount;

        nameView.setText(cursor.getString(cursor.getColumnIndex("name")));
        amountView.setText(amount+"x");
        priceView.setText("€"+Double.toString(cursor.getDouble(cursor.getColumnIndex("price"))));
    }

}
