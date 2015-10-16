/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfview.
 *
 * Android-pdfview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joanzapata;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.joanzapata.pdfview.sample.R;
import com.joanzapata.pdfview.util.FileUtils;

import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

import static java.lang.String.format;

public class PDFViewActivity extends SherlockActivity implements OnPageChangeListener {

    public static final String SAMPLE_FILE = "sample.pdf";

    public static final String ABOUT_FILE = "sample.pdf";

    static final String PDF_FILE_PROVIDER_AUTHORITY = "com.joanzapata.PdfFileProvider";

    View contentView;
    PDFView pdfView;

    String pdfName = SAMPLE_FILE;

    Integer pageNumber = 1;

    void afterViews() {
        display(pdfName, false);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        contentView = getLayoutInflater().inflate(R.layout.activity_main, null, false);
        pdfView = (PDFView) contentView.findViewById(R.id.pdfView);

        setContentView(contentView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        display(ABOUT_FILE, true);
    }

    public void about() {
        if (!displaying(ABOUT_FILE))
            display(ABOUT_FILE, true);
    }

    private void display(String assetFileName, boolean jumpToFirstPage) {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);

        copyAssetFileToInternalCache(assetFileName);

        /*
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_SUBJECT, assetFileName);
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, PDF_FILE_PROVIDER_AUTHORITY, new File(assetFileName)));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            startActivity(intent);
        }
        */

        /*
        pdfView.fromAsset(assetFileName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
        */

        try {
            pdfView.fromUri(FileProvider.getUriForFile(getApplicationContext(), PDF_FILE_PROVIDER_AUTHORITY, FileUtils.fileFromAsset(getApplicationContext(), assetFileName)))
                    .defaultPage(pageNumber)
                    .onPageChange(this)
                    .load();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void copyAssetFileToInternalCache(String assetFileName) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(format("%s %s / %s", pdfName, page, pageCount));
    }

    @Override
    public void onBackPressed() {
        if (ABOUT_FILE.equals(pdfName)) {
            display(SAMPLE_FILE, true);
        } else {
            super.onBackPressed();
        }
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }
}
