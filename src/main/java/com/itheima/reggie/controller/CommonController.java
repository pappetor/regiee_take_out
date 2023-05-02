package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    //上传文件
    @PostMapping("/upload")
    public R<String> upload(@RequestPart("file") MultipartFile file) {
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID() + suffix;
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }

    //下载文件
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        byte[] buffer = new byte[1024];
        try {
            FileInputStream is = new FileInputStream(basePath + name);
            ServletOutputStream os = response.getOutputStream();
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
