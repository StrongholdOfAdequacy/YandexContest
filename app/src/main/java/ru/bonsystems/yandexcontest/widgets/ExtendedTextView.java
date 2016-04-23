package ru.bonsystems.yandexcontest.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ru.bonsystems.yandexcontest.R;

/**
 * Created by Kolomeytsev Anton on 15.03.2016.
 * This class is a part of Yandex Contest project.
 * Виджет, которому можно устанавливать кастомные шрифты из асссетов
 */
public class ExtendedTextView extends TextView {
    public ExtendedTextView(Context context) {
        super(context);
    }
    // кеш шрифтов (повышает производительность более, чем в 10 раз)
    private static Map<String, Typeface> stringTypefaceMap = new HashMap<>();

    public ExtendedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ExtendedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ExtendedTextView);
        String customFont = a.getString(R.styleable.ExtendedTextView_typeface);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        // если шрифт уже кэширован, просто берём его из кэша
        if (stringTypefaceMap.containsKey(asset)) {
            setTypeface(stringTypefaceMap.get(asset));
            return true;
        }
        // иначе, загружаем его из ассетов
        Typeface tf;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
            stringTypefaceMap.put(asset, tf); // кэшируем шрифт
        } catch (Exception e) {
            Log.e("ExtendedTextView", "Could not get typeface: " + e.getMessage());
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
