package foodie.example.com.foodieserver.Interface;

import android.view.View;

public interface ItemClickListener {
    void inClick(View View,int position,boolean isLongClick);

    void onClick(View view, int adapterPosition, boolean b);
}

