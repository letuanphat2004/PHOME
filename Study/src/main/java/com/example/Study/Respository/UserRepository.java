package com.example.Study.Respository;

import com.example.Study.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    long countByDeleted(boolean deleted);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role_id = :roleId")
    long countByRoleId(@Param("roleId") long roleId);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.fullname = :fullname,u.tel = :tel WHERE u.username = :username")
    void updateInfoUser(String tel,String fullname, String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(String password, String username);


    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.linkAvatar = :linkAvatar WHERE u.username = :username")
    void updateAvatarUser(String linkAvatar, String username);

    List<User> findUserByUsernameLike(String username);

    Optional<User> findUserByUsername(String username);

    Page<User> findByUsernameContaining(String name, String tel, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (:name IS NULL OR u.username LIKE %:name%) AND (:tel IS NULL OR u.tel LIKE %:tel%)")
    Page<User> findByUsernameAndTel(@Param("name") String name,
                                    @Param("tel") String tel,
                                    Pageable pageable);
}
