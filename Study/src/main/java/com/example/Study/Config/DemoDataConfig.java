package com.example.Study.Config;

import com.example.Study.Common.RoleEnum;
import com.example.Study.Common.RoomType;
import com.example.Study.Respository.ImageRepository;
import com.example.Study.Respository.RoleRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.entity.Image;
import com.example.Study.entity.Role;
import com.example.Study.entity.Room;
import com.example.Study.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
public class DemoDataConfig {
    @Bean
    CommandLineRunner demoData(RoleRepository roles, UserRepository users, RoomRepository rooms,
                               ImageRepository images, PasswordEncoder encoder) {
        return args -> {
            roles.save(Role.builder().role_name(RoleEnum.Tenant).build());
            roles.save(Role.builder().role_name(RoleEnum.Landlord).build());
            roles.save(Role.builder().role_name(RoleEnum.Admin).build());

            users.save(demoUser("tenant", "Nguyễn Minh An", "tenant@phome.vn", "0901000001", 1, encoder));
            User landlord = users.save(demoUser("landlord", "Trần Gia Huy", "landlord@phome.vn", "0901000002", 2, encoder));
            users.save(demoUser("admin", "Quản trị PHOME", "admin@phome.vn", "0901000003", 3, encoder));

            createRoom(rooms, images, landlord.getId(), "42 Nguyễn Thị Minh Khai, Quận 3, TP.HCM",
                    3.2, 28, RoomType.KHONG_CHUNG_CHU,
                    "Căn phòng nhiều ánh sáng, có ban công riêng và đầy đủ nội thất cơ bản.",
                    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267d?auto=format&fit=crop&w=1200&q=85");
            createRoom(rooms, images, landlord.getId(), "18 Phan Văn Trị, Bình Thạnh, TP.HCM",
                    2.6, 24, RoomType.CHUNG_CHU,
                    "Khu dân cư yên tĩnh, gần trường đại học và các tuyến xe buýt chính.",
                    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=85");
            createRoom(rooms, images, landlord.getId(), "75 Lê Văn Sỹ, Phú Nhuận, TP.HCM",
                    4.1, 35, RoomType.KHONG_CHUNG_CHU,
                    "Studio rộng rãi với khu bếp tách biệt, cửa sổ lớn và chỗ để xe an toàn.",
                    "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=85");
        };
    }

    private User demoUser(String username, String fullname, String email, String tel, long roleId,
                          PasswordEncoder encoder) {
        return User.builder().username(username).password(encoder.encode("Demo1234"))
                .fullname(fullname).email(email).tel(tel).role_id(roleId)
                .linkAvatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80")
                .build();
    }

    private void createRoom(RoomRepository rooms, ImageRepository images, long landlordId, String address,
                            double price, double area, RoomType type, String description, String imageUrl) {
        Room room = rooms.save(Room.builder().user_id(landlordId).address(address).price(price).area(area)
                .capacity(2).roomType(type).description(description).isApproval("true").image(imageUrl).build());
        images.save(Image.builder().room_id(room.getId()).url(imageUrl).build());
    }
}
