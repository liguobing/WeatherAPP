package cn.lixyz.weatherapp.util;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

/**
 * Created by LGB on 2016/4/10.
 */
public class AnimatorUtil {
    public static LayoutAnimationController getGridViewAnimator(float width, float height) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new TranslateAnimation(width / 3, 0, height / 3, 0);
        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set);
        return controller;

    }
}
