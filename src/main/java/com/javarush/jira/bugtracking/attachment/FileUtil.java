package com.javarush.jira.bugtracking.attachment;

import com.javarush.jira.common.error.IllegalRequestDataException;
import com.javarush.jira.common.error.NotFoundException;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@UtilityClass
public class FileUtil {
    private static final String ATTACHMENT_PATH = "./attachments/%s/";

    public static void upload(MultipartFile multipartFile, String directoryPath, String fileName) {
        if (multipartFile.isEmpty()) {
            throw new IllegalRequestDataException("Select a file to upload.");
        }
        Path dirPath = Path.of(directoryPath);
        try {
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(fileName);
            try {
                Files.write(filePath, multipartFile.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                throw new IllegalRequestDataException("Failed to upload file" + multipartFile.getOriginalFilename());
            }
        } catch (IOException e) {
            throw new IllegalRequestDataException("Failed to create directory" + directoryPath);
        }
    }

    public static Resource download(String fileLink) {
        Path path = Path.of(fileLink);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalRequestDataException("Failed to download file " + resource.getFilename());
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File" + fileLink + " not found");
        }
    }

    public static void delete(String fileLink) {
        Path path = Path.of(fileLink);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new IllegalRequestDataException("File" + fileLink + " deletion failed.");
        }
    }

    public static String getPath(String titleType) {
        return String.format(ATTACHMENT_PATH, titleType.toLowerCase());
    }
}
