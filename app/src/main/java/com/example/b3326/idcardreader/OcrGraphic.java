/*
 * Copyright (C) The Android Open Source Project
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
package com.example.b3326.idcardreader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.example.b3326.idcardreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock mTextBlock;

    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        mTextBlock = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(54.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mTextBlock;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        TextBlock text = mTextBlock;
        if (text == null) {
            Log.d("mTouch","text==null");
            return false;
        }
        Log.d("mTouch","Text="+text.getValue());
        RectF rect = new RectF(text.getBoundingBox());

        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);

        Log.d("mTouch","left:"+rect.left+" top:"+rect.top+" right"+rect.right+" bottom:"+rect.bottom) ;
        Log.d("mTouch","x:"+x+" y:"+y) ;
        if(OcrCaptureActivity.faceTo==1) {
            return (rect.left > x && rect.right < x && rect.top < y && rect.bottom > y);
        }
        else if (OcrCaptureActivity.faceTo==0) {
            return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
        }
        else
            return false;
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        TextBlock text = mTextBlock;
        if (text == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(text.getBoundingBox());


        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);




        // Break the text into multiple lines and draw each one according to its own bounding box.
        List<? extends Text> textComponents = text.getComponents();
        for(Text currentText : textComponents) {

            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
           // String ID_number=ID(currentText.getValue());
            /*if(ID_number!="")*/ {

                canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
                if(OcrCaptureActivity.faceTo==1) {
                    rect.right=swap(rect.left,rect.left=rect.right);
                }

                canvas.drawRect(rect, sRectPaint);
                Log.d("mTouch","RECT="+rect.toShortString());
            }
        }
    }
    public float swap(float x, float y) {
        return x;
    }
//    public String ID(String str) {
//
//        String reg = "[A-Z]\\d{9}";
//        String Result = "";
//
//        //將規則封裝成物件
//        Pattern p = Pattern.compile(reg);
//
//        //讓正則物件與要作用的字串相關聯
//        Matcher m = p.matcher(str);
//        //System.out.println(m.matches());
//        //String類中的matches方法,即使用Pattern和Matcher類的matcher方法
//        //只不過String方法封裝後, 使用較簡單, 但功能一致
//
//        //將規則作用到字串上, 並進行符合規則的子串查找
//        while (m.find()) {
//            //System.out.println(m.group() + " ");  //用於獲取匹配後結果
//            Result =m.group();
//        }
//        //Log.d("WillyCheng",Result);
//        return Result;
//
//    }
}
