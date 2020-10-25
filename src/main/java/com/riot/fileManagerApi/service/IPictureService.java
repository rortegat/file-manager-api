package com.riot.fileManagerApi.service;

import com.riot.fileManagerApi.dto.PictureDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IPictureService {
    PictureDTO uploadFile(String path, MultipartFile file);
    public void deleteFile( String path, String filename);
    File getFile(String path, String filename);
    Path getPath(String filePath, String filename);
    Stream<Path> getDirectoryFiles(String directoryPath);
    Resource getResource(String filePath, String filename);
}
