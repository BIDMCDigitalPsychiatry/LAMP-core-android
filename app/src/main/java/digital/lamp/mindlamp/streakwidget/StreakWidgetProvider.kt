package digital.lamp.mindlamp.streakwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import digital.lamp.mindlamp.HomeActivity
import digital.lamp.mindlamp.R

class StreakWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_streak)

            // Retrieve the current streak from SharedPreferences or another storage
            val sharedPreferences = context.getSharedPreferences("UserStreak", Context.MODE_PRIVATE)
            val currentStreak = sharedPreferences.getInt("current_streak", 0)
            val longestStreak = sharedPreferences.getInt("longest_streak", 0)

            views.setTextViewText(R.id.streak_text, "Current streak: $currentStreak days")
            views.setTextViewText(R.id.longestStreakText, "Longest streak: $longestStreak days")

            // Set up an intent to open the app when the widget is clicked
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.streak_text, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
