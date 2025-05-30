package com.example.Study.Respository;

import com.example.Study.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment AS c " +
            "WHERE c.room_id = :room_id " +
            "ORDER BY c.commentTime desc")
    List<Comment> getCommentsByRoom_id(@Param("room_id") long room_id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.room_id = :room_id")
    void deleteCommentsByRoom_id(@Param("room_id") long room_id);
}
