package com.example.Study.Model.DTO;

import com.example.Study.entity.Image;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private long id;

    private long room_id;

    private String url;

    public static ImageDTO toDto(Image image) {
        if (image == null) {
            return null;
        }
        return ImageDTO.builder()
                .id(image.getId())
                .room_id(image.getRoom_id())
                .url(image.getUrl())
                .build() ;
    }

    public static Image toImage(ImageDTO imageDto) {
        if (imageDto == null) {
            return null;
        }
        return Image.builder()
                .id(imageDto.getId())
                .room_id(imageDto.getRoom_id())
                .url(imageDto.getUrl())
                .build() ;
    }

    public static List<ImageDTO> toDto(List<Image> images) {
        return images.stream()
                .map(ImageDTO::toDto)
                .collect(Collectors.toList());
    }

    public static List<Image> toImage(List<ImageDTO> imageDtos) {
        return imageDtos.stream()
                .map(ImageDTO ::toImage)
                .collect(Collectors.toList());
    }
}
