package org.sevengod.javabe.web.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.OutputStream;

public interface MinioService {
    /**
     * 上传文件
     * @param file 文件
     * @param bucketName 存储桶名称
     * @param userId 用户ID
     * @return 文件的访问路径
     */
    String uploadFile(MultipartFile file, String bucketName, Long userId) throws Exception;

    /**
     * 上传文件流
     * @param fileName 文件名
     * @param inputStream 输入流
     * @param bucketName 存储桶名称
     * @param userId 用户ID
     * @return 文件的访问路径
     */
    String uploadFile(String fileName, InputStream inputStream, String bucketName, Long userId) throws Exception;

    /**
     * 下载文件到响应流
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param userId 用户ID
     * @param out HTTP响应对象
     */
    void downloadFile(String bucketName, String objectName, Long userId, OutputStream out) throws Exception;

    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param userId 用户ID
     */
    void deleteFile(String bucketName, String objectName, Long userId) throws Exception;

    /**
     * 获取文件访问URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param userId 用户ID
     * @return 预签名的URL
     */
    String getFileUrl(String bucketName, String objectName, Long userId) throws Exception;

    /**
     * 检查文件是否存在
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否存在
     */
    boolean isFileExist(String bucketName, String objectName) throws Exception;

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     */
    void createBucket(String bucketName) throws Exception;

    /**
     * 检查存储桶是否存在
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName) throws Exception;
}
