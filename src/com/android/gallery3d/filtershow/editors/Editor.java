/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.filtershow.editors;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.gallery3d.R;
import com.android.gallery3d.filtershow.PanelController;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.filters.FilterRepresentation;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import com.android.gallery3d.filtershow.imageshow.MasterImage;
import com.android.gallery3d.filtershow.presets.ImagePreset;

/**
 * Base class for Editors Must contain a mImageShow and a top level view
 */
public class Editor implements OnSeekBarChangeListener {
    protected Context mContext;
    protected View mView;
    protected ImageShow mImageShow;
    protected FrameLayout mFrameLayout;
    protected SeekBar mSeekBar;
    protected PanelController mPanelController;
    protected int mID;
    private final String LOGTAG = "Editor";
    protected FilterRepresentation mLocalRepresentation = null;
    protected byte mShowParameter = SHOW_VALUE_UNDEFINED;
    public static byte SHOW_VALUE_UNDEFINED = -1;
    public static byte SHOW_VALUE_OFF = 0;
    public static byte SHOW_VALUE_INT = 1;

    public void setPanelController(PanelController panelController) {
        this.mPanelController = panelController;
    }

    public String calculateUserMessage(Context context, String effectName, Object parameterValue) {
        String apply = context.getString(R.string.apply_effect);
        if (mShowParameter == SHOW_VALUE_INT) {
            apply += " " + effectName + " " + parameterValue;
        } else {
            apply += " " + effectName;
        }
        return apply;
    }

    protected Editor(int id) {
        mID = id;
    }

    public int getID() {
        return mID;
    }


    public byte showParameterValue() {
        return mShowParameter;
    }

    /**
     * @param actionButton the would be the area for menu etc
     * @param editControl this is the black area for sliders etc
     */
    public void setUtilityPanelUI(View actionButton, View editControl) {
        mSeekBar = (SeekBar) editControl.findViewById(R.id.primarySeekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar sbar, int progress, boolean arg2) {

    }

    public void setPanel() {

    }

    public void createEditor(Context context,FrameLayout frameLayout) {
        mContext = context;
        mFrameLayout = frameLayout;
        mLocalRepresentation = null;
    }

    protected void unpack(int viewid, int layoutid) {

        if (mView == null) {
            mView = mFrameLayout.findViewById(viewid);
            if (mView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                mView = inflater.inflate(layoutid, mFrameLayout, false);
                mFrameLayout.addView(mView, mView.getLayoutParams());
            }
        }
        mImageShow = findImageShow(mView);
    }

    private ImageShow findImageShow(View view) {
        if (view instanceof ImageShow) {
            return (ImageShow) view;
        }
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup vg = (ViewGroup) view;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof ImageShow) {
                return (ImageShow) v;
            } else if (v instanceof ViewGroup) {
                return findImageShow(v);
            }
        }
        return null;
    }

    public View getTopLevelView() {
        return mView;
    }

    public ImageShow getImageShow() {
        return mImageShow;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageShow.setImageLoader(imageLoader);
    }

    public void setVisibility(int visible) {
        mView.setVisibility(visible);
    }

    public FilterRepresentation getLocalRepresentation() {
        if (mLocalRepresentation == null) {
            ImagePreset preset = MasterImage.getImage().getPreset();
            FilterRepresentation filterRepresentation = MasterImage.getImage().getCurrentFilterRepresentation();
            mLocalRepresentation = preset.getFilterRepresentationCopyFrom(filterRepresentation);
            if (mShowParameter == SHOW_VALUE_UNDEFINED) {
                boolean show = filterRepresentation.showParameterValue();
                mShowParameter = show ? SHOW_VALUE_INT : SHOW_VALUE_OFF;
            }
        }

        return mLocalRepresentation;
    }

    public void commitLocalRepresentation() {
        ImagePreset preset = MasterImage.getImage().getPreset();
        preset.updateFilterRepresentation(getLocalRepresentation());
    }

    /**
     * called after the filter is set and the select is called
     */
    public void reflectCurrentFilter() {
        mLocalRepresentation = null;
    }

    public boolean useUtilityPanel() {
        if (mImageShow != null) {
            return mImageShow.useUtilityPanel();
        }
        return false;
    }

    public void openUtilityPanel(LinearLayout mAccessoryViewList) {
        if (mImageShow != null) {
            mImageShow.openUtilityPanel(mAccessoryViewList);
        }
    }

    protected void createMenu(int[] strId, View button) {
        PopupMenu pmenu = new PopupMenu(mContext, button);
        Menu menu = pmenu.getMenu();
        for (int i = 0; i < strId.length; i++) {
            menu.add(Menu.NONE, Menu.FIRST + i, 0, mContext.getString(strId[i]));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {

    }

}
