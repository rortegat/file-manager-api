package com.riot.fileManagerApi.service.impl;

import com.riot.fileManagerApi.config.CustomException;
import com.riot.fileManagerApi.dto.PictureDTO;
import com.riot.fileManagerApi.service.IPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("pictureServiceImpl")
public class PictureService implements IPictureService {


    @Override
    public PictureDTO uploadFile(String path, MultipartFile file) {
        File folder = new File(path);
        if (!folder.isDirectory()) {
            try {
                Files.createDirectory(folder.toPath()).toFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String ele : folder.list()) {
            if (ele.equals(file.getOriginalFilename()))
                throw new CustomException("This file already exist", HttpStatus.NOT_MODIFIED);
        }

        String filename = "profile-picture.png";//StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = Paths.get(path);

        try {
            if (file.isEmpty()) {
                throw new CustomException("Failed to store empty file " + filename, HttpStatus.NOT_MODIFIED);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new CustomException("Cannot store file with relative path outside current directory " + filename, HttpStatus.NOT_MODIFIED);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream,
                        filePath.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);

                return new PictureDTO(
                        file.getOriginalFilename(),
                        filePath.toString(),
                        file.getContentType(),
                        file.getSize());
            }
        } catch (IOException e) {
            throw new CustomException("Failed to store file " + filename, HttpStatus.NOT_MODIFIED);
        }
    }

    @Override
    public void deleteFile(String path, String filename) {
        if (getFile(path, filename).delete())
            System.out.println("Eliminado");
        else throw new CustomException("Could not delete file", HttpStatus.NOT_MODIFIED);
    }

    //Retrieves file from directory
    @Override
    public File getFile(String path, String filename) {
        return new File(path + File.separator + filename);
    }

    @Override
    public Path getPath(String filePath, String filename) {
        Path rootLocation = Paths.get(filePath);
        return rootLocation.resolve(filename);
    }

    @Override
    public Stream<Path> getDirectoryFiles(String directoryPath) {
        Path rootLocation = Paths.get(directoryPath);
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .map(rootLocation::relativize);
        } catch (IOException e) {
            throw new CustomException("Failed to read stored files", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Resource getResource(String filePath, String filename) {
        try {
            Path path = getPath(filePath, filename);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new CustomException("Could not read file: " + filename, HttpStatus.BAD_REQUEST);
            }
        } catch (MalformedURLException e) {
            throw new CustomException("Could not read file: " + filename, HttpStatus.BAD_REQUEST);
        }
    }
}
