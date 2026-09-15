package com.example.Study.Service.Impl;

import com.cloudinary.Cloudinary;
import com.example.Study.Service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
@Service
public class FileServiceImpl implements FileService {
    private final Cloudinary cloudinary;

    public FileServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (!cloudinaryConfigured()) {
            return saveLocally(file);
        }
        try{
            Map<?, ?> data = cloudinary.uploader().upload(file.getBytes(), Map.of("resource_type", "image"));
            Object secureUrl = data.get("secure_url");
            if (secureUrl == null) {
                throw new IllegalArgumentException("Không nhận được đường dẫn ảnh sau khi tải lên");
            }
            return secureUrl.toString();
        }catch (IOException io){
            throw new IllegalArgumentException("Không thể tải ảnh lên. Vui lòng thử lại", io);
        }
    }

    private boolean cloudinaryConfigured() {
        return hasEnvironmentValue("CLOUDINARY_CLOUD_NAME")
                && hasEnvironmentValue("CLOUDINARY_API_KEY")
                && hasEnvironmentValue("CLOUDINARY_API_SECRET");
    }

    private boolean hasEnvironmentValue(String name) {
        String value = System.getenv(name);
        return value != null && !value.isBlank();
    }

    private String saveLocally(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.lastIndexOf('.') >= 0
                ? originalName.substring(originalName.lastIndexOf('.')).replaceAll("[^A-Za-z0-9.]", "")
                : ".jpg";
        if (extension.length() > 10) extension = ".jpg";
        String filename = UUID.randomUUID() + extension.toLowerCase();
        Path uploadDirectory = Path.of(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        Path destination = uploadDirectory.resolve(filename).normalize();
        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Tên tệp ảnh không hợp lệ");
        }
        try {
            Files.createDirectories(uploadDirectory);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể lưu ảnh. Vui lòng thử lại", exception);
        }
    }

    @Override
    public void deleteFile(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        String filename = url.substring("/uploads/".length());
        Path uploadDirectory = Path.of(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        Path target = uploadDirectory.resolve(filename).normalize();
        if (!target.startsWith(uploadDirectory)) return;
        try {
            Files.deleteIfExists(target);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể xóa tệp ảnh", exception);
        }
    }
}
