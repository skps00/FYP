package com.calendar.fyp;

public class DialogUtils {
    private static boolean isDialogShowing = false;

    public static boolean isDialogShowing() {
        return isDialogShowing;
    }

    public static void setDialogShowing(boolean showing) {
        isDialogShowing = showing;
    }
}
