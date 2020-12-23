package com.zomato.utils


import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.zomato.App
import com.zomato.R
import java.lang.ref.WeakReference


class DiscreteScrollViewOptions {


    private var KEY_TRANSITION_TIME: String? = null

    companion object {
        private var instance: DiscreteScrollViewOptions? = null
        public fun init(context: Context) {
            instance = DiscreteScrollViewOptions()

        }
        fun getTransitionTime(): Int {
            return defaultPrefs().getInt(instance!!.KEY_TRANSITION_TIME, 150)
        }
        private fun defaultPrefs(): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(App.getInstance())
        }


    }


    private fun DiscreteScrollViewOptions(context: Context) {
        KEY_TRANSITION_TIME = context.getString(R.string.pref_key_transition_time)
    }

    /*fun configureTransitionTime(scrollView: DiscreteScrollView) {
        val bsd = BottomSheetDialog(scrollView.context)
        val timeChangeListener =
            TransitionTimeChangeListener(scrollView,instance)
        bsd.setContentView(R.layout.dialog_transition_time)
        defaultPrefs().registerOnSharedPreferenceChangeListener(timeChangeListener)
        bsd.setOnDismissListener {
            defaultPrefs().unregisterOnSharedPreferenceChangeListener(
                timeChangeListener
            )
        }
        val dismissBtn: View = bsd.findViewById(R.id.dialog_btn_dismiss)
        if (dismissBtn != null) {
            dismissBtn.setOnClickListener {
                bsd.dismiss()
            }

        }
        bsd.show()
    }*/

    fun smoothScrollToUserSelectedPosition(
        scrollView: DiscreteScrollView,
        anchor: View?
    ) {
        val popupMenu =
            PopupMenu(scrollView.context, anchor!!)
        val menu = popupMenu.menu
        val adapter = scrollView.adapter
        val itemCount =
            if (adapter is InfiniteScrollAdapter<*>) adapter.realItemCount else adapter?.itemCount
                ?: 0
        for (i in 0 until itemCount) {
            menu.add((i + 1).toString())
        }
        popupMenu.setOnMenuItemClickListener { item ->
            var destination = item.title.toString().toInt() - 1
            if (adapter is InfiniteScrollAdapter<*>) {
                destination =
                    adapter.getClosestPosition(destination)
            }
            scrollView.smoothScrollToPosition(destination)
            true
        }
        popupMenu.show()
    }



    private class TransitionTimeChangeListener(
        scrollView: DiscreteScrollView?,
        var instance: DiscreteScrollViewOptions?
    ) :
        OnSharedPreferenceChangeListener {
        private val scrollView: WeakReference<DiscreteScrollView?>?
        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            if (key == instance!!.KEY_TRANSITION_TIME) {
                val scrollView: DiscreteScrollView = scrollView!!.get()!!
                if (scrollView != null) {
                    scrollView.setItemTransitionTimeMillis(sharedPreferences!!.getInt(key, 150))
                } else {
                    sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
                }
            }
        }

        init {
            this.scrollView = WeakReference(scrollView)
        }
    }

}