package com.tencent.appframework;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.appframework.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Tencent. Author: raezlu Date: 12-10-24 Time: 下午7:27
 */
public class FileUtil {

    public static final int ZIP_BUFFER_SIZE = 4 * 1024;

    /**
     * Comparator of files.
     */
    public interface FileComparator {
        public boolean equals(File lhs, File rhs);
    }

    /**
     * Simple file comparator which only depends on file length and modification time.
     */
    public final static FileComparator SIMPLE_COMPARATOR = new FileComparator() {
        @Override
        public boolean equals(File lhs, File rhs) {
            return (lhs.length() == rhs.length()) && (lhs.lastModified() == rhs.lastModified());
        }
    };


    /**
     * Copy files. If src is a directory, then all it's sub files will be copied into directory dst. If src is a file, then it will be copied to file
     * dst.
     *
     * @param src file or directory to copy.
     * @param dst destination file or directory.
     * @return true if copy complete perfectly, false otherwise (more than one file cannot be copied).
     */
    public static boolean copyFiles(File src, File dst) {
        return copyFiles(src, dst, null);
    }

    /**
     * Copy files. If src is a directory, then all it's sub files will be copied into directory dst. If src is a file, then it will be copied to file
     * dst.
     *
     * @param src    file or directory to copy.
     * @param dst    destination file or directory.
     * @param filter a file filter to determine whether or not copy corresponding file.
     * @return true if copy complete perfectly, false otherwise (more than one file cannot be copied).
     */
    public static boolean copyFiles(File src, File dst, FileFilter filter) {
        return copyFiles(src, dst, filter, SIMPLE_COMPARATOR);
    }

    /**
     * Copy files. If src is a directory, then all it's sub files will be copied into directory dst. If src is a file, then it will be copied to file
     * dst.
     *
     * @param src        file or directory to copy.
     * @param dst        destination file or directory.
     * @param filter     a file filter to determine whether or not copy corresponding file.
     * @param comparator a file comparator to determine whether src & dst are equal files. Null to overwrite all dst files.
     * @return true if copy complete perfectly, false otherwise (more than one file cannot be copied).
     */
    public static boolean copyFiles(File src, File dst, FileFilter filter, FileComparator comparator) {
        if (src == null || dst == null) {
            return false;
        }

        if (!src.exists()) {
            return false;
        }
        if (src.isFile()) {
            return performCopyFile(src, dst, filter, comparator);
        }

        File[] paths = src.listFiles();
        if (paths == null) {
            return false;
        }
        // default is true.
        boolean result = true;
        for (File sub : paths) {
            if (!copyFiles(sub, new File(dst, sub.getName()), filter)) {
                result = false;
            }
        }
        return result;
    }


