package dev.pamparampam.myapplication.radiowezel.LGSnackbar.core;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.IBinder;


import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.pamparampam.myapplication.R;

import static dev.pamparampam.myapplication.radiowezel.LGSnackbar.LGSnackbarManager.NO_ICON_ID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * Created by LoreGr on 20/05/2017.
 */
class LGSnackbarPresenter {

    private final WindowManager windowManager;
    private final Context appplicationContext;

    private LGSnackbar lgSnackbar;

    public LGSnackbarPresenter(@NonNull LGSnackbar lgSnackbar) {
        this.lgSnackbar = lgSnackbar;
        this.appplicationContext = lgSnackbar.getContext();
        this.windowManager = (WindowManager) appplicationContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        WindowManager.LayoutParams layoutParams = createDefaultLayoutParams(WindowManager.LayoutParams.TYPE_TOAST, null);
        FrameLayout rootFrame = new FrameLayout(appplicationContext) {
            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                onRootViewAvailable(this);
            }

        };
        rootFrame.setContentDescription("LGSnackbar");
        windowManager.addView(rootFrame, layoutParams);
    }

    private void onRootViewAvailable(final FrameLayout rootView) {
        final CoordinatorLayout snackbarContainer = new CoordinatorLayout(new ContextThemeWrapper(appplicationContext, R.style.AppTheme)) {
            @Override
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                onSnackbarContainerAttached(rootView, this);
            }
        };
        windowManager.addView(snackbarContainer, createDefaultLayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL, rootView.getWindowToken()));
    }

    private void onSnackbarContainerAttached(final View rootView, final CoordinatorLayout snackbarContainer) {
        Snackbar snackbar = Snackbar.make(snackbarContainer, lgSnackbar.getText(), lgSnackbar.getDuration());
        View snackbarLayout = snackbar.getView();

        TextView textView = (TextView)snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarLayout.setBackgroundColor(lgSnackbar.getBackgroundColor());
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setMinimumHeight((int) lgSnackbar.getMinHeight());
        textView.setMaxLines(10);
        textView.setTextColor(lgSnackbar.getTextColor());
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        if (lgSnackbar.getIconID()!= NO_ICON_ID) {
            textView.setCompoundDrawablesWithIntrinsicBounds(lgSnackbar.getIconID(), 0, 0, 0);
            textView.setCompoundDrawablePadding(appplicationContext.getResources().getDimensionPixelOffset(R.dimen.default_margin));
        }

        final Snackbar.Callback callback = lgSnackbar.getCallback();
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if (snackbarContainer.getParent() != null && rootView.getParent() != null) {
                        windowManager.removeView(snackbarContainer);
                        windowManager.removeView(rootView);
                    }
                    if (callback != null) {
                        callback.onDismissed(snackbar, event);
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {
                    super.onShown(snackbar);
                    if (callback != null) {
                        callback.onShown(snackbar);
                    }
                }
            });


        final LGSnackbarAction action = lgSnackbar.getAction();
        if (action != null) {
            snackbar.setActionTextColor(lgSnackbar.getActionTextColor());
            snackbar.setAction(action.getText(), action.getListener());
        }
        snackbar.show();
    }

    private WindowManager.LayoutParams createDefaultLayoutParams(int type, @Nullable IBinder windowToken) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = GravityCompat.getAbsoluteGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, ViewCompat.LAYOUT_DIRECTION_LTR);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.type = type;
        layoutParams.token = windowToken;
        return layoutParams;
    }
}