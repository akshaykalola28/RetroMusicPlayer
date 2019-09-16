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

package code.name.monkey.appthemehelper.common.prefs.supportv7.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.DialogPreference;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ATEPreferenceDialogFragment extends DialogFragment implements MaterialDialog.SingleButtonCallback {
    protected static final String ARG_KEY = "key";
    private DialogAction mWhichButtonClicked;
    private DialogPreference mPreference;

    public static ATEPreferenceDialogFragment newInstance(String key) {
        ATEPreferenceDialogFragment fragment = new ATEPreferenceDialogFragment();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment rawFragment = this.getTargetFragment();
        if (!(rawFragment instanceof DialogPreference.TargetFragment)) {
            throw new IllegalStateException("Target fragment must implement TargetFragment interface");
        } else {
            DialogPreference.TargetFragment fragment = (DialogPreference.TargetFragment) rawFragment;
            String key = this.getArguments().getString(ARG_KEY);
            this.mPreference = (DialogPreference) fragment.findPreference(key);
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity context = this.getActivity();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(this.mPreference.getDialogTitle())
                .icon(this.mPreference.getDialogIcon())
                .onAny(this)
                .positiveText(this.mPreference.getPositiveButtonText())
                .negativeText(this.mPreference.getNegativeButtonText());

        builder.content(this.mPreference.getDialogMessage());
        this.onPrepareDialogBuilder(builder);
        MaterialDialog dialog = builder.build();
        if (this.needInputMethod()) {
            this.requestInputMethod(dialog);
        }

        return dialog;
    }

    public DialogPreference getPreference() {
        return this.mPreference;
    }

    protected void onPrepareDialogBuilder(MaterialDialog.Builder builder) {
    }

    protected boolean needInputMethod() {
        return false;
    }

    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(5);
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        mWhichButtonClicked = which;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogClosed(mWhichButtonClicked == DialogAction.POSITIVE);
    }

    public void onDialogClosed(boolean positiveResult) {

    }
}