    /**
     * 单个文件拷贝。
     */
    public static void copyFile(String srcFilename, String destFilename,
                                boolean overwrite) throws IOException {
        File srcFile = new File(srcFilename);
        // 首先判断源文件是否存在
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the source file: "
                    + srcFile.getAbsolutePath());
        }
        // 判断源文件是否可读
        if (!srcFile.canRead()) {
            throw new IOException("Cannot read the source file: "
                    + srcFile.getAbsolutePath());
        }
        File destFile = new File(destFilename);
        if (overwrite == false) {
            // 目标文件存在就不覆盖
            if (destFile.exists())
                return;
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException("Cannot write the destination file: "
                            + destFile.getAbsolutePath());
                }
            } else {
                // 不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException("Cannot write the destination file: "
                            + destFile.getAbsolutePath());
                }
            }
        }
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        byte[] block = new byte[1024];
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    destFile));
            while (true) {
                int readLength = inputStream.read(block);
                if (readLength == -1)
                    break;// end of file
                outputStream.write(block, 0, readLength);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    // just ignore
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    // just ignore
                }
            }
        }
    }

    public static long getDirSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                if (children != null) {
                    for (File f : children) {
                        size += getDirSize(f);
                    }
                }
                return size;
            } else {
                return file.length();
            }
        }
        return 0;
    }

    private static boolean performCopyFile(File srcFile, File dstFile, FileFilter filter, FileComparator comparator) {
        if (srcFile == null || dstFile == null) {
            return false;
        }
        if (filter != null && !filter.accept(srcFile)) {
            return false;
        }

        FileChannel inc = null;
        FileChannel ouc = null;
        try {
            if (!srcFile.exists() || !srcFile.isFile()) {
                return false;
            }

            if (dstFile.exists()) {
                if (comparator != null && comparator.equals(srcFile, dstFile)) {
                    // equal files.
                    return true;
                } else {
                    // delete it in case of folder.
                    delete(dstFile);
                }
            }

            File toParent = dstFile.getParentFile();
            if (toParent.isFile()) {
                delete(toParent);
            }
            if (!toParent.exists() && !toParent.mkdirs()) {
                return false;
            }

            inc = (new FileInputStream(srcFile)).getChannel();
            ouc = (new FileOutputStream(dstFile)).getChannel();

            ouc.transferFrom(inc, 0, inc.size());

        } catch (Throwable e) {
            e.printStackTrace();
            // exception occur, delete broken file.
            delete(dstFile);
            return false;
        } finally {
            try {
                if (inc != null) inc.close();
                if (ouc != null) ouc.close();
            } catch (Throwable e) {
                // empty.
            }
        }
        return true;
    }

    /**
     * Copy asset files. If assetName is a directory, then all it's sub files will be copied into directory dst. If assetName is a file, the it will
     * be copied to file dst.
     *
     * @param context   application context.
     * @param assetName asset name to copy.
     * @param dst       destination file or directory.
     */
    public static void copyAssets(Context context, String assetName, String dst) {
        if (isEmpty(dst)) {
            return;
        }
        if (assetName == null) {
            assetName = "";
        }
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetName);
        } catch (FileNotFoundException e) {
            // should be file.
            if (assetName.length() > 0) {
                performCopyAssetsFile(context, assetName, dst);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (files == null) {
            return;
        }

        if (files.length == 0) {
            // should be file or empty dir. Try to copy it.
            if (assetName.length() > 0) {
                performCopyAssetsFile(context, assetName, dst);
            }
        }

        for (String file : files) {
            if (isEmpty(file))
                continue;

            String newAssetDir = assetName.length() == 0 ? file : assetName + File.separator + file;
            String newDestDir = dst + File.separator + file;
            copyAssets(context, newAssetDir, newDestDir);
        }
    }

    private static void performCopyAssetsFile(Context context, String assetPath, String dstPath) {
        if (isEmpty(assetPath) || isEmpty(dstPath)) {
            return;
        }

        AssetManager assetManager = context.getAssets();
        File dstFile = new File(dstPath);

        InputStream in = null;
        OutputStream out = null;
        try {
            if (dstFile.exists()) {
                // try to determine whether or not copy this asset file, using their size.
                boolean tryStream = false;
                try {
                    AssetFileDescriptor fd = assetManager.openFd(assetPath);
                    if (dstFile.length() == fd.getLength()) {
                        // same file already exists.
                        return;
                    } else {
                        if (dstFile.isDirectory()) {
                            delete(dstFile);
                        }
                    }
                } catch (IOException e) {
                    // this file is compressed. cannot determine it's size.
                    tryStream = true;
                }

                if (tryStream) {
                    InputStream tmpIn = assetManager.open(assetPath);
                    try {
                        if (dstFile.length() == tmpIn.available()) {
                            return;
                        } else {
                            if (dstFile.isDirectory()) {
                                delete(dstFile);
                            }
                        }
                    } catch (IOException e) {
                        // do nothing.
                    } finally {
                        tmpIn.close();
                    }
                }
            }

            File parent = dstFile.getParentFile();
            if (parent.isFile()) {
                delete(parent);
            }
            if (!parent.exists() && !parent.mkdirs()) {
                return;
            }

            in = assetManager.open(assetPath);
            out = new BufferedOutputStream(new FileOutputStream(dstFile));
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            // delete broken file.
            delete(dstFile);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Throwable e) {
                // empty.
            }
        }
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file path to delete.
     */
    public static void delete(File file) {
        delete(file, false);
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file      path to delete.
     * @param ignoreDir whether ignore directory. If true, all files will be deleted while directories is reserved.
     */
    public static void delete(File file, boolean ignoreDir) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            boolean isDeleteSuccuss = file.delete();
            if(!isDeleteSuccuss){
                Log.d("FileUtil","delete() delete failed");
            }
            return;
        }

        File[] fileList = file.listFiles();
        if (fileList == null) {
            return;
        }

        for (File f : fileList) {
            delete(f, ignoreDir);
        }
        // delete the folder if need.
        if (!ignoreDir){
            boolean isDeleteSuccuss = file.delete();
            if(!isDeleteSuccuss){
                Log.d("FileUtil","ignoreDir = false ,delete() delete failed");
            }
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }


    public static boolean zip(File[] srcFiles, FileOutputStream dest) {
        // 参数检查
        if (srcFiles == null || srcFiles.length < 1 || dest == null) {
            return false;
        }

        boolean resu = false;

        ZipOutputStream zos = null;

        try {
            byte[] buffer = new byte[ZIP_BUFFER_SIZE];

            zos = new ZipOutputStream(new BufferedOutputStream(dest));

            // 添加文件到ZIP压缩流
            for (File src : srcFiles) {
                doZip(zos, src, null, buffer);
            }

            zos.flush();
            zos.closeEntry();

            resu = true;
        } catch (IOException e) {
            // e.print*StackTrace();

            resu = false;
        } finally {
            IOUtils.closeQuietly(zos);
        }

        return resu;
    }

    /**
     * ZIP压缩多个文件/文件夹
     *
     * @param srcFiles 要压缩的文件/文件夹列表
     * @param dest     目标文件
     * @return 压缩成功/失败
     */
    public static boolean zip(File[] srcFiles, File dest) {
        try {
            return zip(srcFiles, new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            LogUtil.e("FileUtil", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 方法：ZIP压缩单个文件/文件夹
     *
     * @param src 源文件/文件夹
     * @param dest   目标文件
     * @return 压缩成功/失败
     */
    public static boolean zip(File src, File dest) {
        return zip(new File[]{src}, dest);
    }

    /**
     * 方法：解压缩单个ZIP文件
     *
     * @param src 源文件/文件夹
     * @param destFolder   目标文件夹
     * @return 解压缩成功/失败
     */
    public static boolean unzip(File src, File destFolder) {
        if (src == null || src.length() < 1 || !src.canRead()) {
            return false;
        }

        boolean resu = false;

        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        ZipInputStream zis = null;

        BufferedOutputStream bos = null;

        ZipEntry entry = null;

        byte[] buffer = new byte[8 * 1024];

        int readLen = 0;

        try {
            zis = new ZipInputStream(new FileInputStream(src));

            while (null != (entry = zis.getNextEntry())) {
                System.out.println(entry.getName());

                if(entry.getName().startsWith("../")){
                    break;
                }

                if (entry.isDirectory()) {
                    new File(destFolder, entry.getName()).mkdirs();
                } else {
                    File entryFile = new File(destFolder, entry.getName());

                    entryFile.getParentFile().mkdirs();

                    bos = new BufferedOutputStream(new FileOutputStream(entryFile));

                    while (-1 != (readLen = zis.read(buffer, 0, buffer.length))) {
                        bos.write(buffer, 0, readLen);
                    }

                    bos.flush();
                    bos.close();
                }
            }

            if(zis != null) {
                zis.closeEntry();
                zis.close();
            }
            resu = true;

        } catch (IOException e) {
            resu = false;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(zis);
        }

        return resu;
    }

    /**
     * 压缩文件/文件夹到ZIP流中 <br> <br> <i>本方法是为了向自定义的压缩流添加文件/文件夹，若只是要压缩文件/文件夹到指定位置，请使用 {@code FileUtils.zip()} 方法</i>
     *
     * @param zos    ZIP输出流
     * @param file   被压缩的文件
     * @param root   被压缩的文件在ZIP文件中的入口根节点
     * @param buffer 读写缓冲区
     * @throws IOException 读写流时可能抛出的I/O异常
     */
    public static void doZip(ZipOutputStream zos, File file, String root, byte[] buffer) throws IOException {
        // 参数检查
        if (zos == null || file == null) {
            throw new IOException("I/O Object got NullPointerException");
        }

        if (!file.exists()) {
            throw new FileNotFoundException("Target File is missing");
        }

        BufferedInputStream bis = null;

        int readLen = 0;

        String rootName = TextUtils.isEmpty(root) ? (file.getName()) : (root + File.separator + file.getName());

        // 文件直接放入压缩流中
        if (file.isFile()) {
            try {
                bis = new BufferedInputStream(new FileInputStream(file));

                zos.putNextEntry(new ZipEntry(rootName));

                while (-1 != (readLen = bis.read(buffer, 0, buffer.length))) {
                    zos.write(buffer, 0, readLen);
                }

                IOUtils.closeQuietly(bis);
            } catch (IOException e) {
                IOUtils.closeQuietly(bis);
                // 关闭BIS流，并抛出异常
                throw e;
            }
        }
        // 文件夹则子文件递归
        else if (file.isDirectory()) {
            File[] subFiles = file.listFiles();

            if(subFiles != null) {
                for (File subFile : subFiles) {
                    doZip(zos, subFile, rootName, buffer);
                }
            }
        }
    }

    public static boolean unjar(File src, File destFolder) {
        if (src == null || src.length() < 1 || !src.canRead()) {
            return false;
        }

        boolean resu = false;

        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        JarInputStream zis = null;

        BufferedOutputStream bos = null;

        JarEntry entry = null;

        byte[] buffer = new byte[8 * 1024];

        int readLen = 0;

        try {
            zis = new JarInputStream(new FileInputStream(src));

            while (null != (entry = zis.getNextJarEntry())) {
                System.out.println(entry.getName());

                if(entry.getName().startsWith("../")) {
                    break;
                }

                if (entry.isDirectory()) {
                    new File(destFolder, entry.getName()).mkdirs();
                } else {
                    bos = new BufferedOutputStream(new FileOutputStream(new File(destFolder, entry.getName())));

                    while (-1 != (readLen = zis.read(buffer, 0, buffer.length))) {
                        bos.write(buffer, 0, readLen);
                    }

                    bos.flush();
                    bos.close();
                }
            }

            if(zis != null) {
                zis.closeEntry();
                zis.close();
            }

            resu = true;
        } catch (IOException e) {
            resu = false;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(zis);
        }

        return resu;
    }

    /**
     * 此文件是否存在
     */
    public static boolean isExistFile(String uploadFilePath) {

        if (TextUtils.isEmpty(uploadFilePath)) {
            //文件不存在 
            return false;
        }
        try {
            File file = new File(uploadFilePath);

            if (!file.exists() || !file.isFile() || file.length() == 0) {
                return false;
            }
        } catch (Exception e) {
            LogUtil.e("UploadTask", e.getMessage(), e);
            return false;
        }

        return true;
    }


    // ------------- common --------------
    private final static Object sCacheDirLock = new Object();

    static class InnerEnvironment {

        private static final String TAG = "InnerEnvironment";

        private static final String EXTEND_SUFFIX = "-ext";

        private static final File EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY
                = new File(new File(Environment.getExternalStorageDirectory(),
                "Android"), "data");

        public static File getExternalStorageAndroidDataDir() {
            return EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY;
        }

        public static File getExternalStorageAppCacheDirectory(String packageName) {
            return new File(new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY,
                    packageName), "cache");
        }

        public static File getExternalStorageAppFilesDirectory(String packageName) {
            return new File(new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY,
                    packageName), "files");
        }

        public static File getExternalCacheDir(Context context, boolean extend) {
            if (!extend && PlatformUtil.version() >= PlatformUtil.VERSION_CODES.FROYO) {
                return context.getExternalCacheDir();
            }
            synchronized (InnerEnvironment.class) {
                File externalCacheDir = getExternalStorageAppCacheDirectory(
                        context.getPackageName() + (extend ? EXTEND_SUFFIX : ""));
                if (!externalCacheDir.exists()) {
                    try {
                        boolean isCreateSuccuss = (new File(getExternalStorageAndroidDataDir(),".nomedia")).createNewFile();
                        if(!isCreateSuccuss){
                            Log.w(TAG, "Unable to create new file");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!externalCacheDir.mkdirs()) {
                        Log.w(TAG, "Unable to create external cache directory");
                        return null;
                    }
                }
                return externalCacheDir;
            }
        }

        public static File getExternalFilesDir(Context context, String type, boolean extend) {
            if (!extend && PlatformUtil.version() >= PlatformUtil.VERSION_CODES.FROYO) {
                return context.getExternalFilesDir(type);
            }
            synchronized (InnerEnvironment.class) {
                File externalFilesDir = getExternalStorageAppFilesDirectory(
                        context.getPackageName() + (extend ? EXTEND_SUFFIX : ""));
                if (!externalFilesDir.exists()) {
                    try {
                        boolean isCreateSuccuss = (new File(getExternalStorageAndroidDataDir(),".nomedia")).createNewFile();
                        if(!isCreateSuccuss){
                            Log.w(TAG, "Unable to create nomedia file");
                        }
                    } catch (IOException e) {
                    }
                    if (!externalFilesDir.mkdirs()) {
                        Log.w(TAG, "Unable to create external files directory");
                        return null;
                    }
                }
                if (type == null) {
                    return externalFilesDir;
                }
                File dir = new File(externalFilesDir, type);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.w(TAG, "Unable to create external media directory " + dir);
                        return null;
                    }
                }
                return dir;
            }
        }
    }

    /**
     * Get common cache dir(external if available, or internal) with corresponding name, which is not persist.
     */
    private static String getCacheDir(Context context, String name) {
        return getCacheDir(context, name, false);
    }

    /**
     * Get common cache dir(external if available, or internal) with corresponding name.
     *
     * @param context context
     * @param name    cache dir name.
     * @param persist whether this cache dir should be persist or not.
     * @return cache dir.
     */
    public static String getCacheDir(Context context, String name, boolean persist) {
        String dir = getExternalCacheDir(context, name, persist);
        return dir != null ? dir : getInternalCacheDir(context, name, persist);
    }

    /**
     * Get external cache dir with corresponding name, which is not persist.
     */
    public static String getExternalCacheDir(Context context, String name) {
        return getExternalCacheDir(context, name, false);
    }

    /**
     * Get external cache dir with corresponding name.
     *
     * @param persist whether this cache dir should be persist or not.
     */
    public static String getExternalCacheDir(Context context, String name, boolean persist) {
        String dir = getExternalCacheDir(context, persist);
        if (dir == null) {
            return null;
        }
        if (isEmpty(name)) {
            return dir;
        }
        File file = new File(dir + File.separator + name);
        if (!file.exists() || !file.isDirectory()) {
            synchronized (sCacheDirLock) {
                if (!file.isDirectory()) {
                    FileUtil.delete(file);
                    file.mkdirs();
                } else if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file.getAbsolutePath();
    }

    private static String getExternalCacheDir(Context context, boolean persist) {
        if (!isExternalAvailable()) {
            return null;
        }
        File externalDir = !persist ? InnerEnvironment.getExternalCacheDir(context, false)
                : InnerEnvironment.getExternalFilesDir(context, "cache", false);
        return externalDir == null ? null : externalDir.getAbsolutePath();
    }

    /**
     * Get extend external cache dir with corresponding name, which is not persist.
     */
    public static String getExternalCacheDirExt(Context context, String name) {
        return getExternalCacheDirExt(context, name, false);
    }

    /**
     * Get extend external cache dir with corresponding name.
     *
     * @param persist whether this cache dir should be persist or not.
     */
    public static String getExternalCacheDirExt(Context context, String name, boolean persist) {
        String dir = getExternalCacheDirExt(context, persist);
        if (dir == null) {
            return null;
        }
        if (isEmpty(name)) {
            return dir;
        }
        File file = new File(dir + File.separator + name);
        if (!file.exists() || !file.isDirectory()) {
            synchronized (sCacheDirLock) {
                if (!file.isDirectory()) {
                    FileUtil.delete(file);
                    file.mkdirs();
                } else if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file.getAbsolutePath();
    }

    private static String getExternalCacheDirExt(Context context, boolean persist) {
        if (!isExternalAvailable()) {
            return null;
        }
        File externalDir = !persist ? InnerEnvironment.getExternalCacheDir(context, true)
                : InnerEnvironment.getExternalFilesDir(context, "cache", true);
        return externalDir == null ? null : externalDir.getAbsolutePath();
    }

    /**
     * Get internal cache dir with corresponding name, which is not persist.
     */
    public static String getInternalCacheDir(Context context, String name) {
        return getInternalCacheDir(context, name, false);
    }

    /**
     * Get internal cache dir with corresponding name.
     *
     * @param persist whether this cache dir should be persist or not.
     */
    public static String getInternalCacheDir(Context context, String name, boolean persist) {
        String dir = getInternalCacheDir(context, persist);
        if (isEmpty(name)) {
            return dir;
        }
        File file = new File(dir + File.separator + name);
        if (!file.exists() || !file.isDirectory()) {
            synchronized (sCacheDirLock) {
                if (!file.isDirectory()) {
                    FileUtil.delete(file);
                    file.mkdirs();
                } else if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static String getInternalCacheDir(Context context, boolean persist) {
        return !persist ? context.getCacheDir().getAbsolutePath()
                : context.getFilesDir().getAbsolutePath() + File.separator + "cache";
    }

    public static String getInternalFileDir(Context context, boolean persist) {
        return !persist ? context.getCacheDir().getAbsolutePath()
                : context.getFilesDir().getAbsolutePath() + File.separator;
    }

    /**
     * Determine whether a path is external.
     */
    public static boolean isExternal(String path) {
        String externalCacheDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path != null && path.startsWith(externalCacheDir);
    }

    /**
     * Determine whether a path is internal.
     */
    public static boolean isInternal(String path) {
        String internalCacheDir = Environment.getDataDirectory().getAbsolutePath();
        return path != null && path.startsWith(internalCacheDir);
    }


    /**
     * Whether the external storage is available.
     */
    public static boolean isExternalAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


}
