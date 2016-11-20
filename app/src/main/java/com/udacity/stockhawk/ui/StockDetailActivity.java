package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockDetailActivity extends AppCompatActivity
      implements LoaderManager.LoaderCallbacks<Cursor> {

   private static final String BUNDLE_STOCK_SYMBOL = "stock.symbol";
   private String symbol;

   public static void start(Activity activity, String symbol) {
      Intent starter = new Intent(activity, StockDetailActivity.class);
      starter.putExtra(BUNDLE_STOCK_SYMBOL, symbol);
      activity.startActivity(starter);
   }

   @Override
   public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(this, Contract.Quote.uri, Contract.Quote.QUOTE_COLUMNS, null, null,
            Contract.Quote.COLUMN_SYMBOL);
   }

   @Override
   public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

      while (data.moveToNext()) {
         if (TextUtils.equals(data.getString(Contract.Quote.POSITION_SYMBOL), symbol)) {
            break;
         }
      }

      String stockHistory = data.getString(Contract.Quote.POSITION_HISTORY);
      initChart(symbol, createHistory(stockHistory));
   }

   @Override
   public void onLoaderReset(Loader<Cursor> loader) {
   }

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_stock_detail);

      symbol = getIntent().getExtras()
            .getString(BUNDLE_STOCK_SYMBOL, "");

      getSupportLoaderManager().initLoader(0, null, this);
   }

   private LongSparseArray<Float> createHistory(String stockHistory) {
      String[] data = stockHistory.split("\\n");
      LongSparseArray<Float> history = new LongSparseArray<>();
      for (String entry : data) {
         String[] tmp = entry.split(",");
         history.append(Long.valueOf(tmp[0]), Float.valueOf(tmp[1].trim()));
      }
      return history;
   }

   private void initChart(String symbol, LongSparseArray<Float> stockValues) {
      LineChart chart = (LineChart) findViewById(R.id.chart);
      List<Entry> yValues = new ArrayList<>();
      SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
      List<String> xValues = new ArrayList<>();
      for (int i = 0; i < stockValues.size(); i++) {
         yValues.add(new Entry(stockValues.valueAt(i), i));
         xValues.add(df.format(new Date(stockValues.keyAt(i))));
      }

      LineDataSet dataSet = new LineDataSet(yValues, symbol);
      dataSet.setColor(R.color.colorAccent);
      dataSet.setValueTextColor(R.color.colorPrimary);

      LineData lineData = new LineData(xValues, dataSet);
      chart.setData(lineData);
      chart.invalidate();
   }
}
