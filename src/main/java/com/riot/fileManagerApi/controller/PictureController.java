package com.riot.fileManagerApi.controller;

import com.riot.fileManagerApi.dto.PictureDTO;
import com.riot.fileManagerApi.service.IPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLConnection;

@CrossOrigin("*")
@RestController
@RequestMapping("/pictures")
public class PictureController {

    @Value("${config.file.upload-dir}")
    private String rootDir;

    @Autowired
    @Qualifier("pictureServiceImpl")
    private IPictureService pictureService;

    @PostMapping("/{username}/upload")
    public PictureDTO uploadUserFile(@PathVariable String username,
                                     @RequestParam("picture") MultipartFile file) {
        String dirPath = rootDir + File.separator+username;
        return pictureService.uploadFile(dirPath, file);
    }

    @GetMapping("/{username}/preview")
    public ResponseEntity<Resource> getImageAsset(@PathVariable String username) {
        String dirPath = rootDir + File.separator+username;
        Resource resource = pictureService.getResource(dirPath, "profile-picture.png");
        var file = pictureService.getFile(rootDir, "profile-picture.png");
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "filename=\"" + username + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteFile( @PathVariable String id) {
        pictureService.deleteFile(id, "");
    }
}
