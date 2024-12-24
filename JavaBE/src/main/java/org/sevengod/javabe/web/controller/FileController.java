package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "文件管理接口", description = "提供文件上传、下载、删除等基础操作")
public class FileController {

    @Autowired
    private MinioService minioService;

    @Operation(
        summary = "上传文件",
        description = "上传文件到指定的存储桶，返回可访问的文件URL。支持常见文件格式，单个文件大小限制为100MB"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件上传成功", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResult.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadFile(
            @Parameter(description = "文件，支持常见格式，大小限制100MB", required = true) 
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储桶名称，例如：images, documents等", required = true) 
            @RequestParam("bucketName") String bucketName,
            @Parameter(description = "用户ID，用于权限控制和文件归属", required = true) 
            @RequestParam("userId") Long userId) {
        try {
            String fileUrl = minioService.uploadFile(file, bucketName, userId);
            return AjaxResult.success("文件上传成功", fileUrl);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            return AjaxResult.error("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(
        summary = "获取文件下载链接",
        description = "获取指定文件的临时下载链接，链接有效期为7天。\n" +
                     "请求参数说明：\n" +
                     "- bucketName: 存储桶名称（必填）\n" +
                     "- objectName: 文件对象名称（必填）\n" +
                     "- userId: 用户ID（必填）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取下载链接", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResult.class))),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/download")
    public AjaxResult downloadFile(
            @RequestBody Map<String, Object> params) {
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

    @Operation(
        summary = "直接下载文件",
        description = "直接下载指定文件，支持断点续传和大文件下载"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件下载成功", 
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/download")
    public void downloadFile(
            @Parameter(description = "文件名，包含扩展名", required = true, example = "example.pdf") 
            @RequestParam("fileName") String fileName,
            @Parameter(description = "存储桶名称", required = true, example = "documents") 
            @RequestParam("bucketName") String bucketName,
            @Parameter(description = "用户ID", required = true, example = "12345") 
            @RequestParam("userId") Long userId,
            @Parameter(description = "HTTP响应对象，用于文件流写入", hidden = true) 
            HttpServletResponse response) {
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            log.info("正在下载:{}", fileName);
            minioService.downloadFile(bucketName, fileName, userId, response.getOutputStream());
        } catch (Exception e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "删除文件",
        description = "从指定存储桶中删除文件，删除后无法恢复。\n" +
                     "请求参数说明：\n" +
                     "- bucketName: 存储桶名称（必填）\n" +
                     "- objectName: 要删除的文件名称（必填）\n" +
                     "- userId: 用户ID，用于权限验证（必填）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件删除成功", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResult.class))),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "403", description = "没有删除权限"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/delete")
    public AjaxResult deleteFile(
            @RequestBody Map<String, Object> params) {
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
