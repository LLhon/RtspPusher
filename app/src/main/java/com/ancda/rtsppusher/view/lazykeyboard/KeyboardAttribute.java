package com.ancda.rtsppusher.view.lazykeyboard;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

class KeyboardAttribute {
    public ColorStateList chooserSelectedColor;
    public ColorStateList chooserUnselectedColor;
    public Drawable chooserBackground;
    public Drawable keyboardBackground;
    public boolean isKeyPreview;
    public int keyboardMode;

    public KeyboardAttribute(ColorStateList chooserSelectedColor, ColorStateList chooserUnselectedColor,
        Drawable chooserBackground, Drawable keyboardBackground, boolean isKeyPreview, int keyboardMode) {
        this.chooserSelectedColor = chooserSelectedColor;
        this.chooserUnselectedColor = chooserUnselectedColor;
        this.chooserBackground = chooserBackground;
        this.keyboardBackground = keyboardBackground;
        this.isKeyPreview = isKeyPreview;
        this.keyboardMode = keyboardMode;
    }
}
