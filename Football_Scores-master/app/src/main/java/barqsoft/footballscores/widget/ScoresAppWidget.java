package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FetchScoreService;

/**
 * Implementation of App Widget functionality.
 */
public class ScoresAppWidget extends AppWidgetProvider {

    private static int ID;
    private static final int[] SAMEID = new int[1];

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ID = appWidgetIds[0];
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.scores_app_widget);

        views.setRemoteAdapter(R.id.widget_list, new Intent(context, WidgetService.class));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout_main, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }

        ComponentName componentName = new ComponentName(context, ScoresAppWidget.class);
        appWidgetManager.updateAppWidget(componentName, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,new Intent(context,WidgetService.class));
    }

    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, final RemoteViews views) {
        views.setRemoteAdapter(0,R.id.widget_list,new Intent(context,WidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(FetchScoreService.ACTION_SCORE_UPDATE.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            for(int i=0; i<1; i++){
                SAMEID[i] = ID;
                onUpdate(context,appWidgetManager,SAMEID);
            }
        }
    }
}

