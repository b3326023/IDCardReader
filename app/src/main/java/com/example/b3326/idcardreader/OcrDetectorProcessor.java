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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.example.b3326.idcardreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Context mcontext;
    private Activity mactivity;
    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay,Activity activity) {
        mGraphicOverlay = ocrGraphicOverlay;
        //mcontext=context;
        mactivity=activity;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            String result=ID(item.getValue());
            if (result!="") {
               // Log.d("mTouch","result!=null");
                gotID(item.getValue());
                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
            }
        }
    }
    public void gotID(String ID)
    {
        //Log.d("mTouch","gotID()="+ID);
        Intent data = new Intent();
        data.putExtra("ID",ID);
        mactivity.setResult(CommonStatusCodes.SUCCESS, data);
        //Log.d("mTouch","gotID()");
        mactivity.finish();
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
    public String ID(String str) {

        String reg = "[A-Z]\\d{9}\n\\d{2}+/\\d{2}/\\d{2}";
        String Result = "";

        //將規則封裝成物件
        Pattern p = Pattern.compile(reg);

        //讓正則物件與要作用的字串相關聯
        Matcher m = p.matcher(str);
        //System.out.println(m.matches());
        //String類中的matches方法,即使用Pattern和Matcher類的matcher方法
        //只不過String方法封裝後, 使用較簡單, 但功能一致

        //將規則作用到字串上, 並進行符合規則的子串查找
        while (m.find()) {
            //System.out.println(m.group() + " ");  //用於獲取匹配後結果
            Result =m.group();
        }
        //Log.d("WillyCheng",Result);
        return Result;

    }
}
