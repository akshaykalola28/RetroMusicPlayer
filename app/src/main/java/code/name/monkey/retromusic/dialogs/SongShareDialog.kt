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

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.MusicUtil
import com.afollestad.materialdialogs.MaterialDialog


class SongShareDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val song: Song? = arguments!!.getParcelable("song")
        val currentlyListening: String = getString(R.string.currently_listening_to_x_by_x, song?.title, song?.artistName)

        return MaterialDialog.Builder(requireActivity())
                .title(R.string.what_do_you_want_to_share)
                .items(getString(R.string.the_audio_file), "\u201C" + currentlyListening + "\u201D")
                .itemsCallback { _, _, position, text ->
                    when (position) {
                        0 -> startActivity(Intent.createChooser(song?.let { MusicUtil.createShareSongFileIntent(it, requireActivity()) }, null))
                        1 -> requireActivity().startActivity(
                                Intent.createChooser(
                                        Intent()
                                                .setAction(Intent.ACTION_SEND)
                                                .putExtra(Intent.EXTRA_TEXT, currentlyListening)
                                                .setType("text/plain"),
                                        null
                                )
                        )
                    }
                }
                .build()
    }

    companion object {

        fun create(song: Song): SongShareDialog {
            val dialog = SongShareDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }
    }
}
