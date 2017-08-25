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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.ocrreader.R;

import org.json.JSONObject;

import java.util.Locale;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    String mtext;
    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    TextToSpeech tts;
    String SRESULT,SMESSAGE,OPDATE,OPDSEQ,OPDSECTION,SECTIONNMC,OPDDRNMC,OPDROOM,OPDTIMEFLAG,ESTIDATETIME,OPDROOMPLACE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("TTS", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("TTS", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);

        statusMessage = (TextView) findViewById(R.id.status_message);
        textValue = (TextView) findViewById(R.id.text_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        //useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_text).setOnClickListener(this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.re);
        relativeLayout.setBackgroundResource(R.drawable.pattern1);

        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
        intent.putExtra(OcrCaptureActivity.UseFlash, false);

        startActivityForResult(intent, RC_OCR_CAPTURE);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    final String text = data.getStringExtra("ID");
                    final String ID = text.split("\n")[0];
                    final String Bir = text.split("\n")[1];
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setTitle("請問您的身分證字號是\n" + ID + "\n生日是" + Bir + " 嗎?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
//下面這行的網址，裡面的IDNO及BIRTHDT 目前為測試用的，之後須改為前面所得到之ID及Bir
                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://regws.vghtc.gov.tw/REGMOBILEAPP/services/GetReservation.ashx?IDNO=Q122873449&BIRTHDT=19800731", null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try
                                                    {
                                                        SRESULT=response.get("SRESULT").toString();
                                                        OPDATE=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDDATE").toString();
                                                        OPDSEQ=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDSEQ").toString();
                                                        OPDSECTION=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDSECTION").toString();
                                                        SECTIONNMC=response.getJSONArray("RECORDS").getJSONObject(0).get("SECTIONNMC").toString();
                                                        OPDDRNMC=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDDRNMC").toString();
                                                        OPDROOM=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDROOM").toString();
                                                        OPDTIMEFLAG=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDTIMEFLAG").toString();
                                                        ESTIDATETIME=response.getJSONArray("RECORDS").getJSONObject(0).get("ESTIDATETIME").toString();
                                                        OPDROOMPLACE=response.getJSONArray("RECORDS").getJSONObject(0).get("OPDROOMPLACE").toString();
//                                                        Log.d("MYTAG","SRESULT:"+SRESULT );
//                                                        Log.d("MYTAG", "RECORDS.OPDDATE:"+OPDATE);
                                                    }
                                                    catch (Exception e)
                                                    {

                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("MYTAG", error.getMessage(), error);
                                        }
                                    });

                                    mQueue.add(jsonObjectRequest);

                                    statusMessage.setText(R.string.ocr_success);
                                    mtext = text;
                                    textValue.setText("身分證字號:" + ID +
                                            "\n生日:" + Bir+
                                            "\n日期:"+OPDATE+
                                            "\nOPDSEQ:"+OPDSEQ+
                                            "\n科別:"+SECTIONNMC+
                                            "\n診次:"+OPDROOM+
                                            "\n位置:"+OPDROOMPLACE
                                    );

                                    textValue.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            tts.speak(mtext, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                        }
                                    });
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            textValue.setText("");
                            Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
                            intent.putExtra(OcrCaptureActivity.UseFlash, false);
                            startActivityForResult(intent, RC_OCR_CAPTURE);
                        }
                    })
                    ;

                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();


                    //Log.d(TAG, "Text read: " + text);
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    //Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
