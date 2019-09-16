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
import androidx.fragment.app.DialogFragment
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.loaders.PlaylistLoader
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PlaylistsUtil
import com.afollestad.materialdialogs.MaterialDialog


class AddToPlaylistDialog : DialogFragment() {

    override fun onCreateDialog(
            savedInstanceState: Bundle?
    ): Dialog {

        val list = PlaylistLoader.getAllPlaylists(requireContext())
        val playlistNames: MutableList<String> = mutableListOf()
        playlistNames.add(resources.getString(R.string.action_new_playlist))
        for (p in list) {
            playlistNames.add(p.name)
        }
        return MaterialDialog.Builder(requireActivity()).title(R.string.add_playlist_title)
                .items(playlistNames)
                .itemsCallback { dialog, itemView, position, text ->
                    val songs = arguments!!.getParcelableArrayList<Song>("songs")
                            ?: return@itemsCallback
                    if (position == 0) {
                        dialog.dismiss();
                        CreatePlaylistDialog.create(songs).show(requireActivity().supportFragmentManager, "ADD_TO_PLAYLIST");
                    } else {
                        dialog.dismiss()
                        PlaylistsUtil.addToPlaylist(requireContext(), songs, list[position - 1].id, true)
                    }
                }
                .build()
    }

    companion object {

        fun create(song: Song): AddToPlaylistDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<Song>): AddToPlaylistDialog {
            val dialog = AddToPlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}