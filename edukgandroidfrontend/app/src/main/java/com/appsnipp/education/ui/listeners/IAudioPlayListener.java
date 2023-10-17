package com.appsnipp.education.ui.listeners;

/***
 * This is the audio player interface used for the chat activity, mainly for audio playing event.
 * @author Shuning Zhang
 * @version 1.0
 */

import android.net.Uri;

public interface IAudioPlayListener {
    void onStart(Uri var1);

    void onStop(Uri var1);

    void onComplete(Uri var1);
}
