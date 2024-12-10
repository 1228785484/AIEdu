package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.web.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理接口")
public class FileController {

    @Autowired
    private MinioService minioService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public AjaxResult uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储桶名称") @RequestParam("bucketName") String bucketName,
            @Parameter(description = "用户ID") @RequestParam("userId") Long userId) {
        try {
            String fileUrl = minioService.uploadFile(file, bucketName, userId);
            return AjaxResult.success("文件上传成功", fileUrl);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            return AjaxResult.error("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "下载文件")
    @PostMapping("/download")
    public AjaxResult downloadFile(@RequestBody Map<String, Object> params) {
        try {
            String bucketName = (String) params.get("bucketName");
            String objectName = (String) params.get("objectName");
            Long userId = Long.parseLong(params.get("userId").toString());
            
            String fileUrl = minioService.getFileUrl(bucketName, objectName, userId);
            return AjaxResult.success("获取下载链接成功", fileUrl);
        } catch (Exception e) {
            log.error("获取下载链接失败: {}", e.getMessage());
            return AjaxResult.error("获取下载链接失败: " + e.getMessage());
        }
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download")
    public void downloadFile(
            @Parameter(description = "文件名") @RequestParam("fileName") String fileName,
            @Parameter(description = "存储桶名称") @RequestParam("bucketName") String bucketName,
            @Parameter(description = "用户ID") @RequestParam("userId") Long userId,
            HttpServletResponse response) {
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            minioService.downloadFile(bucketName, fileName, userId, response.getOutputStream());
        } catch (Exception e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "删除文件")
    @PostMapping("/delete")
    public AjaxResult deleteFile(@RequestBody Map<String, Object> params) {
        try {
            String bucketName = (String) params.get("bucketName");
            String objectName = (String) params.get("objectName");
            Long userId = Long.parseLong(params.get("userId").toString());
            
            minioService.deleteFile(bucketName, objectName, userId);
            return AjaxResult.success("文件删除成功");
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            return AjaxResult.error("文件删除失败: " + e.getMessage());
        }
    }
}
