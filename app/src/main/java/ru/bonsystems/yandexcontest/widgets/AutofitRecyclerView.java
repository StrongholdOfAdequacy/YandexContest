package ru.bonsystems.yandexcontest.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import ru.bonsystems.yandexcontest.Controller;
import ru.bonsystems.yandexcontest.R;

/**
 * Created by Kolomeytsev Anton on 24.03.2016.
 * Это наследник RecyclerView, умеющий автоматически определять нужное количество колонок
 * для расположения плиточек.
 * Естественно, данная функция работает только если Layout Manager'ом является GridLayoutManager
 */
public class AutofitRecyclerView extends RecyclerView {
    public AutofitRecyclerView(Context context) {
        super(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (getLayoutManager() instanceof GridLayoutManager) {
            int width = MeasureSpec.getSize(widthSpec);
            if (width != 0) {
                int spans = width / Controller.getInstance().getResources().getDimensionPixelSize(R.dimen.artist_cardview_width);
                if (spans > 0) {
                    ((GridLayoutManager) getLayoutManager()).setSpanCount(spans);
                }
            }
        }
    }
}
