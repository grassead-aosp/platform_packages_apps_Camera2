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

package com.android.gallery3d.ui;

import android.content.Context;
import android.view.MotionEvent;

import com.android.gallery3d.R;
import com.android.gallery3d.util.GalleryUtils;

public class UndoBarView extends GLView {
    private static final String TAG = "UndoBarView";

    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFFAAAAAA;

    private final NinePatchTexture mPanel;
    private final StringTexture mUndoText;
    private final StringTexture mDeletedText;
    private final ResourceTexture mUndoIcon;
    private final int mBarHeight;
    private final int mBarMargin;
    private final int mUndoTextMargin;
    private final int mIconSize;
    private final int mIconMargin;
    private final int mSeparatorTopMargin;
    private final int mSeparatorBottomMargin;
    private final int mSeparatorRightMargin;
    private final int mSeparatorWidth;
    private final int mDeletedTextMargin;
    private final int mClickRegion;

    private OnClickListener mOnClickListener;
    private boolean mDownOnButton;

    // This is the layout of UndoBarView. The unit is dp.
    //
    //    +-+----+----------------+-+--+----+-+------+--+-+
    // 48 | |    | Deleted        | |  | <- | | UNDO |  | |
    //    +-+----+----------------+-+--+----+-+------+--+-+
    //     4  16                   1 12  32  8        16 4
    public UndoBarView(Context context) {
        mBarHeight = (int) GalleryUtils.dpToPixel(48);
        mBarMargin = (int) GalleryUtils.dpToPixel(4);
        mUndoTextMargin = (int) GalleryUtils.dpToPixel(16);
        mIconMargin = (int) GalleryUtils.dpToPixel(8);
        mIconSize = (int) GalleryUtils.dpToPixel(32);
        mSeparatorRightMargin = (int) GalleryUtils.dpToPixel(12);
        mSeparatorTopMargin = (int) GalleryUtils.dpToPixel(10);
        mSeparatorBottomMargin = (int) GalleryUtils.dpToPixel(10);
        mSeparatorWidth = (int) GalleryUtils.dpToPixel(1);
        mDeletedTextMargin = (int) GalleryUtils.dpToPixel(16);

        mPanel = new NinePatchTexture(context, R.drawable.panel_undo_holo);
        mUndoText = StringTexture.newInstance(context.getString(R.string.undo),
                GalleryUtils.dpToPixel(12), GRAY, 0, true);
        mDeletedText = StringTexture.newInstance(
                context.getString(R.string.deleted),
                GalleryUtils.dpToPixel(16), WHITE);
        mUndoIcon = new ResourceTexture(
                context, R.drawable.ic_menu_revert_holo_dark);
        mClickRegion = mBarMargin + mUndoTextMargin + mUndoText.getWidth()
                + mIconMargin + mIconSize + mSeparatorRightMargin;
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredSize(0 /* unused */, mBarHeight);
    }

    @Override
    protected void render(GLCanvas canvas) {
        super.render(canvas);
        int w = getWidth();
        int h = getHeight();
        mPanel.draw(canvas, mBarMargin, 0, w - mBarMargin * 2, mBarHeight);

        int x = w - mBarMargin;
        int y;

        x -= mUndoTextMargin + mUndoText.getWidth();
        y = (mBarHeight - mUndoText.getHeight()) / 2;
        mUndoText.draw(canvas, x, y);

        x -= mIconMargin + mIconSize;
        y = (mBarHeight - mIconSize) / 2;
        mUndoIcon.draw(canvas, x, y, mIconSize, mIconSize);

        x -= mSeparatorRightMargin + mSeparatorWidth;
        y = mSeparatorTopMargin;
        canvas.fillRect(x, y, mSeparatorWidth,
                mBarHeight - mSeparatorTopMargin - mSeparatorBottomMargin, GRAY);

        x = mBarMargin + mDeletedTextMargin;
        y = (mBarHeight - mDeletedText.getHeight()) / 2;
        mDeletedText.draw(canvas, x, y);
    }

    @Override
    protected boolean onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownOnButton = inUndoButton(event);
                break;
            case MotionEvent.ACTION_UP:
                if (mDownOnButton) {
                    if (mOnClickListener != null && inUndoButton(event)) {
                        mOnClickListener.onClick(this);
                    }
                    mDownOnButton = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDownOnButton = false;
                break;
        }
        return true;
    }

    // Check if the event is on the right of the separator
    private boolean inUndoButton(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int w = getWidth();
        int h = getHeight();
        return (x >= w - mClickRegion && x < w && y >= 0 && y < h);
    }
}
