package com.example.Study.Controller;


import com.example.Study.Model.Request.Comment.AddCommentRequest;
import com.example.Study.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public String addComment (@ModelAttribute AddCommentRequest request) {
        commentService.addComment(request);
        return "redirect:/api/room?room_id="+request.getRoom_id();
    }
}