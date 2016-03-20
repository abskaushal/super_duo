package it.jaschke.alexandria;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class BookDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString(BookDetail.EAN_KEY, getIntent().getStringExtra(BookDetail.EAN_KEY));
            args.putInt(BookDetail.POSITION, getIntent().getIntExtra(BookDetail.POSITION, 0));
            args.putBoolean(BookDetail.TWO_PANE, getIntent().getBooleanExtra(BookDetail.TWO_PANE, false));
            BookDetail fragment = new BookDetail();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
