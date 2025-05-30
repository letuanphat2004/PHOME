package com.example.Study.Model.DTO;

import com.example.Study.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDTO {
    private long id;

    private String username;

    private String avatar;

    private String content;

    private String commentTime;

    private long room_id;
    public static CommentDTO toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var commentDto = CommentDTO.builder()
                .id(comment.getId())
                .room_id(comment.getRoom_id())
                .commentTime(formatter.format(comment.getCommentTime()))
                .content(comment.getContent())
                .username(comment.getUsername())
                .avatar(comment.getAvatar())
                .build();
        return commentDto;
    }

    public static Comment toComment(CommentDTO commentDto) {
        if (commentDto == null) {
            return null;
        }
        var comment = Comment.builder()
                .id(commentDto.getId())
                .room_id(commentDto.getRoom_id())
                .commentTime(LocalDateTime.parse(commentDto.getCommentTime()))
                .content(commentDto.getContent())
                .username(commentDto.getUsername())
                .avatar(commentDto.getAvatar())
                .build();
        return comment;
    }

    public static List<CommentDTO> toDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentDTO::toDto)
                .collect(Collectors.toList());
    }

    public static List<Comment> toComment(List<CommentDTO> commentDtos) {
        return commentDtos.stream()
                .map(CommentDTO::toComment)
                .collect(Collectors.toList());
    }
}
