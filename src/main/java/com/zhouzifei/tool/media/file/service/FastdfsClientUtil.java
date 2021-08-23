package com.zhouzifei.tool.media.file.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Set;

@Component
@Slf4j
public class FastdfsClientUtil {

    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    public FastdfsClientUtil() {
    }

    public VirtualFile imgUploadAndthumb(MultipartFile myfile) {
        try {
            String originalFilename = myfile.getOriginalFilename().substring(myfile.getOriginalFilename().lastIndexOf(".") + 1);
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());
            if (log.isDebugEnabled()) {
                log.debug(">>>>> 上传文件名称：{}; 扩展名为: {}", originalFilename, ext);
            }

            StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(myfile.getInputStream(), myfile.getSize(), originalFilename, (Set)null);
            return this.getfIleDTO(originalFilename, ext, storePath);
        } catch (IOException var5) {
            log.error("FDFS upload error ！ res：{}", var5);
            throw new ServiceException("文件上传异常!");
        }
    }

    public String getThumbImagePath(String fullPath) {
        return this.thumbImageConfig.getThumbImagePath(fullPath);
    }

    public String fileUpload(MultipartFile myfile) {
        try {
            String originalFilename = myfile.getOriginalFilename().substring(myfile.getOriginalFilename().lastIndexOf(".") + 1);
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());
            log.debug(">>>>> 上传文件名称：{}; 扩展名为: {}", originalFilename, ext);
            StorePath storePath = this.storageClient.uploadFile(myfile.getInputStream(), myfile.getSize(), originalFilename, (Set)null);
            return storePath.getFullPath();
        } catch (IOException var5) {
            log.error("FDFS upload error ！ res：{}", var5);
            throw new ServiceException("文件上传异常!");
        }
    }

    public String uploadFile(byte[] bytes, String format) {
        StorePath storePath = this.storageClient.uploadFile(new ByteArrayInputStream(bytes), (long)bytes.length, format, (Set)null);
        return storePath.getFullPath();
    }

    public String uploadFile(File file) {
        try {
            StorePath storePath = this.storageClient.uploadFile(FileUtils.openInputStream(file), file.length(), FilenameUtils.getExtension(file.getName()), (Set)null);
            return storePath.getFullPath();
        } catch (IOException var3) {
            log.error("FDFS upload error ！ res：{}", var3);
            throw new ServiceException("文件上传异常!");
        }
    }

    public String uploadFile(String content, String fileExtension) {
        try {
            byte[] buff = content.getBytes("UTF-8");
            ByteArrayInputStream stream = new ByteArrayInputStream(buff);
            StorePath storePath = this.storageClient.uploadFile(stream, (long)buff.length, fileExtension, (Set)null);
            return storePath.getFullPath();
        } catch (UnsupportedEncodingException var6) {
            log.error("FDFS upload error ！ res：{}", var6);
            throw new ServiceException("文件上传异常!");
        }
    }
    public String uploadFile(InputStream inputStream,String fileExtension) {
        try {
            final int available = inputStream.available();
            StorePath storePath = this.storageClient.uploadFile(inputStream, (long)available, fileExtension, (Set)null);
            return storePath.getFullPath();
        } catch (IOException var6) {
            log.error("FDFS upload error ！ res：{}", var6);
            throw new ServiceException("文件上传异常!");
        }
    }
    public byte[] downFile(String filePath) {
        StorePath storePath = StorePath.parseFromUrl(filePath);
        return (byte[])this.storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadCallback<byte[]>() {
            @Override
            public byte[] recv(InputStream ins) throws IOException {
                return IOUtils.toByteArray(ins);
            }
        });
    }

    public void deleteFile(String fileUrl) {
        if (!StringUtils.isEmpty(fileUrl)) {
            try {
                StorePath storePath = StorePath.parseFromUrl(fileUrl);
                this.storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
            } catch (FdfsUnsupportStorePathException var3) {
                log.error("FDFS upload error ！ res：{}", var3);
                throw new ServiceException("文件删除失败!");
            }
        }
    }

    private VirtualFile getfIleDTO(String originalFilename, String ext, StorePath storePath) {
        VirtualFile virtualFile = new VirtualFile();
        virtualFile.setOriginalFileName(originalFilename);
        virtualFile.setSuffix(ext);
        virtualFile.setFullFilePath(storePath.getFullPath());
//        virtualFile.setThumbImagePath(this.thumbImageConfig.getThumbImagePath(storePath.getFullPath()));
        if (log.isDebugEnabled()) {
            log.debug(">>>>> 文件上传详情 : {}", virtualFile.toString());
        }

        return virtualFile;
    }
}