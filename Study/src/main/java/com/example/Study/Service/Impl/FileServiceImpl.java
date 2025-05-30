package com.example.Study.Service.Impl;

import com.cloudinary.Cloudinary;
import com.example.Study.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try{
            Map data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());
            String url = (String) data.getOrDefault("url", null);
            return url;
        }catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }
}
