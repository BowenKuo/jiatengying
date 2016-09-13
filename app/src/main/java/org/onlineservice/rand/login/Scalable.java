package org.onlineservice.rand.login;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Rand on 2016/9/11. Awesome!
 */
public interface Scalable {
    @Deprecated
    void scale();
    void scale(final int height, final int width,float textSize);
    void scale(@NonNull final ViewGroup.LayoutParams params, float textSize);
    void scale(@NonNull final FrameLayout frameLayout, float textSize);
}
