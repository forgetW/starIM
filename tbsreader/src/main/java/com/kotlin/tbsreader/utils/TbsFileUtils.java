package com.kotlin.tbsreader.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TbsFileUtils {

    public static String getFileType4Name(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return null;
    }

    /**
     * 判断文件的编码格式
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(String fileName) throws Exception{
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }

    /**
     * 从assets目录中复制整个文件夹内容到新的路径下
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：Data(assets文件夹下文件夹名称)
     * @param newPath String  复制后路径  如：data/data/（手机内部存储路径名称）
     */
    public static boolean copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            boolean copy;
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                Log.e("TbsReader", "copyFilesFromAssets: oldPath---" + oldPath);
                Log.e("TbsReader", "copyFilesFromAssets: newPath---" + newPath);
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
            ////如果捕捉到错误则通知UI线程
            //MainActivity.handler.sendEmptyMessage(COPY_FALSE);
        }
    }

    /**
     * 复制assets文件夹下的文件夹到apk安装后的files文件夹中
     *
     * @param context
     * @param folder  要复制的assets文件夹下的文件夹或文件的名字，如assets文件夹下有个文件夹是Data，则folder的值为Data
     */
    public static boolean copyFileFromAssets(Context context, String folder, String newPath) {
        String filesDir = context.getFilesDir().getPath();
        filesDir = filesDir + "/assets/" + folder;
        return copyFilesFromAssets(context, folder, newPath);
    }

    public static String tbsFilePath(){
        return  ExceptionHandler.fileDownloadPath + "/Download/";
    }

    /**
     * 把asset的文件转化为本地文件
     *
     * @param context 上下文对象
     * @param oldPath 旧的文件路径
     * @param newPath 新的文件路径
     */
    public static boolean copyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void copyfile(Context context, String filepath, String fileName, String assetsName) {
        try {
            if (!new File(filepath + "/" + fileName).exists()) {
                InputStream is = context.getResources().getAssets().open(assetsName);
                FileOutputStream fos = new FileOutputStream(filepath + "/" + fileName);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SdCard是否存在
     *
     * @return true存在
     */
    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 创建用户文档目录文件
     *
     * @return 下载文件
     */
    public static File createDocumentsFile(Context context) {
        File file;
        if (isSdCardAvailable()) {
            String absolutePath = context.getApplicationContext().getFilesDir().getAbsolutePath().replace("files", "app_tbs");
//            /data/user/0/com.wit.cqgzy/files
            file = new File(absolutePath);

//            file = context.getExternalFilesDir(absolutePath);
//            file = context.getExternalFilesDir(
//                    context.getApplicationContext().getFilesDir().getAbsolutePath()
//                            + "/" + context.getPackageName() + "/app_tbs/core_share");
        } else {
            file = context.getFilesDir();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static boolean normalFile(String fileName) {
        boolean normal = false;
        for (int i = 0; i < CONTENT_TYPE.length; i++) {
            if (fileName.contains(CONTENT_TYPE[i][0]) || CONTENT_TYPE[i][0].contains(fileName))
                normal = true;
        }

        return normal;
    }

    public static String getFileType(String contentType) {
        String fileType = contentType;
        for (int i = 0; i < CONTENT_TYPE.length; i++) {
            if (contentType.equals(CONTENT_TYPE[i][1]))
                fileType = CONTENT_TYPE[i][0];
        }
        return fileType;
    }

    public static String getFileAction(String fileType) {
        String fileAction = fileType;
        for (int i = 0; i < CONTENT_TYPE.length; i++) {
            if (fileType.equals(CONTENT_TYPE[i][0]))
                fileAction = CONTENT_TYPE[i][1];
        }
        return fileAction;
    }

    //建立一个MIME类型与文件后缀名的匹配表
    private static final String[][] CONTENT_TYPE = {
            {".load", "text/html"},
            {".123", "application/vnd.lotus-1-2-3"},
            {".3ds", "p_w_picpath/x-3ds"},
            {".3g2", "video/3gpp"},
            {".3ga", "video/3gpp"},
            {".3gp", "video/3gpp"},
            {".3gpp", "video/3gpp"},
            {".602", "application/x-t602"},
            {".669", "audio/x-mod"},
            {".7z", "application/x-7z-compressed"},
            {".a", "application/x-archive"},
            {".aac", "audio/mp4"},
            {".abw", "application/x-abiword"},
            {".abw.crashed", "application/x-abiword"},
            {".abw.gz", "application/x-abiword"},
            {".ac3", "audio/ac3"},
            {".ace", "application/x-ace"},
            {".adb", "text/x-adasrc"},
            {".ads", "text/x-adasrc"},
            {".afm", "application/x-font-afm"},
            {".ag", "p_w_picpath/x-applix-graphics"},
            {".ai", "application/illustrator"},
            {".aif", "audio/x-aiff"},
            {".aifc", "audio/x-aiff"},
            {".aiff", "audio/x-aiff"},
            {".al", "application/x-perl"},
            {".alz", "application/x-alz"},
            {".amr", "audio/amr"},
            {".ani", "application/x-navi-animation"},
            {".anim[1-9j]", "video/x-anim"},
            {".anx", "application/annodex"},
            {".ape", "audio/x-ape"},
            {".arj", "application/x-arj"},
            {".arw", "p_w_picpath/x-sony-arw"},
            {".as", "application/x-applix-spreadsheet"},
            {".asf", "video/x-ms-asf"},
            {".asp", "application/x-asp"},
            {".ass", "text/x-ssa"},
            {".asx", "audio/x-ms-asx"},
            {".atom", "application/atom+xml"},
            {".au", "audio/basic"},
            {".avi", "video/x-msvideo"},
            {".aw", "application/x-applix-word"},
            {".awb", "audio/amr-wb"},
            {".awk", "application/x-awk"},
            {".axa", "audio/annodex"},
            {".axv", "video/annodex"},
            {".bak", "application/x-trash"},
            {".bcpio", "application/x-bcpio"},
            {".bdf", "application/x-font-bdf"},
            {".bib", "text/x-bibtex"},
            {".bin", "application/octet-stream"},
            {".blend", "application/x-blender"},
            {".blender", "application/x-blender"},
            {".bmp", "p_w_picpath/bmp"},
            {".bz", "application/x-bzip"},
            {".bz2", "application/x-bzip"},
            {".c", "text/x-csrc"},
            {".c++", "text/x-c++src"},
            {".cab", "application/vnd.ms-cab-compressed"},
            {".cb7", "application/x-cb7"},
            {".cbr", "application/x-cbr"},
            {".cbt", "application/x-cbt"},
            {".cbz", "application/x-cbz"},
            {".cc", "text/x-c++src"},
            {".cdf", "application/x-netcdf"},
            {".cdr", "application/vnd.corel-draw"},
            {".cer", "application/x-x509-ca-cert"},
            {".cert", "application/x-x509-ca-cert"},
            {".cgm", "p_w_picpath/cgm"},
            {".chm", "application/x-chm"},
            {".chrt", "application/x-kchart"},
            {".class", "application/x-java"},
            {".cls", "text/x-tex"},
            {".cmake", "text/x-cmake"},
            {".cpio", "application/x-cpio"},
            {".cpio.gz", "application/x-cpio-compressed"},
            {".cpp", "text/x-c++src"},
            {".cr2", "p_w_picpath/x-canon-cr2"},
            {".crt", "application/x-x509-ca-cert"},
            {".crw", "p_w_picpath/x-canon-crw"},
            {".cs", "text/x-csharp"},
            {".csh", "application/x-csh"},
            {".css", "text/css"},
            {".cssl", "text/css"},
            {".csv", "text/csv"},
            {".cue", "application/x-cue"},
            {".cur", "p_w_picpath/x-win-bitmap"},
            {".cxx", "text/x-c++src"},
            {".d", "text/x-dsrc"},
            {".dar", "application/x-dar"},
            {".dbf", "application/x-dbf"},
            {".dc", "application/x-dc-rom"},
            {".dcl", "text/x-dcl"},
            {".dcm", "application/dicom"},
            {".dcr", "p_w_picpath/x-kodak-dcr"},
            {".dds", "p_w_picpath/x-dds"},
            {".deb", "application/x-deb"},
            {".der", "application/x-x509-ca-cert"},
            {".desktop", "application/x-desktop"},
            {".dia", "application/x-dia-diagram"},
            {".diff", "text/x-patch"},
            {".divx", "video/x-msvideo"},
            {".djv", "p_w_picpath/vnd.djvu"},
            {".djvu", "p_w_picpath/vnd.djvu"},
            {".dng", "p_w_picpath/x-adobe-dng"},
            {".doc", "application/msword"},
            {".docbook", "application/docbook+xml"},
            {".docm", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".dot", "text/vnd.graphviz"},
            {".dsl", "text/x-dsl"},
            {".dtd", "application/xml-dtd"},
            {".dtx", "text/x-tex"},
            {".dv", "video/dv"},
            {".dvi", "application/x-dvi"},
            {".dvi.bz2", "application/x-bzdvi"},
            {".dvi.gz", "application/x-gzdvi"},
            {".dwg", "p_w_picpath/vnd.dwg"},
            {".dxf", "p_w_picpath/vnd.dxf"},
            {".e", "text/x-eiffel"},
            {".egon", "application/x-egon"},
            {".eif", "text/x-eiffel"},
            {".el", "text/x-emacs-lisp"},
            {".emf", "p_w_picpath/x-emf"},
            {".emp", "application/vnd.emusic-emusic_package"},
            {".ent", "application/xml-external-parsed-entity"},
            {".eps", "p_w_picpath/x-eps"},
            {".eps.bz2", "p_w_picpath/x-bzeps"},
            {".eps.gz", "p_w_picpath/x-gzeps"},
            {".epsf", "p_w_picpath/x-eps"},
            {".epsf.bz2", "p_w_picpath/x-bzeps"},
            {".epsf.gz", "p_w_picpath/x-gzeps"},
            {".epsi", "p_w_picpath/x-eps"},
            {".epsi.bz2", "p_w_picpath/x-bzeps"},
            {".epsi.gz", "p_w_picpath/x-gzeps"},
            {".epub", "application/epub+zip"},
            {".erl", "text/x-erlang"},
            {".es", "application/ecmascript"},
            {".etheme", "application/x-e-theme"},
            {".etx", "text/x-setext"},
            {".exe", "application/x-ms-dos-executable"},
            {".exr", "p_w_picpath/x-exr"},
            {".ez", "application/andrew-inset"},
            {".f", "text/x-fortran"},
            {".f90", "text/x-fortran"},
            {".f95", "text/x-fortran"},
            {".fb2", "application/x-fictionbook+xml"},
            {".fig", "p_w_picpath/x-xfig"},
            {".fits", "p_w_picpath/fits"},
            {".fl", "application/x-fluid"},
            {".flac", "audio/x-flac"},
            {".flc", "video/x-flic"},
            {".fli", "video/x-flic"},
            {".flv", "video/x-flv"},
            {".flw", "application/x-kivio"},
            {".fo", "text/x-xslfo"},
            {".for", "text/x-fortran"},
            {".g3", "p_w_picpath/fax-g3"},
            {".gb", "application/x-gameboy-rom"},
            {".gba", "application/x-gba-rom"},
            {".gcrd", "text/directory"},
            {".ged", "application/x-gedcom"},
            {".gedcom", "application/x-gedcom"},
            {".gen", "application/x-genesis-rom"},
            {".gf", "application/x-tex-gf"},
            {".gg", "application/x-sms-rom"},
            {".gif", "p_w_picpath/gif"},
            {".glade", "application/x-glade"},
            {".gmo", "application/x-gettext-translation"},
            {".gnc", "application/x-gnucash"},
            {".gnd", "application/gnunet-directory"},
            {".gnucash", "application/x-gnucash"},
            {".gnumeric", "application/x-gnumeric"},
            {".gnuplot", "application/x-gnuplot"},
            {".gp", "application/x-gnuplot"},
            {".gpg", "application/pgp-encrypted"},
            {".gplt", "application/x-gnuplot"},
            {".gra", "application/x-graphite"},
            {".gsf", "application/x-font-type1"},
            {".gsm", "audio/x-gsm"},
            {".gtar", "application/x-tar"},
            {".gv", "text/vnd.graphviz"},
            {".gvp", "text/x-google-video-pointer"},
            {".gz", "application/x-gzip"},
            {".h", "text/x-chdr"},
            {".h++", "text/x-c++hdr"},
            {".hdf", "application/x-hdf"},
            {".hh", "text/x-c++hdr"},
            {".hp", "text/x-c++hdr"},
            {".hpgl", "application/vnd.hp-hpgl"},
            {".hpp", "text/x-c++hdr"},
            {".hs", "text/x-haskell"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".hwp", "application/x-hwp"},
            {".hwt", "application/x-hwt"},
            {".hxx", "text/x-c++hdr"},
            {".ica", "application/x-ica"},
            {".icb", "p_w_picpath/x-tga"},
            {".icns", "p_w_picpath/x-icns"},
            {".ico", "p_w_picpath/vnd.microsoft.icon"},
            {".ics", "text/calendar"},
            {".idl", "text/x-idl"},
            {".ief", "p_w_picpath/ief"},
            {".iff", "p_w_picpath/x-iff"},
            {".ilbm", "p_w_picpath/x-ilbm"},
            {".ime", "text/x-imelody"},
            {".imy", "text/x-imelody"},
            {".ins", "text/x-tex"},
            {".iptables", "text/x-iptables"},
            {".iso", "application/x-cd-p_w_picpath"},
            {".iso9660", "application/x-cd-p_w_picpath"},
            {".it", "audio/x-it"},
            {".j2k", "p_w_picpath/jp2"},
            {".jad", "text/vnd.sun.j2me.app-descriptor"},
            {".jar", "application/x-java-archive"},
            {".java", "text/x-java"},
            {".jng", "p_w_picpath/x-jng"},
            {".jnlp", "application/x-java-jnlp-file"},
            {".jp2", "p_w_picpath/jp2"},
            {".jpc", "p_w_picpath/jp2"},
            {".jpe", "p_w_picpath/jpeg"},
            {".jpeg", "p_w_picpath/jpeg"},
            {".jpf", "p_w_picpath/jp2"},
            {".jpg", "p_w_picpath/jpeg"},
            {".jpr", "application/x-jbuilder-project"},
            {".jpx", "p_w_picpath/jp2"},
            {".js", "application/javascript"},
            {".json", "application/json"},
            {".jsonp", "application/jsonp"},
            {".k25", "p_w_picpath/x-kodak-k25"},
            {".kar", "audio/midi"},
            {".karbon", "application/x-karbon"},
            {".kdc", "p_w_picpath/x-kodak-kdc"},
            {".kdelnk", "application/x-desktop"},
            {".kexi", "application/x-kexiproject-sqlite3"},
            {".kexic", "application/x-kexi-connectiondata"},
            {".kexis", "application/x-kexiproject-shortcut"},
            {".kfo", "application/x-kformula"},
            {".kil", "application/x-killustrator"},
            {".kino", "application/smil"},
            {".kml", "application/vnd.google-earth.kml+xml"},
            {".kmz", "application/vnd.google-earth.kmz"},
            {".kon", "application/x-kontour"},
            {".kpm", "application/x-kpovmodeler"},
            {".kpr", "application/x-kpresenter"},
            {".kpt", "application/x-kpresenter"},
            {".kra", "application/x-krita"},
            {".ksp", "application/x-kspread"},
            {".kud", "application/x-kugar"},
            {".kwd", "application/x-kword"},
            {".kwt", "application/x-kword"},
            {".la", "application/x-shared-library-la"},
            {".latex", "text/x-tex"},
            {".ldif", "text/x-ldif"},
            {".lha", "application/x-lha"},
            {".lhs", "text/x-literate-haskell"},
            {".lhz", "application/x-lhz"},
            {".log", "text/x-log"},
            {".ltx", "text/x-tex"},
            {".lua", "text/x-lua"},
            {".lwo", "p_w_picpath/x-lwo"},
            {".lwob", "p_w_picpath/x-lwo"},
            {".lws", "p_w_picpath/x-lws"},
            {".ly", "text/x-lilypond"},
            {".lyx", "application/x-lyx"},
            {".lz", "application/x-lzip"},
            {".lzh", "application/x-lha"},
            {".lzma", "application/x-lzma"},
            {".lzo", "application/x-lzop"},
            {".m", "text/x-matlab"},
            {".m15", "audio/x-mod"},
            {".m2t", "video/mpeg"},
            {".m3u", "audio/x-mpegurl"
            },
            {".m3u8", "audio/x-mpegurl"
            },
            {".m4", "application/x-m4"},
            {".m4a", "audio/mp4"},
            {".m4b", "audio/x-m4b"},
            {".m4v", "video/mp4"},
            {".mab", "application/x-markaby"},
            {".man", "application/x-troff-man"},
            {".mbox", "application/mbox"},
            {".md", "application/x-genesis-rom"},
            {".mdb", "application/vnd.ms-access"},
            {".mdi", "p_w_picpath/vnd.ms-modi"},
            {".me", "text/x-troff-me"},
            {".med", "audio/x-mod"},
            {".metalink", "application/metalink+xml"},
            {".mgp", "application/x-magicpoint"},
            {".mid", "audio/midi"},
            {".midi", "audio/midi"},
            {".mif", "application/x-mif"},
            {".minipsf", "audio/x-minipsf"},
            {".mka", "audio/x-matroska"},
            {".mkv", "video/x-matroska"},
            {".ml", "text/x-ocaml"},
            {".mli", "text/x-ocaml"},
            {".mm", "text/x-troff-mm"},
            {".mmf", "application/x-smaf"},
            {".mml", "text/mathml"},
            {".mng", "video/x-mng"},
            {".mo", "application/x-gettext-translation"},
            {".mo3", "audio/x-mo3"},
            {".moc", "text/x-moc"},
            {".mod", "audio/x-mod"},
            {".mof", "text/x-mof"},
            {".moov", "video/quicktime"},
            {".mov", "video/quicktime"},
            {".movie", "video/x-sgi-movie"},
            {".mp+", "audio/x-musepack"},
            {".mp2", "video/mpeg"},
            {".mp3", "audio/mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "audio/x-musepack"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpga", "audio/mpeg"},
            {".mpp", "audio/x-musepack"},
            {".mrl", "text/x-mrml"},
            {".mrml", "text/x-mrml"},
            {".mrw", "p_w_picpath/x-minolta-mrw"},
            {".ms", "text/x-troff-ms"},
            {".msi", "application/x-msi"},
            {".msod", "p_w_picpath/x-msod"},
            {".msx", "application/x-msx-rom"},
            {".mtm", "audio/x-mod"},
            {".mup", "text/x-mup"},
            {".mxf", "application/mxf"},
            {".n64", "application/x-n64-rom"},
            {".nb", "application/mathematica"},
            {".nc", "application/x-netcdf"},
            {".nds", "application/x-nintendo-ds-rom"},
            {".nef", "p_w_picpath/x-nikon-nef"},
            {".nes", "application/x-nes-rom"},
            {".nfo", "text/x-nfo"},
            {".not", "text/x-mup"},
            {".nsc", "application/x-netshow-channel"},
            {".nsv", "video/x-nsv"},
            {".o", "application/x-object"},
            {".obj", "application/x-tgif"},
            {".ocl", "text/x-ocl"},
            {".oda", "application/oda"},
            {".odb", "application/vnd.oasis.opendocument.database"},
            {".odc", "application/vnd.oasis.opendocument.chart"},
            {".odf", "application/vnd.oasis.opendocument.formula"},
            {".odg", "application/vnd.oasis.opendocument.graphics"},
            {".odi", "application/vnd.oasis.opendocument.p_w_picpath"},
            {".odm", "application/vnd.oasis.opendocument.text-master"},
            {".odp", "application/vnd.oasis.opendocument.presentation"},
            {".ods", "application/vnd.oasis.opendocument.spreadsheet"},
            {".odt", "application/vnd.oasis.opendocument.text"},
            {".oga", "audio/ogg"},
            {".ogg", "video/x-theora+ogg"},
            {".ogm", "video/x-ogm+ogg"},
            {".ogv", "video/ogg"},
            {".ogx", "application/ogg"},
            {".old", "application/x-trash"},
            {".oleo", "application/x-oleo"},
            {".opml", "text/x-opml+xml"},
            {".ora", "p_w_picpath/openraster"},
            {".orf", "p_w_picpath/x-olympus-orf"},
            {".otc", "application/vnd.oasis.opendocument.chart-template"},
            {".otf", "application/x-font-otf"},
            {".otg", "application/vnd.oasis.opendocument.graphics-template"},
            {".oth", "application/vnd.oasis.opendocument.text-web"},
            {".otp", "application/vnd.oasis.opendocument.presentation-template"},
            {".ots", "application/vnd.oasis.opendocument.spreadsheet-template"},
            {".ott", "application/vnd.oasis.opendocument.text-template"},
            {".owl", "application/rdf+xml"},
            {".oxt", "application/vnd.openofficeorg.extension"},
            {".p", "text/x-pascal"},
            {".p10", "application/pkcs10"},
            {".p12", "application/x-pkcs12"},
            {".p7b", "application/x-pkcs7-certificates"},
            {".p7s", "application/pkcs7-signature"},
            {".pack", "application/x-java-pack200"},
            {".pak", "application/x-pak"},
            {".par2", "application/x-par2"},
            {".pas", "text/x-pascal"},
            {".patch", "text/x-patch"},
            {".pbm", "p_w_picpath/x-portable-bitmap"},
            {".pcd", "p_w_picpath/x-photo-cd"},
            {".pcf", "application/x-cisco-***-settings"},
            {".pcf.gz", "application/x-font-pcf"},
            {".pcf.z", "application/x-font-pcf"},
            {".pcl", "application/vnd.hp-pcl"},
            {".pcx", "p_w_picpath/x-pcx"},
            {".pdb", "chemical/x-pdb"},
            {".pdc", "application/x-aportisdoc"},
            {".pdf", "application/pdf"},
            {".pdf.bz2", "application/x-bzpdf"},
            {".pdf.gz", "application/x-gzpdf"},
            {".pef", "p_w_picpath/x-pentax-pef"},
            {".pem", "application/x-x509-ca-cert"},
            {".perl", "application/x-perl"},
            {".pfa", "application/x-font-type1"},
            {".pfb", "application/x-font-type1"},
            {".pfx", "application/x-pkcs12"},
            {".pgm", "p_w_picpath/x-portable-graymap"},
            {".pgn", "application/x-chess-pgn"},
            {".pgp", "application/pgp-encrypted"},
            {".php", "application/x-php"},
            {".php3", "application/x-php"},
            {".php4", "application/x-php"},
            {".pict", "p_w_picpath/x-pict"},
            {".pict1", "p_w_picpath/x-pict"},
            {".pict2", "p_w_picpath/x-pict"},
            {".pickle", "application/python-pickle"},
            {".pk", "application/x-tex-pk"},
            {".pkipath", "application/pkix-pkipath"},
            {".pkr", "application/pgp-keys"},
            {".pl", "application/x-perl"},
            {".pla", "audio/x-iriver-pla"},
            {".pln", "application/x-planperfect"},
            {".pls", "audio/x-scpls"},
            {".pm", "application/x-perl"},
            {".png", "p_w_picpath/png"},
            {".pnm", "p_w_picpath/x-portable-anymap"},
            {".pntg", "p_w_picpath/x-macpaint"},
            {".po", "text/x-gettext-translation"},
            {".por", "application/x-spss-por"},
            {".pot", "text/x-gettext-translation-template"},
            {".ppm", "p_w_picpath/x-portable-pixmap"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prc", "application/x-palm-database"},
            {".ps", "application/postscript"},
            {".ps.bz2", "application/x-bzpostscript"},
            {".ps.gz", "application/x-gzpostscript"},
            {".psd", "p_w_picpath/vnd.adobe.photoshop"},
            {".psf", "audio/x-psf"},
            {".psf.gz", "application/x-gz-font-linux-psf"},
            {".psflib", "audio/x-psflib"},
            {".psid", "audio/prs.sid"},
            {".psw", "application/x-pocket-word"},
            {".pw", "application/x-pw"},
            {".py", "text/x-python"},
            {".pyc", "application/x-python-bytecode"},
            {".pyo", "application/x-python-bytecode"},
            {".qif", "p_w_picpath/x-quicktime"},
            {".qt", "video/quicktime"},
            {".qtif", "p_w_picpath/x-quicktime"},
            {".qtl", "application/x-quicktime-media-link"},
            {".qtvr", "video/quicktime"},
            {".ra", "audio/vnd.rn-realaudio"},
            {".raf", "p_w_picpath/x-fuji-raf"},
            {".ram", "application/ram"},
            {".rar", "application/x-rar"},
            {".ras", "p_w_picpath/x-cmu-raster"},
            {".raw", "p_w_picpath/x-panasonic-raw"},
            {".rax", "audio/vnd.rn-realaudio"},
            {".rb", "application/x-ruby"},
            {".rdf", "application/rdf+xml"},
            {".rdfs", "application/rdf+xml"},
            {".reg", "text/x-ms-regedit"},
            {".rej", "application/x-reject"},
            {".rgb", "p_w_picpath/x-rgb"},
            {".rle", "p_w_picpath/rle"},
            {".rm", "application/vnd.rn-realmedia"},
            {".rmj", "application/vnd.rn-realmedia"},
            {".rmm", "application/vnd.rn-realmedia"},
            {".rms", "application/vnd.rn-realmedia"},
            {".rmvb", "application/vnd.rn-realmedia"},
            {".rmx", "application/vnd.rn-realmedia"},
            {".roff", "text/troff"},
            {".rp", "p_w_picpath/vnd.rn-realpix"},
            {".rpm", "application/x-rpm"},
            {".rss", "application/rss+xml"},
            {".rt", "text/vnd.rn-realtext"},
            {".rtf", "application/rtf"},
            {".rtx", "text/richtext"},
            {".rv", "video/vnd.rn-realvideo"},
            {".rvx", "video/vnd.rn-realvideo"},
            {".s3m", "audio/x-s3m"},
            {".sam", "application/x-amipro"},
            {".sami", "application/x-sami"},
            {".sav", "application/x-spss-sav"},
            {".scm", "text/x-scheme"},
            {".sda", "application/vnd.stardivision.draw"},
            {".sdc", "application/vnd.stardivision.calc"},
            {".sdd", "application/vnd.stardivision.impress"},
            {".sdp", "application/sdp"},
            {".sds", "application/vnd.stardivision.chart"},
            {".sdw", "application/vnd.stardivision.writer"},
            {".sgf", "application/x-go-sgf"},
            {".sgi", "p_w_picpath/x-sgi"},
            {".sgl", "application/vnd.stardivision.writer"},
            {".sgm", "text/sgml"},
            {".sgml", "text/sgml"},
            {".sh", "application/x-shellscript"},
            {".shar", "application/x-shar"},
            {".shn", "application/x-shorten"},
            {".siag", "application/x-siag"},
            {".sid", "audio/prs.sid"},
            {".sik", "application/x-trash"},
            {".sis", "application/vnd.symbian.install"},
            {".sisx", "x-epoc/x-sisx-app"},
            {".sit", "application/x-stuffit"},
            {".siv", "application/sieve"},
            {".sk", "p_w_picpath/x-skencil"},
            {".sk1", "p_w_picpath/x-skencil"},
            {".skr", "application/pgp-keys"},
            {".slk", "text/spreadsheet"},
            {".smaf", "application/x-smaf"},
            {".smc", "application/x-snes-rom"},
            {".smd", "application/vnd.stardivision.mail"},
            {".smf", "application/vnd.stardivision.math"},
            {".smi", "application/x-sami"},
            {".smil", "application/smil"},
            {".sml", "application/smil"},
            {".sms", "application/x-sms-rom"},
            {".snd", "audio/basic"},
            {".so", "application/x-sharedlib"},
            {".spc", "application/x-pkcs7-certificates"},
            {".spd", "application/x-font-speedo"},
            {".spec", "text/x-rpm-spec"},
            {".spl", "application/x-shockwave-flash"},
            {".spx", "audio/x-speex"},
            {".sql", "text/x-sql"},
            {".sr2", "p_w_picpath/x-sony-sr2"},
            {".src", "application/x-wais-source"},
            {".srf", "p_w_picpath/x-sony-srf"},
            {".srt", "application/x-subrip"},
            {".ssa", "text/x-ssa"},
            {".stc", "application/vnd.sun.xml.calc.template"},
            {".std", "application/vnd.sun.xml.draw.template"},
            {".sti", "application/vnd.sun.xml.impress.template"},
            {".stm", "audio/x-stm"},
            {".stw", "application/vnd.sun.xml.writer.template"},
            {".sty", "text/x-tex"},
            {".sub", "text/x-subviewer"},
            {".sun", "p_w_picpath/x-sun-raster"},
            {".sv4cpio", "application/x-sv4cpio"},
            {".sv4crc", "application/x-sv4crc"},
            {".svg", "p_w_picpath/svg+xml"},
            {".svgz", "p_w_picpath/svg+xml-compressed"},
            {".swf", "application/x-shockwave-flash"},
            {".sxc", "application/vnd.sun.xml.calc"},
            {".sxd", "application/vnd.sun.xml.draw"},
            {".sxg", "application/vnd.sun.xml.writer.global"},
            {".sxi", "application/vnd.sun.xml.impress"},
            {".sxm", "application/vnd.sun.xml.math"},
            {".sxw", "application/vnd.sun.xml.writer"},
            {".sylk", "text/spreadsheet"},
            {".t", "text/troff"},
            {".t2t", "text/x-txt2tags"},
            {".tar", "application/x-tar"},
            {".tar.bz", "application/x-bzip-compressed-tar"},
            {".tar.bz2", "application/x-bzip-compressed-tar"},
            {".tar.gz", "application/x-compressed-tar"},
            {".tar.lzma", "application/x-lzma-compressed-tar"},
            {".tar.lzo", "application/x-tzo"},
            {".tar.xz", "application/x-xz-compressed-tar"},
            {".tar.z", "application/x-tarz"},
            {".tbz", "application/x-bzip-compressed-tar"},
            {".tbz2", "application/x-bzip-compressed-tar"},
            {".tcl", "text/x-tcl"},
            {".tex", "text/x-tex"},
            {".texi", "text/x-texinfo"},
            {".texinfo", "text/x-texinfo"},
            {".tga", "p_w_picpath/x-tga"},
            {".tgz", "application/x-compressed-tar"},
            {".theme", "application/x-theme"},
            {".themepack", "application/x-windows-themepack"},
            {".tif", "p_w_picpath/tiff"},
            {".tiff", "p_w_picpath/tiff"},
            {".tk", "text/x-tcl"},
            {".tlz", "application/x-lzma-compressed-tar"},
            {".tnef", "application/vnd.ms-tnef"},
            {".tnf", "application/vnd.ms-tnef"},
            {".toc", "application/x-cdrdao-toc"},
            {".torrent", "application/x-bittorrent"},
            {".tpic", "p_w_picpath/x-tga"},
            {".tr", "text/troff"},
            {".ts", "application/x-linguist"},
            {".tsv", "text/tab-separated-values"},
            {".tta", "audio/x-tta"},
            {".ttc", "application/x-font-ttf"},
            {".ttf", "application/x-font-ttf"},
            {".ttx", "application/x-font-ttx"},
            {".txt", "text/plain"},
            {".txz", "application/x-xz-compressed-tar"},
            {".tzo", "application/x-tzo"},
            {".ufraw", "application/x-ufraw"},
            {".ui", "application/x-designer"},
            {".uil", "text/x-uil"},
            {".ult", "audio/x-mod"},
            {".uni", "audio/x-mod"},
            {".uri", "text/x-uri"},
            {".url", "text/x-uri"},
            {".ustar", "application/x-ustar"},
            {".vala", "text/x-vala"},
            {".vapi", "text/x-vala"},
            {".vcf", "text/directory"},
            {".vcs", "text/calendar"},
            {".vct", "text/directory"},
            {".vda", "p_w_picpath/x-tga"},
            {".vhd", "text/x-vhdl"},
            {".vhdl", "text/x-vhdl"},
            {".viv", "video/vivo"},
            {".vivo", "video/vivo"},
            {".vlc", "audio/x-mpegurl"},
            {".vob", "video/mpeg"},
            {".voc", "audio/x-voc"},
            {".vor", "application/vnd.stardivision.writer"},
            {".vst", "p_w_picpath/x-tga"},
            {".wav", "audio/x-wav"},
            {".wax", "audio/x-ms-asx"},
            {".wb1", "application/x-quattropro"},
            {".wb2", "application/x-quattropro"},
            {".wb3", "application/x-quattropro"},
            {".wbmp", "p_w_picpath/vnd.wap.wbmp"},
            {".wcm", "application/vnd.ms-works"},
            {".wdb", "application/vnd.ms-works"},
            {".webm", "video/webm"},
            {".wk1", "application/vnd.lotus-1-2-3"},
            {".wk3", "application/vnd.lotus-1-2-3"},
            {".wk4", "application/vnd.lotus-1-2-3"},
            {".wks", "application/vnd.ms-works"},
            {".wma", "audio/x-ms-wma"},
            {".wmf", "p_w_picpath/x-wmf"},
            {".wml", "text/vnd.wap.wml"},
            {".wmls", "text/vnd.wap.wmlscript"},
            {".wmv", "video/x-ms-wmv"},
            {".wmx", "audio/x-ms-asx"},
            {".wp", "application/vnd.wordperfect"},
            {".wp4", "application/vnd.wordperfect"},
            {".wp5", "application/vnd.wordperfect"},
            {".wp6", "application/vnd.wordperfect"},
            {".wpd", "application/vnd.wordperfect"},
            {".wpg", "application/x-wpg"},
            {".wpl", "application/vnd.ms-wpl"},
            {".wpp", "application/vnd.wordperfect"},
            {".wps", "application/vnd.ms-works"},
            {".wri", "application/x-mswrite"},
            {".wrl", "model/vrml"},
            {".wv", "audio/x-wavpack"},
            {".wvc", "audio/x-wavpack-correction"},
            {".wvp", "audio/x-wavpack"},
            {".wvx", "audio/x-ms-asx"},
            {".x3f", "p_w_picpath/x-sigma-x3f"},
            {".xac", "application/x-gnucash"},
            {".xbel", "application/x-xbel"},
            {".xbl", "application/xml"},
            {".xbm", "p_w_picpath/x-xbitmap"},
            {".xcf", "p_w_picpath/x-xcf"},
            {".xcf.bz2", "p_w_picpath/x-compressed-xcf"},
            {".xcf.gz", "p_w_picpath/x-compressed-xcf"},
            {".xhtml", "application/xhtml+xml"},
            {".xi", "audio/x-xi"},
            {".xlf", "application/x-xliff"},
            {".xliff", "application/x-xliff"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".xm", "audio/x-xm"},
            {".xmf", "audio/x-xmf"},
            {".xmi", "text/x-xmi"},
            {".xml", "application/xml"},
            {".xpm", "p_w_picpath/x-xpixmap"},
            {".xps", "application/vnd.ms-xpsdocument"},
            {".xsl", "application/xml"},
            {".xslfo", "text/x-xslfo"},
            {".xslt", "application/xml"},
            {".xspf", "application/xspf+xml"},
            {".xul", "application/vnd.mozilla.xul+xml"},
            {".xwd", "p_w_picpath/x-xwindowdump"},
            {".xyz", "chemical/x-pdb"},
            {".xz", "application/x-xz"},
            {".w2p", "application/w2p"},
            {".z", "application/x-compress"},
            {".zabw", "application/x-abiword"},
            {".zip", "application/zip"},
            {".zoo", "application/x-zoo"},
    };


}
