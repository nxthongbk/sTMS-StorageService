package com.scity.storage.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SUPPORT_FILE_TYPE {

  private SUPPORT_FILE_TYPE() {
    throw new IllegalStateException("Utility SUPPORT_FILE_TYPE class should not be initiated");
  }

  public static boolean isSupported(String mimeType) {
    return Audio.MIME_TYPES.contains(mimeType)
        || Image.MIME_TYPES.contains(mimeType)
        || Video.MIME_TYPES.contains(mimeType)
        || Office.MIME_TYPES.contains(mimeType);
  }

  public static final class Audio {

    public static final Set<String> MIME_TYPES =
        new HashSet<>(
            Arrays.asList(
                "audio/mpeg",
                "audio/ogg",
                "audio/x-wav",
                "audio/webm",
                "audio/vnd.wave",
                "audio/mp4"));

    private Audio() {
      throw new IllegalStateException("Utility Audio class should not be initiated");
    }
  }

  public static final class Video {

    public static final Set<String> MIME_TYPES =
        new HashSet<>(
            Arrays.asList(
                "video/mp4",
                "video/mpeg",
                "video/ogg",
                "video/webm",
                "video/3gpp",
                "video/x-msvideo",
                "video/x-ms-wmv",
                "video/quicktime",
                "video/theora",
                "application/x-matroska"
//                ,"video/hevc",
//                "video/h265"
            ));

    private Video() {
      throw new IllegalStateException("Utility Video class should not be initiated");
    }
  }

  public static final class Image {

    public static final Set<String> MIME_TYPES =
        new HashSet<>(
            Arrays.asList(
                "image/avif",
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/jpg",
                "image/bmp",
                "image/svg+xml",
                "image/webp",
                "image/tiff"
//                ,"image/heic",
//                "image/heif"
            ));

    private Image() {
      throw new IllegalStateException("Utility Image class should not be initiated");
    }
  }

  public static final class Office {

    public static final Set<String> MIME_TYPES =
        new HashSet<>(
            Arrays.asList(
                "application/x-abiword",
                "application/x-freearc",
                "application/vnd.amazon.ebook",
                "application/octet-stream",
                "application/x-bzip",
                "application/x-bzip2",
                "application/x-csh",
                "application/gzip",
                "text/html",
                "application/vnd.oasis.opendocument.presentation",
                "application/vnd.oasis.opendocument.spreadsheet",
                "application/vnd.oasis.opendocument.text",
                "application/vnd.rar",
                "application/x-tar",
                "text/plain",
                "application/vnd.visio",
                "application/xhtml+xml",
                "application/zip",
                "application/x-7z-compressed",
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"));

    private Office() {
      throw new IllegalStateException("Utility Office class should not be initiated");
    }
  }
}
