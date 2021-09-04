package com.kotlin.starim

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aspose.words.Document
import com.aspose.words.SaveFormat
import com.github.barteksc.pdfviewer.PDFView
import com.kotlin.starim.DownloadUtil.OnDownloadListener
import java.io.File


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)

//        val fileUrl = "http://219.152.174.78:8080/uie-webapp/common/download.action;jsessionid=52604359E9136B63CD02DF018CAECF21?sid=590689"
//        val path = "${DownloadUtil.absolutePath}/Download";
        findViewById<TextView>(R.id.text).setOnClickListener {

            val testPublicAPI2 = FileUtils.testPublicAPI2()
            imageView.setImageBitmap(testPublicAPI2)
//            DownloadUtil.get().download(fileUrl,  "/Download", object : OnDownloadListener {
//                override fun onDownloadSuccess(file: File) {
//                    Log.e(TAG, "onDownloadSuccess: ${file.absolutePath}" )
//
////                    val initFile = FileUtils.initFile(file.absolutePath)
////                    val decodeFile = BitmapFactory.decodeFile(file.absolutePath)
//
//                    val testPublicAPI2 = FileUtils.testPublicAPI2()
//                    imageView.setImageBitmap(testPublicAPI2)
////                    try {
////                        val document = Document(file.absolutePath)
//////                        document.save("$path/7865.docx", SaveFormat.DOCX)
////                        document.save("$path/test.pdf", SaveFormat.PDF)
////                        val pdfView = findViewById<PDFView>(R.id.pdfView)
////
//////                        pdfView.fromFile(File(filePath))
//////                        document.save("$path/7867.html", SaveFormat.HTML)
////                    }catch (e : Exception){
////                        e.printStackTrace()
////                    }
//                }
//                override fun onDownloading(progress: Int) {
//                    Log.e(TAG, "onDownloading: $progress" )
//                }
//                override fun onDownloadFailed(e: Exception) {
//                    Log.e(TAG, "onDownloadFailed: ${e.message}" )
//
//                }
//            })
        }
    }
}