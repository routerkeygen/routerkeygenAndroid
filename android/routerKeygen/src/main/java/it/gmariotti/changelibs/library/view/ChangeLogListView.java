/**
 * ****************************************************************************
 * Copyright (c) 2013 Gabriele Mariotti.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package it.gmariotti.changelibs.library.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.exobel.routerkeygen.R;

import it.gmariotti.changelibs.library.Constants;
import it.gmariotti.changelibs.library.internal.ChangeLog;
import it.gmariotti.changelibs.library.internal.ChangeLogAdapter;
import it.gmariotti.changelibs.library.internal.ChangeLogRow;
import it.gmariotti.changelibs.library.parser.XmlParser;

/**
 * ListView for ChangeLog
 *
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class ChangeLogListView extends ListView implements
        AdapterView.OnItemClickListener {

    // --------------------------------------------------------------------------
    private static final String TAG = "ChangeLogListView";
    // --------------------------------------------------------------------------
    // Custom Attrs
    // --------------------------------------------------------------------------
    private int mRowLayoutId = Constants.mRowLayoutId;
    private int mRowHeaderLayoutId = Constants.mRowHeaderLayoutId;
    private int mChangeLogFileResourceId = Constants.mChangeLogFileResourceId;
    // Adapter
    private ChangeLogAdapter mAdapter;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public ChangeLogListView(Context context) {
        super(context);
        init(null, 0);
    }

    public ChangeLogListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChangeLogListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    // --------------------------------------------------------------------------
    // Init
    // --------------------------------------------------------------------------

    /**
     * Initialize
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        // Init attrs
        initAttrs(attrs, defStyle);
        // Init adapter
        initAdapter();

        // Set divider to 0dp
        setDividerHeight(0);
    }

    /**
     * Init custom attrs.
     *
     * @param attrs
     * @param defStyle
     */
    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.ChangeLogListView, defStyle, defStyle);

        try {
            // Layout for rows and header
            mRowLayoutId = a.getResourceId(
                    R.styleable.ChangeLogListView_rowLayoutId, mRowLayoutId);
            mRowHeaderLayoutId = a.getResourceId(
                    R.styleable.ChangeLogListView_rowHeaderLayoutId,
                    mRowHeaderLayoutId);

            // Changelog.xml file
            mChangeLogFileResourceId = a.getResourceId(
                    R.styleable.ChangeLogListView_changeLogFileResourceId,
                    mChangeLogFileResourceId);

            // String which is used in header row for Version
            // mStringVersionHeader=
            // a.getResourceId(R.styleable.ChangeLogListView_StringVersionHeader,mStringVersionHeader);

        } finally {
            a.recycle();
        }
    }

    /**
     * Init adapter
     */
    private void initAdapter() {

        try {
            // Read and parse changelog.xml
            XmlParser parse = new XmlParser(getContext(),
                    mChangeLogFileResourceId);
            // ChangeLog chg=parse.readChangeLogFile();
            ChangeLog chg = new ChangeLog();
            // Create adapter and set custom attrs
            mAdapter = new ChangeLogAdapter(getContext(), chg.getRows());
            mAdapter.setmRowLayoutId(mRowLayoutId);
            mAdapter.setmRowHeaderLayoutId(mRowHeaderLayoutId);

            // Parse in a separate Thread to avoid UI block with large files
            new ParseAsyncTask(mAdapter, parse).execute();
            setAdapter(mAdapter);
        } catch (Exception e) {
            Log.e(TAG,
                    getResources().getString(
                            R.string.changelog_internal_error_parsing), e);
        }

    }

    /**
     * Sets the list's adapter, enforces the use of only a ChangeLogAdapter
     */
    private void setAdapter(ChangeLogAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO
    }

    /**
     * Async Task to parse xml file in a separate thread
     */
    class ParseAsyncTask extends AsyncTask<Void, Void, ChangeLog> {

        private final ChangeLogAdapter mAdapter;
        private final XmlParser mParse;
        private final String errorString;

        public ParseAsyncTask(ChangeLogAdapter adapter, XmlParser parse) {
            mAdapter = adapter;
            mParse = parse;
            errorString =  getResources().getString(R.string.changelog_internal_error_parsing);
        }

        @Override
        protected ChangeLog doInBackground(Void... params) {

            try {
                if (mParse != null) {
                    return mParse.readChangeLogFile();
                }
            } catch (Exception e) {
                Log.e(TAG,errorString, e);
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected void onPostExecute(ChangeLog chg) {

            // Notify data changed
            if (chg != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    mAdapter.addAll(chg.getRows());
                } else {
                    for (ChangeLogRow c : chg.getRows())
                        mAdapter.add(c);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
