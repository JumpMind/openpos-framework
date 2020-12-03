package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

public class Toast extends Message {

    private static final long serialVersionUID = 1L;

    private String message;
    private ToastType toastType;
    private int duration = 2500;
    private String verticalPosition = "bottom";
    private String icon;

    public static Toast createSuccessToast(String message) {
        return createSuccessToast(message, true);
    }

    public static Toast createSuccessToast(String message, boolean willUnblock) {
        Toast toast = new Toast(message);
        toast.setWillUnblock(willUnblock);
        toast.setToastType(ToastType.Success);
        return toast;
    }

    public static Toast createWarningToast(String message) {
        Toast toast = new Toast(message);
        toast.setToastType(ToastType.Warn);
        toast.setDuration(0);
        return toast;
    }

    public Toast() {
        setType(MessageType.Toast);
        setWillUnblock(true);
    }

    public Toast(String message) {
        this();
        setMessage(message);
    }

    public Toast(String message, ToastType toastType, int duration) {
        this();
        this.message = message;
        this.toastType = toastType;
        this.duration = duration;
    }
    
    public Toast(String message, ToastType toastType, int duration, String verticalPosition) {
        this(message, toastType, duration);
        this.verticalPosition = verticalPosition;
    }

    public Toast(String message, ToastType toastType, int duration, String verticalPosition, String icon) {
        this(message, toastType, duration, verticalPosition);
        this.icon = icon;
    }

    public Toast(String message, ToastType toastType, int duration, String verticalPosition, boolean willUnblock){
        this(message, toastType, duration, verticalPosition);
        this.setWillUnblock(willUnblock);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ToastType getToastType() {
        return toastType;
    }

    public void setToastType(ToastType toastType) {
        this.toastType = toastType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public void setVerticalPosition(String verticalPosition) {
        this.verticalPosition = verticalPosition;
    }
    
    public String getVerticalPosition() {
        return verticalPosition;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
