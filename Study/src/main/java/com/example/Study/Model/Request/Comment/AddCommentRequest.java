package com.example.Study.Model.Request.Comment;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentRequest {

    private String username;

    private String content;

    private String commentTime;

    private String room_id;
}
