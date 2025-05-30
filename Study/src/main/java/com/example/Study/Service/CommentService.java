package com.example.Study.Service;

import com.example.Study.Model.DTO.CommentDTO;
import com.example.Study.Model.Request.Comment.AddCommentRequest;
import com.example.Study.Model.Respone.Comment.AddCommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentDTO> getAllCommentsByRoom_id(long room_id);

    AddCommentResponse addComment (AddCommentRequest request);
}
