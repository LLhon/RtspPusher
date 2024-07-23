package com.ancda.rtsppusher.view.lazykeyboard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.ancda.rtsppusher.R;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import java.lang.reflect.Method;
import razerdp.basepopup.BasePopupWindow.GravityMode;

/**
 * SecurityEditText
 *
 * @author yidong (onlyloveyd@gmaill.com)
 * @date 2018/6/15 08:29
 */
public class SecurityEditText extends AppCompatEditText {

    private String TAG = "SecurityEditText";
//    private KeyboardDialog dialog;
    private KeyboardPopup popup;
    private KeyboardAttribute keyboardAttribute;

    public SecurityEditText(Context context) {
        this(context, null);
    }

    public SecurityEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public SecurityEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SecurityEditText);
        ColorStateList chooserSelectedColor = a.getColorStateList(R.styleable.SecurityEditText_chooserSelectedColor);
        ColorStateList chooserUnselectedColor = a.getColorStateList(R.styleable.SecurityEditText_chooserUnselectedColor);
        Drawable chooserBackground = a.getDrawable(R.styleable.SecurityEditText_chooserBackground);
        Drawable keyboardBackground = a.getDrawable(R.styleable.SecurityEditText_keyboardBackground);
        boolean isKeyPreview = a.getBoolean(R.styleable.SecurityEditText_keyPreview, false);
        int keyboardMode = a.getInteger(R.styleable.SecurityEditText_keyboardMode, 2);
        a.recycle();
        keyboardAttribute = new KeyboardAttribute(chooserSelectedColor, chooserUnselectedColor, chooserBackground, keyboardBackground, isKeyPreview, keyboardMode);
        initialize();
    }

    private void initialize() {
        setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(false);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        Log.d(TAG, "onFocusChanged: focused=" + focused);
//        if (focused) {
//            hideSystemKeyboard();
//            showSoftInput();
//        } else {
//            hideSoftKeyboard();
//        }
    }

    @Override
    public boolean performClick() {
        if (this.isFocused()) {
            hideSystemKeyboard();
            showSoftInput();
        }
        return false;
    }

    private void hideSystemKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    private void showSoftInput() {
        Log.d(TAG, "showSoftInput");
        if (popup == null) {
            popup = new KeyboardPopup(getContext(), this);
            popup.setOffsetX(-18).setOffsetY(-8).setWidth(CommonUtil.dip2px(getContext(), 530))
                .setBackgroundColor(getResources().getColor(android.R.color.transparent))
                .setPopupGravity(Gravity.TOP)
                .setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR);
        }
        popup.showPopupWindow(this);
//        if (dialog == null) {
//            dialog = KeyboardDialog.show(getContext(), this);
//        } else {
//            dialog.show();
//        }
    }

    private void hideSoftKeyboard() {
        if (popup != null) {
            popup.dismiss();
        }
//        if (dialog != null) {
//            dialog.dismiss();
//        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: isFocused=" + isFocused());
//        if (this.isFocused()) {
//            hideSystemKeyboard();
//            showSoftInput();
//        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.isFocused()) {
            hideSoftKeyboard();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d(TAG, "onWindowFocusChanged: hasWindowFocus=" + hasWindowFocus + ", hasFocus=" + hasFocus());
//        if (hasWindowFocus && hasFocus()) {
//            this.post(() -> {
//                hideSystemKeyboard();
//                showSoftInput();
//            });
//        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popup != null && popup.isShowing()) {
                popup.dismiss();
                return true;
            } else {
                return false;
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    public KeyboardAttribute getKeyboardAttribute() {
        return keyboardAttribute;
    }
}
