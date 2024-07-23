package com.ancda.rtsppusher.view.lazykeyboard

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.util.SparseArray
import android.view.View
import com.ancda.base.ext.util.windowManager
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogCustomKeyboardBinding
import razerdp.basepopup.BasePopupWindow
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.Random

/**
 * author  : LLhon
 * date    : 2024/5/6 11:07.
 * des     : 自定义软键盘
 */
class KeyboardPopup(context: Context, editText: SecurityEditText) : BasePopupWindow(context),
    KeyboardView.OnKeyboardActionListener {

    private lateinit var mBinding: DialogCustomKeyboardBinding
    private val mOrderToKeyboard: SparseArray<Keyboard> = SparseArray()
    private val mNumberPool: ArrayList<String> = ArrayList()
    private val mTargetEditText: WeakReference<SecurityEditText> = WeakReference(editText)
    private var attribute: KeyboardAttribute? = null
    private var mSelectedTextColor = ColorStateList.valueOf(Color.BLUE)
    private var mUnSelectedTextColor = ColorStateList.valueOf(Color.BLACK)
    private var mLetterKeyboard: Keyboard? = null
    private var mSymbolKeyboard: Keyboard? = null
    private var mNumberKeyboard: Keyboard? = null
    private var isNumberRandom = false
    private var isUpper = false
    private var mCurrentOrder = 0

    companion object {
        private const val TAG = "KeyboardPopup"
        private const val ORDER_NUMBER = 0
        private const val ORDER_SYMBOL = 1
        private const val ORDER_LETTER = 2
    }

    init {
        setContentView(R.layout.dialog_custom_keyboard)
        setOutSideDismiss(true)
        isOutSideTouchable = true

        mNumberPool.add("48#0")
        mNumberPool.add("49#1")
        mNumberPool.add("50#2")
        mNumberPool.add("51#3")
        mNumberPool.add("52#4")
        mNumberPool.add("53#5")
        mNumberPool.add("54#6")
        mNumberPool.add("55#7")
        mNumberPool.add("56#8")
        mNumberPool.add("57#9")
    }

    override fun onViewCreated(contentView: View) {
        mBinding = DialogCustomKeyboardBinding.bind(contentView)
        initAttribute()
        initKeyboards()
        initKeyboardChooser()
    }

    private fun initAttribute() {
        attribute = mTargetEditText.get()!!.keyboardAttribute
    }

    private fun initKeyboards() {
        if (attribute?.keyboardBackground != null) {
            mBinding.keyboardView.background = attribute!!.keyboardBackground
        }
        if (attribute?.chooserBackground != null) {
            mBinding.keyboardChooser.background = attribute!!.chooserBackground
        }
        if (attribute?.chooserSelectedColor != null) {
            mSelectedTextColor = attribute!!.chooserSelectedColor
        }
        if (attribute?.chooserUnselectedColor != null) {
            mUnSelectedTextColor = attribute!!.chooserUnselectedColor
        }
        mBinding.keyboardView.isPreviewEnabled = attribute!!.isKeyPreview
        mBinding.keyboardView.setOnKeyboardActionListener(this)
        if (isPortrait()) {
            mLetterKeyboard = Keyboard(context, R.xml.gs_keyboard_english)
            mSymbolKeyboard = Keyboard(context, R.xml.gs_keyboard_symbols_shift)
            mNumberKeyboard = Keyboard(context, R.xml.gs_keyboard_number)
        } else {
            mLetterKeyboard = Keyboard(context, R.xml.gs_keyboard_english_land)
            mSymbolKeyboard = Keyboard(context, R.xml.gs_keyboard_symbols_shift_land)
            mNumberKeyboard = Keyboard(context, R.xml.gs_keyboard_number_land)
        }
        if (isNumberRandom) {
            randomNumbers()
        }
        mOrderToKeyboard.put(ORDER_NUMBER, mNumberKeyboard)
        mOrderToKeyboard.put(ORDER_SYMBOL, mSymbolKeyboard)
        mOrderToKeyboard.put(ORDER_LETTER, mLetterKeyboard)
        mCurrentOrder = ORDER_LETTER
        onCurrentKeyboardChange()
    }

    private fun initKeyboardChooser() {
        mBinding.tvNumber.setOnClickListener {
            mCurrentOrder = ORDER_NUMBER
            onCurrentKeyboardChange()
        }
        mBinding.tvSymbol.setOnClickListener {
            mCurrentOrder = ORDER_SYMBOL
            onCurrentKeyboardChange()
        }
        mBinding.tvLetter.setOnClickListener {
            mCurrentOrder = ORDER_LETTER
            onCurrentKeyboardChange()
        }
    }

    private fun onCurrentKeyboardChange() {
        if (mCurrentOrder == ORDER_NUMBER && isNumberRandom) {
            randomNumbers()
        }
        mBinding.keyboardView.keyboard = mOrderToKeyboard[mCurrentOrder]
        when (mCurrentOrder) {
            ORDER_NUMBER -> {
                mBinding.tvNumber.setTextColor(mSelectedTextColor)
                mBinding.tvSymbol.setTextColor(mUnSelectedTextColor)
                mBinding.tvLetter.setTextColor(mUnSelectedTextColor)
            }

            ORDER_SYMBOL -> {
                mBinding.tvNumber.setTextColor(mUnSelectedTextColor)
                mBinding.tvSymbol.setTextColor(mSelectedTextColor)
                mBinding.tvLetter.setTextColor(mUnSelectedTextColor)
            }

            ORDER_LETTER -> {
                mBinding.tvNumber.setTextColor(mUnSelectedTextColor)
                mBinding.tvSymbol.setTextColor(mUnSelectedTextColor)
                mBinding.tvLetter.setTextColor(mSelectedTextColor)
            }

            else -> throw IllegalStateException(context.getString(R.string.exception_invalid_keyboard))
        }
    }

    private fun isPortrait(): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 键盘数字随机切换
     */
    private fun randomNumbers() {
        if (mNumberKeyboard != null) {
            val source = ArrayList(mNumberPool)
            val keys = mNumberKeyboard!!.keys
            for (key in keys) {
                if (key.label != null && isNumber(key.label.toString())) {
                    val number = Random().nextInt(source.size)
                    val text = source[number].split("#".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    key.label = text[1]
                    key.codes[0] = Integer.valueOf(text[0], 10)
                    source.removeAt(number)
                }
            }
        }
    }

    private fun isNumber(str: String): Boolean {
        val numStr = context.getString(R.string.zeroToNine)
        return numStr.contains(str.lowercase(Locale.getDefault()))
    }

    override fun onPress(primaryCode: Int) {
        Log.d(TAG, "onPress: primaryCode=${primaryCode}")
    }

    override fun onRelease(primaryCode: Int) {

    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray) {
        Log.d(TAG, "onKey: primaryCode=$primaryCode")
        val editable = mTargetEditText.get()!!.text
        val start = mTargetEditText.get()!!.selectionStart
        if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            hideKeyboard()
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (editable != null && editable.length > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start)
                }
            }
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            changeKey()
            mCurrentOrder = ORDER_LETTER
            onCurrentKeyboardChange()
        } else {
            editable!!.insert(start, Character.toString(primaryCode.toChar()))
        }
    }

    /**
     * 键盘大小写切换
     */
    private fun changeKey() {
        if (mLetterKeyboard != null) {
            val keys = mLetterKeyboard!!.keys
            if (isUpper) {
                isUpper = false
                for (key in keys) {
                    if (key.label != null && isLetter(key.label.toString())) {
                        key.label = key.label.toString().lowercase(Locale.getDefault())
                        key.codes[0] = key.codes[0] + 32
                    }
                    if (key.codes[0] == -1) {
                        key.icon = context.resources.getDrawable(
                            R.drawable.keyboard_shift
                        )
                    }
                }
            } else { // 小写切换大写
                isUpper = true
                for (key in keys) {
                    if (key.label != null && isLetter(key.label.toString())) {
                        key.label = key.label.toString().uppercase(Locale.getDefault())
                        key.codes[0] = key.codes[0] - 32
                    }
                    if (key.codes[0] == -1) {
                        key.icon = context.resources.getDrawable(
                            R.drawable.keyboard_shift_c
                        )
                    }
                }
            }
        }
    }

    private fun isLetter(str: String): Boolean {
        val letterStr = context.getString(R.string.aToz)
        return letterStr.contains(str.lowercase(Locale.getDefault()))
    }


    private fun hideKeyboard() {
        this.dismiss()
    }

    override fun onText(text: CharSequence?) {}

    override fun swipeLeft() {}

    override fun swipeRight() {}

    override fun swipeDown() {}

    override fun swipeUp() {}
}