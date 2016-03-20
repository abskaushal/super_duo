package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SimpleTimeZone;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by Abhishek on 19-Mar-16.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = WidgetDataProvider.class.getSimpleName();

    private List<ScoreEntity> mList = new ArrayList<>();
    private Context mContext;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        fetchScore();
    }

    @Override
    public void onDataSetChanged() {
        fetchScore();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = null;
        if (mList.get(i).getHome().equals(mContext.getString(R.string.no_match))) {
            views = new RemoteViews(mContext.getPackageName(), R.layout.widget_no_internet);
            if (!Utilies.isInternetAvailable(mContext)) {
                views.setTextViewText(R.id.text, mContext.getString(R.string.no_internet));
            }
        } else {
            views = new RemoteViews(mContext.getPackageName(), R.layout.widget_score_row);
            ScoreEntity entity = mList.get(i);
            views.setTextViewText(R.id.home, entity.getHome());
            views.setTextViewText(R.id.away, entity.getAway());
            views.setTextViewText(R.id.time, entity.getDate());
            views.setTextViewText(R.id.goals,Utilies.getScores(entity.getHomeGoal(),entity.getAwayGoal()));
            views.setImageViewResource(R.id.home_image,Utilies.getTeamCrestByTeamName(entity.getHome()));
            views.setImageViewResource(R.id.away_image,Utilies.getTeamCrestByTeamName(entity.getAway()));
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void fetchScore() {
        mList.clear();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(calendar.getTime());
        String arr[] = new String[]{date};

        Cursor cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), null, DatabaseContract.scores_table.DATE_COL, arr, null);
        ScoreEntity entity;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                entity = new ScoreEntity();
                entity.setHome(cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
                entity.setAway(cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));
                entity.setDate(cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL)));
                entity.setHomeGoal(cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL)));
                entity.setAwayGoal(cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL)));
                mList.add(entity);
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            entity = new ScoreEntity();
            entity.setHome(mContext.getString(R.string.no_match));
            mList.add(entity);
        }

    }
}
