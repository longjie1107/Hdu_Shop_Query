package com.baidu.tts.sample;


import com.baidu.aip.asrwakeup3.core.recog.RecogResult;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.tts.sample.control.MySyntherizer;

import android.os.Handler;

public class MyTtsRecogListener extends MessageStatusRecogListener {
    private MySyntherizer mySyntherizer;
    public  MyTtsRecogListener(Handler handler,MySyntherizer mySyntherizer){

        super(handler);
        this.mySyntherizer=mySyntherizer;

    }
    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult){
        super.onAsrFinalResult(results,recogResult);
        mySyntherizer.speak(results[0]);
    }
}
