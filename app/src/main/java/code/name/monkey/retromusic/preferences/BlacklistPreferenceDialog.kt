/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.preferences

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.dialogs.BlacklistFolderChooserDialog
import code.name.monkey.retromusic.providers.BlacklistStore
import com.afollestad.materialdialogs.MaterialDialog
import java.io.File
import java.util.*

class BlacklistPreference : ATEDialogPreference {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        icon?.setColorFilter(ThemeStore.textColorSecondary(context), PorterDuff.Mode.SRC_IN)
    }
}

class BlacklistPreferenceDialog : DialogFragment(), BlacklistFolderChooserDialog.FolderCallback {
    companion object {
        fun newInstance(): BlacklistPreferenceDialog {
            return BlacklistPreferenceDialog()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val blacklistFolderChooserDialog = childFragmentManager.findFragmentByTag("FOLDER_CHOOSER") as BlacklistFolderChooserDialog?
        blacklistFolderChooserDialog?.setCallback(this)
        refreshBlacklistData()
        return MaterialDialog.Builder(requireActivity())
                .title(R.string.blacklist)
                .positiveText(android.R.string.ok)
                .neutralText(R.string.clear_action)
                .negativeText(R.string.add_action)
                .items(paths)
                .autoDismiss(false)
                .itemsCallback { _, _, _, text ->
                    MaterialDialog.Builder(requireActivity())
                            .title(R.string.remove_from_blacklist)
                            .content(Html.fromHtml(getString(R.string.do_you_want_to_remove_from_the_blacklist, text)))
                            .positiveText(R.string.remove_action)
                            .negativeText(android.R.string.cancel)
                            .onPositive { _, _ ->
                                BlacklistStore.getInstance(requireContext()).removePath(File(text.toString()))
                                refreshBlacklistData()
                            }
                            .show()
                }
                .onNeutral { _, _ ->
                    MaterialDialog.Builder(requireActivity())
                            .title(R.string.clear_blacklist)
                            .content(R.string.do_you_want_to_clear_the_blacklist)
                            .positiveText(R.string.clear_action)
                            .negativeText(android.R.string.cancel)
                            .onPositive { _, _ ->
                                BlacklistStore.getInstance(requireContext()).clear()
                                refreshBlacklistData()
                            }
                            .show()
                }
                .onNegative { _, _ ->
                    val dialog = BlacklistFolderChooserDialog.create()
                    dialog.setCallback(this@BlacklistPreferenceDialog)
                    dialog.show(childFragmentManager, "FOLDER_CHOOSER");
                }
                .onPositive { _, _ ->
                    dismiss()
                }
                .build()
        /*return MaterialDialog(context!!, BottomSheet()).show {
            title(code.name.monkey.retromusic.R.string.blacklist)
            positiveButton(android.R.string.ok) {
                dismiss()
            }
            neutralButton(text = getString(R.string.clear_action)) {
                MaterialDialog(context, BottomSheet()).show {
                    title(code.name.monkey.retromusic.R.string.clear_blacklist)
                    message(code.name.monkey.retromusic.R.string.do_you_want_to_clear_the_blacklist)
                    positiveButton(code.name.monkey.retromusic.R.string.clear_action) {
                        BlacklistStore.getInstance(context).clear()
                        refreshBlacklistData()
                    }
                    negativeButton(android.R.string.cancel)
                }
            }
            negativeButton(R.string.add_action) {
                val dialog = BlacklistFolderChooserDialog.create()
                dialog.setCallback(this@BlacklistPreferenceDialog)
                dialog.show(childFragmentManager, "FOLDER_CHOOSER");
            }
            listItems(items = paths, waitForPositiveButton = false) { _, _, text ->
                MaterialDialog(context, BottomSheet()).show {
                    title(code.name.monkey.retromusic.R.string.remove_from_blacklist)
                    message(text = Html.fromHtml(getString(code.name.monkey.retromusic.R.string.do_you_want_to_remove_from_the_blacklist, text)))
                    positiveButton(code.name.monkey.retromusic.R.string.remove_action) {
                        BlacklistStore.getInstance(context).removePath(File(text.toString()))
                        refreshBlacklistData()
                    }
                    negativeButton(android.R.string.cancel)
                }
            }
            noAutoDismiss()
        }*/
    }

    private lateinit var paths: ArrayList<String>

    private fun refreshBlacklistData() {
        this.paths = BlacklistStore.getInstance(context!!).paths
        val dialog = dialog as MaterialDialog?
        dialog?.setItems(*paths.toTypedArray())
    }

    override fun onFolderSelection(dialog: BlacklistFolderChooserDialog, folder: File) {
        BlacklistStore.getInstance(context!!).addPath(folder);
        refreshBlacklistData();
    }
}
