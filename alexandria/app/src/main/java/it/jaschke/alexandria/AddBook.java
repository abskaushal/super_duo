package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.data.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.Utils;
import it.jaschke.alexandria.zxing.CaptureActivity;
import it.jaschke.alexandria.zxing.Intents;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    public static final int REQUEST_CODE = 100;
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private ProgressBar progressBar;
    private static final String BOOK_DETAIL = "book";
    private final String EAN_CONTENT = "eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    private Book book;


    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ean != null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
            outState.putParcelable(BOOK_DETAIL, book);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress);
        ean = (EditText) rootView.findViewById(R.id.ean);

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }
                searchBook(ean);
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
                clearFields();
            }
        });

        if (savedInstanceState != null) {
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            book = savedInstanceState.getParcelable(BOOK_DETAIL);
            setBookDetail();
        } else {
            book = new Book();
            book.setInit(0);
        }

        return rootView;
    }

    private void searchBook(String ean) {
        if (Utils.isInternetAvailable(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, ean);
            bookIntent.setAction(BookService.FETCH_BOOK);
            getActivity().startService(bookIntent);
            AddBook.this.restartLoader();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ean.getText().length() == 0) {
            return null;
        }
        String eanStr = ean.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        book.setInit(1);
        book.setTitle(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
        book.setSubTitle(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));
        book.setAuthors(data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR)));
        book.setImage(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL)));
        book.setCategory(data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY)));


        setBookDetail();

        progressBar.setVisibility(View.GONE);

    }

    private void setBookDetail() {
        if (book.getInit() == 1) {
            ((TextView) rootView.findViewById(R.id.bookTitle)).setText(book.getTitle());
            ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(book.getSubTitle());
            String authors = book.getAuthors();
            if (authors!=null && authors.length() > 0) {
                String[] authorsArr = authors.split(",");
                ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
                ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
            }
            String imgUrl = book.getImage();
            ImageView imageView = (ImageView) rootView.findViewById(R.id.cover);
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                Picasso.with(getActivity()).load(imgUrl).placeholder(R.drawable.default_book).error(R.drawable.default_book).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.default_book);
            }

            ((TextView) rootView.findViewById(R.id.categories)).setText(book.getCategory());

            rootView.findViewById(R.id.book_detail).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields() {
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.book_detail).setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String isbn = data.getStringExtra(Intents.Scan.RESULT);
                ean.setText("");
                ean.setText(isbn);
                searchBook(isbn);
            }
        }
    }
}
