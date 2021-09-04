package com.kotlin.starim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        Intent intent = getIntent();
        if (intent.hasExtra("filePath")) {
            String filePath = intent.getStringExtra("filePath");

            PDFView pdfView = findViewById(R.id.pdfView);

            pdfView.fromFile(new File(filePath));
        }



    }
}