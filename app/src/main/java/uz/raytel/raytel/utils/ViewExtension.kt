package uz.raytel.raytel.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import uz.raytel.raytel.app.App

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun toast(msg: String) {
    Toast.makeText(App.INSTANCE, msg, Toast.LENGTH_SHORT).show()
}

fun showSnackBar(view: View, msg: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, msg, length).show()
}

fun Fragment.showSnackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(requireView(), msg, length).show()
}

fun ImageView.setImageWithGlide(context: Context, url: String) {
    Glide.with(context).load(url).into(this)
}

val String.toPhoneNumber: String
    get() {
        val arr = this.toCharArray()
        var phone = if (arr.size == 9) "(" else "+998 ("
        arr.forEachIndexed { index, c ->
            phone += c
            if (index == 1) {
                phone += ") "
            }
            if (index == 4 || index == 6) {
                phone += " "
            }
        }
        return phone
    }

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.click(block: (it: View) -> Unit) {
    this.setOnClickListener(block)
}

fun View.clickWithDebounce(scope: CoroutineScope, block: (it: View) -> Unit) {
    this.clicks().debounce(200).onEach {
        block.invoke(this)
    }.launchIn(scope)
}

val String.isValidPhoneNumber: Boolean
    get() {
        val phone = this.filter { it.isDigit() }
        return phone.length == 9
    }

val Int.toSumFormat: String
    get() {
        var text = this.toString().reversed()
        text = text.subSequence(0, text.length).chunked(3) // group every 3 chars
            .joinToString(" ")
        return text.reversed()
    }


fun View.setScreenShotAnimation() {
    this.show()
    val fade = AlphaAnimation(1f, 0f);
    fade.duration = 600;
    fade.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
            this@setScreenShotAnimation.show()
        }

        override fun onAnimationEnd(p0: Animation?) {
            this@setScreenShotAnimation.hide()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })
    this.startAnimation(fade);
}