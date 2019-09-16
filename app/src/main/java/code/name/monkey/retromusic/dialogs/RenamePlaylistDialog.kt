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
import android.os.Bundle
import android.provider.MediaStore.Audio.Playlists.Members.PLAYLIST_ID
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import code.name.monkey.appthemehelper.util.MaterialUtil
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.extensions.appHandleColor
import code.name.monkey.retromusic.util.PlaylistsUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class RenamePlaylistDialog : DialogFragment() {
    private lateinit var playlistView: TextInputEditText
    private lateinit var actionNewPlaylistContainer: TextInputLayout

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_playlist, null)
        playlistView = view.findViewById(R.id.actionNewPlaylist)
        actionNewPlaylistContainer = view.findViewById(R.id.actionNewPlaylistContainer)
        val materialDialogBuilder = MaterialDialog.Builder(requireActivity())
                .customView(view, false)
                .title(R.string.rename_playlist_title)
                .positiveText(R.string.rename_action)
                .negativeText(android.R.string.cancel)
                .onPositive { _, _ ->
                    val name = playlistView.text.toString().trim()
                    if (name.isNotEmpty()) {
                        val playlistId = arguments!!.getLong(PLAYLIST_ID)
                        PlaylistsUtil.renamePlaylist(requireContext(), playlistId, playlistView.text!!.toString())
                    }
                }
                .build()
        MaterialUtil.setTint(actionNewPlaylistContainer, false)

        val playlistId = arguments!!.getLong(PLAYLIST_ID)
        playlistView.appHandleColor().setText(PlaylistsUtil.getNameForPlaylist(context!!, playlistId), TextView.BufferType.EDITABLE)
        return materialDialogBuilder
    }

    companion object {

        fun create(playlistId: Long): RenamePlaylistDialog {
            val dialog = RenamePlaylistDialog()
            val args = Bundle()
            args.putLong(PLAYLIST_ID, playlistId)
            dialog.arguments = args
            return dialog
        }
    }
}