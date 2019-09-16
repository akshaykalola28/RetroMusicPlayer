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

package code.name.monkey.retromusic.dialogs

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import code.name.monkey.retromusic.R
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_lyrics.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class BlacklistFolderChooserDialog : DialogFragment(), MaterialDialog.ListCallback {
    override fun onSelection(dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence?) {
        if (canGoUp && position == 0) {
            parentFolder = parentFolder!!.parentFile
            if (parentFolder!!.absolutePath == "/storage/emulated") {
                parentFolder = parentFolder!!.parentFile
            }
            checkIfCanGoUp()
        } else {
            parentFolder = parentContents!![if (canGoUp) position - 1 else position]
            canGoUp = true
            if (parentFolder!!.absolutePath == "/storage/emulated") {
                parentFolder = Environment.getExternalStorageDirectory()
            }
        }
        reload()
    }

    private val initialPath = Environment.getExternalStorageDirectory().absolutePath
    private var parentFolder: File? = null
    private var parentContents: Array<File>? = null
    private var canGoUp = false
    private var callback: FolderCallback? = null

    private fun contentsArray(): List<String> {
        if (parentContents == null) {
            return if (canGoUp) {
                return listOf("..")
            } else listOf()
        }

        val results = arrayOfNulls<String>(parentContents!!.size + if (canGoUp) 1 else 0)
        if (canGoUp) {
            results[0] = ".."
        }
        for (i in parentContents!!.indices) {
            results[if (canGoUp) i + 1 else i] = parentContents!![i].name
        }

        val data = ArrayList<String>()
        for (i in results) {
            data.add(i!!)
        }
        return data
    }

    private fun listFiles(): Array<File>? {
        val contents = parentFolder!!.listFiles()
        val results = ArrayList<File>()
        if (contents != null) {
            for (fi in contents) {
                if (fi.isDirectory) {
                    results.add(fi)
                }
            }
            Collections.sort(results, FolderSorter())
            return results.toTypedArray()
        }
        return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var savedInstanceStateFinal = savedInstanceState
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return MaterialDialog.Builder(requireActivity())
                    .title(R.string.md_error_label)
                    .content(R.string.md_storage_perm_error)
                    .positiveText(android.R.string.ok)
                    .build()
        }
        if (savedInstanceStateFinal == null) {
            savedInstanceStateFinal = Bundle()
        }
        if (!savedInstanceStateFinal.containsKey("current_path")) {
            savedInstanceStateFinal.putString("current_path", initialPath)
        }
        parentFolder = File(savedInstanceStateFinal.getString("current_path", File.pathSeparator))
        checkIfCanGoUp()
        parentContents = listFiles()

        val builder = MaterialDialog.Builder(requireActivity())
                .title(parentFolder!!.absolutePath)
                .items(contentsArray())
                .itemsCallback(this)
                .autoDismiss(false)
                .positiveText(R.string.add_action)
                .negativeText(android.R.string.cancel)
                .onPositive { _, _ ->
                    dismiss()
                    callback?.onFolderSelection(this@BlacklistFolderChooserDialog, parentFolder!!)
                }.onNegative { dialog, _ ->
                    dialog.dismiss()
                }

        return builder.build()
    }


    private fun checkIfCanGoUp() {
        canGoUp = parentFolder!!.parent != null
    }

    private fun reload() {
        parentContents = listFiles()
        val dialog = dialog as MaterialDialog?

        dialog?.apply {
            setTitle(parentFolder!!.absolutePath)
            setItems(*contentsArray().toTypedArray())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("current_path", parentFolder!!.absolutePath)
    }

    fun setCallback(callback: FolderCallback) {
        this.callback = callback
    }

    interface FolderCallback {
        fun onFolderSelection(dialog: BlacklistFolderChooserDialog, folder: File)
    }

    private class FolderSorter : Comparator<File> {

        override fun compare(lhs: File, rhs: File): Int {
            return lhs.name.compareTo(rhs.name)
        }
    }

    companion object {

        fun create(): BlacklistFolderChooserDialog {
            return BlacklistFolderChooserDialog()
        }
    }
}