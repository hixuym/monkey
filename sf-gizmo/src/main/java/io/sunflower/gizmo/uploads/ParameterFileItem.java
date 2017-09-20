package io.sunflower.gizmo.uploads;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.util.FileItemHeadersImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * This {@link FileItem} type wraps a file received via a form parameter.
 *
 * @author Jens Fendler <jf@jensfendler.com>
 */
public class ParameterFileItem implements FileItem {

    private String filename;

    private FileItemHeaders headers;

    private File file;

    public ParameterFileItem() {
        headers = new FileItemHeadersImpl();
    }

    public ParameterFileItem(String filename, File file, FileItemHeaders headers) {
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
        return headers.getHeader("Content-Type");
    }

    /**
     * @see io.sunflower.gizmo.uploads.FileItem#getHeaders()
     */
    @Override
    @JsonIgnore
    public FileItemHeaders getHeaders() {
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
