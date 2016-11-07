package com.faendir.lightning_launcher.multitool.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.faendir.omniadapter.model.Leaf;

import java.lang.ref.SoftReference;

/**
 * Created on 26.01.2016.
 *
 * @author F43nd1r
 */
public class IntentInfo extends Leaf implements Comparable<IntentInfo>{
    private SoftReference<Drawable> icon;
    private final DrawableProvider provider;
    private final Intent intent;
    private final String title;
    private final boolean isIndirect;

    public IntentInfo(Intent intent, DrawableProvider provider, String title, boolean isIndirect) {
        this.provider = provider;
        this.intent = intent;
        this.title = title;
        this.isIndirect = isIndirect;

    }

    public Drawable getImage() {
        if (icon == null || icon.get() == null) {
            Drawable drawable = provider.getDrawable();
            icon = new SoftReference<>(drawable);
            return drawable;
        } else{
            return icon.get();
        }
    }

    public Intent getIntent() {
        return intent;
    }

    public String getText() {
        return title;
    }

    public boolean isIndirect() {
        return isIndirect;
    }

    @Override
    public int compareTo(IntentInfo o) {
        return getText().compareTo(o.getText());
    }
}
