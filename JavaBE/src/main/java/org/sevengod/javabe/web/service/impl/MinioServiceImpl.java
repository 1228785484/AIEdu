package org.sevengod.javabe.web.service.impl;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.sevengod.javabe.config.properties.MinioProperties;
import org.sevengod.javabe.web.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String uploadFile(MultipartFile file, String bucketName, Long userId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        // 简化文件名：userId_原始文件名
        String fileName = String.format("%d_%s", userId, originalFilename);
        
        try (InputStream inputStream = file.getInputStream()) {
            return uploadFile(fileName, inputStream, bucketName, userId);
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String bucketName, Long userId) throws Exception {
        if (!StringUtils.hasText(fileName) || inputStream == null) {
            throw new IllegalArgumentException("文件名和输入流不能为空");
        }
        
        // 确保存储桶存在
        createBucketIfNotExists(bucketName);
        
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            
            minioClient.putObject(objectArgs);
            return getFileUrl(bucketName, fileName, userId);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void downloadFile(String bucketName, String objectName, Long userId, OutputStream outputStream) throws Exception {
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectName, Long userId) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String getFileUrl(String bucketName, String objectName, Long userId) throws Exception {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS) // URL有效期7天
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage());
            throw new RuntimeException("获取文件URL失败", e);
        }
    }

    @Override
    public boolean isFileExist(String bucketName, String objectName) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void createBucket(String bucketName) throws Exception {
        if (!bucketExists(bucketName)) {
            try {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            } catch (Exception e) {
                log.error("创建存储桶失败: {}", e.getMessage());
                throw new RuntimeException("创建存储桶失败", e);
            }
        }
    }

    @Override
    public boolean bucketExists(String bucketName) throws Exception {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: {}", e.getMessage());
            throw new RuntimeException("检查存储桶是否存在失败", e);
        }
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        if (!bucketExists(bucketName)) {
            createBucket(bucketName);
        }
    }
}
