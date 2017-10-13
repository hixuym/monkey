package io.sunflower.gizmo.uploads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

/**
 * This {@link FileItem} type wraps a file received via a form parameter.
 *
 * @author Jens Fendler <jf@jensfendler.com>
 */
public class ParameterFileItem implements FileItem {

  private String filename;

  private Map<String, String> headers;

  private File file;

  public ParameterFileItem() {
    headers = Maps.newHashMap();
  }

  public ParameterFileItem(String filename, File file, Map<String, String> headers) {
    this.filename = filename;
    this.file = file;
    this.headers = headers;
  }

  /**
   * @see io.sunflower.gizmo.uploads.FileItem#getFileName()
   */
  @Override
  public String getFileName() {
    return filename;
  }

  /**
   * @see io.sunflower.gizmo.uploads.FileItem#getInputStream()
   */
  @Override
  public InputStream getInputStream() {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  /**
   * @see io.sunflower.gizmo.uploads.FileItem#getFile()
   */
  @Override
  public File getFile() {
    return file;
  }

  /**
   * @see io.sunflower.gizmo.uploads.FileItem#getContentType()
   */
  @Override
  public String getContentType() {
    return headers.get("Content-Type");
  }

  /**
   * @see io.sunflower.gizmo.uploads.FileItem#getHeaders()
   */
  @Override
  @JsonIgnore
  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * @see io.sunflower.gizmo.uploads.FileItem#cleanup()
   */
  @Override
  public void cleanup() {
    // TODO check from where cleanup() is called and consider removing the
    // file.
  }

  @Override
  public String toString() {
    return "ParameterFileItem [filename=" + filename + ", file=" + file.getAbsolutePath() + "]";
  }
}
