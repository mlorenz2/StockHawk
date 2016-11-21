package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetailActivity;

public class StockHawkAppWidgetProvider extends AppWidgetProvider {

   @Override
   public void onReceive(@NonNull Context context, @NonNull Intent intent) {
      super.onReceive(context, intent);
      if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
         AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
         int[] appWidgetIds =
               appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
         appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
      }
   }

   @Override
   public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
      for (int appWidgetId : appWidgetIds) {
         Intent intent = new Intent(context, MainActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

         RemoteViews views =
               new RemoteViews(context.getPackageName(), R.layout.stock_hawk_appwidget);
         views.setOnClickPendingIntent(R.id.widget, pendingIntent);
         views.setEmptyView(R.id.widget_list, R.id.widget_empty);

         views.setRemoteAdapter(R.id.widget_list,
               new Intent(context, StockHawkWidgetRemoteViewsService.class));

         Intent clickIntentTemplate = new Intent(context, StockDetailActivity.class);

         PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
               .addNextIntentWithParentStack(clickIntentTemplate)
               .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
         views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

         appWidgetManager.updateAppWidget(appWidgetId, views);
      }
   }
}
