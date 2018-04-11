package itl.angelo.smartcistern.util.ui;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


/**
 * Created by Angelo on 23/12/2016.
 */

public class UserInteraction {
    public static final int SAVING = 1;
    //public static final int GAS_ALARM = 2;
    private Context mContext;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Notification notification;

    /**
     * @param context the context of activity
     */
    public UserInteraction(Context context) {
        this.mContext = context;
        setNotificationManager((NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE));
    }

    /**
     * @param contentTitle the title of the notification
     * @param contentText  the text of the notification
     */
    public Notification createNotification(
            int contentTitle,
            String contentText,
            int icon,
            Class<?> resultIntentClass,
            Class<?> parentStackClass,
            int flag) {
        Intent resultIntent = new Intent(mContext, resultIntentClass);//create intent to open with not
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);//create a stack builder to contain intent
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(parentStackClass);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //build the not
        builder = new Notification.Builder(mContext);
        builder
                .setSmallIcon(icon)
                .setAutoCancel(false)
                .setContentTitle(mContext.getString(contentTitle))
                .setContentText(contentText)
                .setContentIntent(resultPendingIntent).setVibrate(new long[]{
                120,100,90,120,40,12
        });
        notification = builder.build();//assing the build to not
        notification.flags = flag;//indicates that is not a closable not
        return notification;
    }

    /**
     * @param resText the resource of text to show
     */
    public void showSnackbar(int resText) {
        Snackbar.make(
                ((AppCompatActivity) mContext).findViewById(android.R.id.content),
                mContext.getString(resText),
                Snackbar.LENGTH_LONG
        ).show();
    }

    /**
     * @param resText the resource of text to show
     */
    public void showToast(int resText) {
        Toast.makeText(
                mContext,
                resText,
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * @param resTitle             title resource
     * @param resMessage           message resource
     * @param resAcceptButton      resource text accept button
     * @param resDeclineButton     resource text decline button
     * @param acceptClickListener  click listener to accept button
     * @param declineClickListener click listener to decline button
     */
    public void showAlertDialog(boolean haveNegativeButton,
                                int resTitle,
                                int resMessage,
                                int resAcceptButton,
                                int resDeclineButton,
                                AlertDialog.OnClickListener acceptClickListener,
                                AlertDialog.OnClickListener declineClickListener,
                                boolean cancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog
                .setTitle(resTitle)
                .setMessage(resMessage)
                .setPositiveButton(resAcceptButton, acceptClickListener);
        if (haveNegativeButton) dialog.setNegativeButton(resDeclineButton, declineClickListener);
        dialog.setCancelable(cancelable);
        dialog.show();
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}