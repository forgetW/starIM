package com.kotlin.starim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.aspose.words.Document;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtils {

    public static Bitmap testPublicAPI2() throws Exception {
        String gTestDocumentPath = "/storage/emulated/0/Download/督查专报（2021年第3期） .docx";
//        String outFile = "/storage/emulated/0/Download/out.png";
        File file = new File("/storage/emulated/0/Download/out.png");
        Document doc = new Document(gTestDocumentPath);
        Bitmap image = null;
        FileOutputStream fos = null;
        try {
            image = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            Canvas gr = new Canvas(image);
            gr.rotate(45);
            doc.renderToSize(0, gr, 0, 0, image.getWidth(), image.getHeight());
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } finally {
            if (fos != null)
                fos.close();
            if (image != null)
                image.recycle();
        }

        return image;
    }

//    public static Bitmap initFile(String file) throws Exception{
////        //Shows how to the individual pages of a document to graphics to create one image with thumbnails of all pages.
////        Document doc = new Document(file);
////
////        // Calculate the number of rows and columns that we will fill with thumbnails.
////        final int thumbColumns = 2;
////        int thumbRows = doc.getPageCount() / thumbColumns;
////
////        int remainder = doc.getPageCount() % thumbColumns;
////        if (remainder > 0) thumbRows++;
////
////        // Scale the thumbnails relative to the size of the first page.
////        float scale = 1f;
////        PointF thumbSize = doc.getPageInfo(0).getSizeInPixels(scale, 96);
////        Bitmap img = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
////        Canvas gr = new Canvas(img);
////        Paint paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
////        paint.setColor(Color.YELLOW);
////        paint.setStrokeJoin(Paint.Join.ROUND);
////        paint.setStrokeCap(Paint.Cap.ROUND);
////        paint.setStrokeWidth(3);
////        for (int pageIndex = 0; pageIndex < doc.getPageCount(); pageIndex++) {
////            int rowIdx = pageIndex / thumbColumns;
////            int columnIdx = pageIndex % thumbColumns;
////
////            // Specify where we want the thumbnail to appear.
//////                float thumbLeft = (float) (columnIdx * thumbSize.getWidth());
//////                float thumbTop = (float) (rowIdx * thumbSize.getHeight());
////            PointF size = doc.renderToScale(pageIndex, gr, thumbSize.x, thumbSize.y, scale);
////
//////                gr.setColor(Color.black);
////
////            // Render a page as a thumbnail, and then frame it in a rectangle of the same size.
////            gr.drawRect(0, 0, size.x, size.y, paint);
////        }
////
////        return img;
//
////        Shows how to the individual pages of a document to graphics to create one image with thumbnails of all pages.
////        Document doc = new Document(getMyDir() + "Rendering.docx");
//
//// Calculate the number of rows and columns that we will fill with thumbnails.
//        final int thumbColumns = 2;
//        int thumbRows = doc.getPageCount() / thumbColumns;
//
//        int remainder = doc.getPageCount() % thumbColumns;
//        if (remainder > 0) thumbRows++;
//
//// Scale the thumbnails relative to the size of the first page.
//        float scale = 0.25f;
//        Dimension thumbSize = doc.getPageInfo(0).getSizeInPixels(scale, 96);
//
//// Calculate the size of the image that will contain all the thumbnails.
//        int imgWidth = (int) (thumbSize.getWidth() * thumbColumns);
//        int imgHeight = (int) (thumbSize.getHeight() * thumbRows);
//
//        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D gr = img.createGraphics();
//        try {
//            gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//            gr.setColor(Color.white);
//            // Fill the background, which is transparent by default, in white.
//            gr.fillRect(0, 0, imgWidth, imgHeight);
//
//            for (int pageIndex = 0; pageIndex < doc.getPageCount(); pageIndex++) {
//                int rowIdx = pageIndex / thumbColumns;
//                int columnIdx = pageIndex % thumbColumns;
//
//                // Specify where we want the thumbnail to appear.
//                float thumbLeft = (float) (columnIdx * thumbSize.getWidth());
//                float thumbTop = (float) (rowIdx * thumbSize.getHeight());
//
//                Point2D.Float size = doc.renderToScale(pageIndex, gr, thumbLeft, thumbTop, scale);
//
//                gr.setColor(Color.black);
//
//                // Render a page as a thumbnail, and then frame it in a rectangle of the same size.
//                gr.drawRect((int) thumbLeft, (int) thumbTop, (int) size.getX(), (int) size.getY());
//            }
//
//            ImageIO.write(img, "PNG", new File(getArtifactsDir() + "Rendering.Thumbnails.png"));
//        } finally {
//            if (gr != null) {
//                gr.dispose();
//            }
//        }
//    }

}
