package com.android.todohelper.utils


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.todohelper.activity.UserActivity
import com.android.todohelper.data.User
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

var mLastClickTime: Long = 0


fun Context.makeAllertDialogNO(message: String, negativeButton: String) =
    AlertDialog.Builder(this).setMessage(message)
        .setNegativeButton(negativeButton, null)
        .create()
        .show()


fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


fun Context.userActivityIntent(it: User, isFromWidget: Boolean): Intent {

    return Intent(this, UserActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .apply {
            if (isFromWidget) {
                putExtra("fromWidget", "true")
            }
            putExtra("name", it.name)
            putExtra("lastname", it.lastName)
            putExtra("id", it.id.toString())
        }

}

fun EditText.isEmpty(): Boolean {
    return this.text.toString().trim().isEmpty()
}


//
//fun Context.LoginActivityIntent(): Intent {
//    return Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//}


inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T {
    when (T::class) {
        Boolean::class -> return this.getBoolean(key, defaultValue as Boolean) as T
        Float::class -> return this.getFloat(key, defaultValue as Float) as T
        Int::class -> return this.getInt(key, defaultValue as Int) as T
        Long::class -> return this.getLong(key, defaultValue as Long) as T
        String::class -> return this.getString(key, defaultValue as String) as T
        else -> {
            if (defaultValue is Set<*>) {
                return this.getStringSet(key, defaultValue as Set<String>) as T
            }
        }
    }

    return defaultValue
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            }
        }
    }
    editor.apply()
}


fun Editable.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun String.isOnlyLetters(): Boolean {
    this.trim()
    var count = 0
    var letter: Char?
    while (count <= this.length - 1) {
        letter = this[count]
        if (!Character.isLetter(letter)) {
            return false
        }
        count++
    }
    return true
}


fun String.isValidPhone(): Boolean {
    return Patterns.PHONE.matcher(this).matches()
}

fun String.isValidURL(): Boolean {
    return Patterns.WEB_URL.matcher(this).matches()
}


class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    companion object {
        private const val TAG = "SingleLiveEvent"
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.i(TAG, "Multiple observers registered but only one will be notified of changes")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

}

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
    permissionsArray: Array<String>,
    requestCode: Int
                                              ) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}


fun preventMultiClick(): Boolean {
    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
        return true
    }
    mLastClickTime = SystemClock.elapsedRealtime()
    return false
}

//fun EditText.showKeyboard() {
//    this.post {
//        val inputMethodManager: InputMethodManager =
//            this.context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.toggleSoftInputFromWindow(
//            this.applicationWindowToken, InputMethodManager.SHOW_IMPLICIT, 0
//        )
//        this.requestFocus()
//
//    }
//}

fun getCurrentTime(): String {
    return SimpleDateFormat("dd-MM-yy HH:mm")
        .format(Calendar.getInstance().time)
}


fun makeMovebleOnTouchListener(
    sharedPreferences: SharedPreferences,
    onClick: () -> Unit): View.OnTouchListener {
    var dX: Float = 0.0f
    var dY: Float = 0.0f
    var lastAction: Int = 0
    return View.OnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
                lastAction = MotionEvent.ACTION_DOWN
            }
            MotionEvent.ACTION_MOVE -> {
                lastAction = MotionEvent.ACTION_MOVE
                v.y = event.rawY + dY
                v.x = event.rawX + dX
                sharedPreferences.put(SHARED_POSITION_LOGOUT_BUTTON, "${v.x}!${v.y}")
            }
            MotionEvent.ACTION_UP -> {
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    onClick.invoke()
                }
            }
            else -> {
            }
        }; true
    }
}













