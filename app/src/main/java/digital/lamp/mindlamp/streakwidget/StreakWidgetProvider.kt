package digital.lamp.mindlamp.streakwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import digital.lamp.mindlamp.HomeActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState

class StreakWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_streak)

            views.setTextViewText(R.id.streak_text, "Current streak: ${AppState.session.currentStreakDays} days")
            views.setTextViewText(R.id.longestStreakText, "Longest streak: ${AppState.session.longestStreakDays} days")

            // Set up an intent to open the app when the widget is clicked
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.parentLayout, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
