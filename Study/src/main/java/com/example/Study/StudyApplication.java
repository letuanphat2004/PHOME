package com.example.Study;

import com.example.Study.Common.RoomType;
import com.example.Study.Respository.ImageRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.entity.Image;
import com.example.Study.entity.Room;
import com.example.Study.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class StudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ImageRepository imageRepository;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void init() {
//        var u1 = User.builder()
//                .username("thanh123")
//                .fullname("Nguyễn Thị Thành")
//                .tel("0334246272")
//                .email("ntthanh@gmail.com")
//                .password(passwordEncoder.encode("thanh123"))
//                .role_id(2)
//                .build();
//        var u2 = User.builder()
//                .username("minh197")
//                .fullname("Trần Văn Minh")
//                .tel("0971131576")
//                .email("vanminh@gmail.com")
//                .password(passwordEncoder.encode("minh197"))
//                .role_id(2)
//                .build();
//        var u3 = User.builder()
//                .username("huong123")
//                .fullname("Nguyễn Thị Hương")
//                .tel("0915987675")
//                .password(passwordEncoder.encode("huong123"))
//                .role_id(2)
//                .email("nghuong@gmail.com")
//                .build();
//        var u4 = User.builder()
//                .username("nam2000")
//                .fullname("Vũ Văn Nam")
//                .tel("0934696161")
//                .password(passwordEncoder.encode("nam2000"))
//                .role_id(2)
//                .email("namvu@gmail.com")
//                .build();
//        var u5 = User.builder()
//                .username("tung1234")
//                .fullname("Nguyễn Tùng")
//                .tel("0888183239")
//                .password(passwordEncoder.encode("tung1234"))
//                .role_id(2)
//                .email("tungng@gmail.com")
//                .build();
//        u1 = userRepository.save(u1);
//        u2 = userRepository.save(u2);
//        u3 = userRepository.save(u3);
//        u4 = userRepository.save(u4);
//        u5 = userRepository.save(u5);
//        var r1 = Room.builder()
//                .user_id(u1.getId())
//                .address("Số nhà 37, ngõ 31 phố Lương Ngọc Quyến, Văn Quán, Hà Đông - HN")
//                .capacity(2)
//                .price(4.3)
//                .description("Có đồng hồ điện, nước riêng, nhà ở riêng biệt thoáng mát, " +
//                        "\ntrước cửa là một sân chơi rộng, gần nhiều trường Đại học (Kiến Trúc, SP Nghệ Thuật, Bưu Chính, Hà Nội, CN GTVT,…), " +
//                        "\nđối diện chợ Phùng Khoang 200m")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(30)
//                .isApproval("false")
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .build();
//        var r2 = Room.builder()
//                .user_id(u1.getId())
//                .address("Ngã tư Tố Hữu-Vạn Phúc-Hà Đông")
//                .capacity(2)
//                .price(2.8)
//                .description("Tầng 5/8, có 2 cửa sổ, full nội thất cơ bản")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(25)
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .isApproval("true")
//                .build();
//        var r3 = Room.builder()
//                .user_id(u2.getId())
//                .address("Ngõ 1 Trần Nhật Duật, Q.Trung, Hà Đông, Hà Nội")
//                .capacity(2)
//                .price(3.5)
//                .description("Có giường, tủ, vệ sinh khép kín. Có chỗ để xe T1. " +
//                        "\nKhu vực bếp chung ở T2, có máy giặt và khu phơi q.áo trên T5. " +
//                        "\nĐiện 3.8k/1 số. Nước + phí đổ rác 80k/tháng. \n" +
//                        "Điện các khu vực dùng chung chia bình quân. \n" +
//                        "Nhà gần chợ, gần hồ, gần BV đa khoa Hà Đông, Bưu điện Hà Đông")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(35)
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .isApproval("false")
//                .build();
//        var r4 = Room.builder()
//                .user_id(u2.getId())
//                .address("Tổ 9 -Mặt  Phố Mậu Lương, thuận tiện xe buýt, xe liên tỉnh")
//                .capacity(2)
//                .price(2.8)
//                .description("Có giường tủ, vệ sinh khép kín, nóng lạnh, điều hòa, ban công." +
//                        "\n Điện 3k/số. Nước 25k/khối")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(28)
//                .isApproval("false")
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .build();
//        var r5 = Room.builder()
//                .user_id(u3.getId())
//                .address("31, ngách 43/23 phố Văn Phú, Phú La, Hà Đông")
//                .capacity(2)
//                .price(2.2)
//                .description("Full đồ, điện nước giá dân, vệ sinh chung")
//                .roomType(RoomType.CHUNG_CHU)
//                .area(30)
//                .isApproval("false")
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .build();
//        var r6 = Room.builder()
//                .user_id(u4.getId())
//                .address("Chung cư Sông nhuệ - đối diện viện K tân triều, gần ngã ba Xa La Hà đông")
//                .capacity(4)
//                .price(5)
//                .description("Full nội thất cơ bản, điện 3,5k/số, nước 60k/người/tháng\n" +
//                        "mạng 100k/tháng, phí dịch vụ chung cư 4k/m2")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(60)
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .isApproval("true")
//                .build();
//        var r7 = Room.builder()
//                .user_id(u5.getId())
//                .address("165 Phố Văn Quán")
//                .capacity(3)
//                .price(3.6)
//                .description("Full nội thất cơ bản, vệ sinh khép kín, có gác xép")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(25)
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .isApproval("true")
//                .build();
//        var r8 = Room.builder()
//                .user_id(u5.getId())
//                .address("Văn Khê (Giữa ngõ 32A Ngô Quyền-khu cây Quýt)")
//                .capacity(4)
//                .price(4.6)
//                .description("Nội thất: VSKK, có gác xép để đồ, thang máy, PCCC\n" +
//                        "Full đồ: giường, tủ quần áo, điều hòa, nóng lạnh, tủ bếp, chạn bát, tủ lạnh\n" +
//                        "Máy giặt chung, có ban công phơi đồ riêng, Wifi\n" +
//                        " An ninh đảm bảo: cửa khóa vân tay, camera giám sát 24/7")
//                .roomType(RoomType.CHUNG_CHU)
//                .area(25)
//                .image("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//                .isApproval("true")
//                .build();
//        r1 = roomRepository.save(r1);
//        r2 = roomRepository.save(r2);
//        r3 = roomRepository.save(r3);
//        r4 = roomRepository.save(r4);
//        r5 = roomRepository.save(r5);
//        r6 = roomRepository.save(r6);
//        r7 = roomRepository.save(r7);
//        r8 = roomRepository.save(r8);
//var image1 = Image.builder()
//		.room_id(r1.getId())
//		.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//		.build();
//	var image2 = Image.builder()
//			.room_id(r1.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image3 = Image.builder()
//			.room_id(r2.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image4 = Image.builder()
//			.room_id(r2.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image5 = Image.builder()
//			.room_id(r3.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image6 = Image.builder()
//			.room_id(r3.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image7 = Image.builder()
//			.room_id(r3.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image8 = Image.builder()
//			.room_id(r3.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image9 = Image.builder()
//			.room_id(r3.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image10 = Image.builder()
//			.room_id(r4.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image11 = Image.builder()
//			.room_id(r4.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image12 = Image.builder()
//			.room_id(r4.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image13 = Image.builder()
//			.room_id(r5.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image14 = Image.builder()
//			.room_id(r5.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image15 = Image.builder()
//			.room_id(r6.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image16 = Image.builder()
//			.room_id(r6.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image17 = Image.builder()
//			.room_id(r6.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image18 = Image.builder()
//			.room_id(r6.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image19 = Image.builder()
//			.room_id(r7.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image20 = Image.builder()
//			.room_id(r8.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//	var image21 = Image.builder()
//			.room_id(r8.getId())
//			.url("https://res.cloudinary.com/phatle/image/upload/v1747009242/what-is-a-rumpus-room_wpfatc.jpg")
//			.build();
//
//        imageRepository.saveAll(new ArrayList<>(List.of(image1, image2, image3, image4, image5
//        , image6, image7, image8, image9, image10, image11, image12, image13, image14, image15
//        , image16, image17, image18, image19, image20, image21)));
//    }

//	    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ImageRepository imageRepository;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void init() {
//        var u1 = User.builder()
//                .username("thanh123")
//                .fullname("Nguyễn Thị Thành")
//                .tel("0334246272")
//                .email("ntthanh@gmail.com")
//                .password(passwordEncoder.encode("thanh123"))
//                .role_id(2)
//                .build();
//        var u2 = User.builder()
//                .username("minh197")
//                .fullname("Trần Văn Minh")
//                .tel("0971131576")
//                .email("vanminh@gmail.com")
//                .password(passwordEncoder.encode("minh197"))
//                .role_id(2)
//                .build();
//        var u3 = User.builder()
//                .username("huong123")
//                .fullname("Nguyễn Thị Hương")
//                .tel("0915987675")
//                .password(passwordEncoder.encode("huong123"))
//                .role_id(2)
//                .email("nghuong@gmail.com")
//                .build();
//        var u4 = User.builder()
//                .username("nam2000")
//                .fullname("Vũ Văn Nam")
//                .tel("0934696161")
//                .password(passwordEncoder.encode("nam2000"))
//                .role_id(2)
//                .email("namvu@gmail.com")
//                .build();
//        var u5 = User.builder()
//                .username("tung1234")
//                .fullname("Nguyễn Tùng")
//                .tel("0888183239")
//                .password(passwordEncoder.encode("tung1234"))
//                .role_id(2)
//                .email("tungng@gmail.com")
//                .build();
//        u1 = userRepository.save(u1);
//        u2 = userRepository.save(u2);
//        u3 = userRepository.save(u3);
//        u4 = userRepository.save(u4);
//        u5 = userRepository.save(u5);
//        var r1 = Room.builder()
//                .user_id(u1.getId())
//                .address("Số nhà 37, ngõ 31 phố Lương Ngọc Quyến, Văn Quán, Hà Đông - HN")
//                .capacity(2)
//                .price(4.3)
//                .description("Có đồng hồ điện, nước riêng, nhà ở riêng biệt thoáng mát, " +
//                        "\ntrước cửa là một sân chơi rộng, gần nhiều trường Đại học (Kiến Trúc, SP Nghệ Thuật, Bưu Chính, Hà Nội, CN GTVT,…), " +
//                        "\nđối diện chợ Phùng Khoang 200m")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(30)
//                .isApproval("false")
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789422/p1_wjequs.jpg")
//                .build();
//        var r2 = Room.builder()
//                .user_id(u1.getId())
//                .address("Ngã tư Tố Hữu-Vạn Phúc-Hà Đông")
//                .capacity(2)
//                .price(2.8)
//                .description("Tầng 5/8, có 2 cửa sổ, full nội thất cơ bản")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(25)
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p21_yzzmu8.jpg")
//                .isApproval("true")
//                .build();
//        var r3 = Room.builder()
//                .user_id(u2.getId())
//                .address("Ngõ 1 Trần Nhật Duật, Q.Trung, Hà Đông, Hà Nội")
//                .capacity(2)
//                .price(3.5)
//                .description("Có giường, tủ, vệ sinh khép kín. Có chỗ để xe T1. " +
//                        "\nKhu vực bếp chung ở T2, có máy giặt và khu phơi q.áo trên T5. " +
//                        "\nĐiện 3.8k/1 số. Nước + phí đổ rác 80k/tháng. \n" +
//                        "Điện các khu vực dùng chung chia bình quân. \n" +
//                        "Nhà gần chợ, gần hồ, gần BV đa khoa Hà Đông, Bưu điện Hà Đông")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(35)
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p31_hw3skv.jpg")
//                .isApproval("false")
//                .build();
//        var r4 = Room.builder()
//                .user_id(u2.getId())
//                .address("Tổ 9 -Mặt  Phố Mậu Lương, thuận tiện xe buýt, xe liên tỉnh")
//                .capacity(2)
//                .price(2.8)
//                .description("Có giường tủ, vệ sinh khép kín, nóng lạnh, điều hòa, ban công." +
//                        "\n Điện 3k/số. Nước 25k/khối")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(28)
//                .isApproval("false")
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789418/p41_zxeclf.jpg")
//                .build();
//        var r5 = Room.builder()
//                .user_id(u3.getId())
//                .address("31, ngách 43/23 phố Văn Phú, Phú La, Hà Đông")
//                .capacity(2)
//                .price(2.2)
//                .description("Full đồ, điện nước giá dân, vệ sinh chung")
//                .roomType(RoomType.CHUNG_CHU)
//                .area(30)
//                .isApproval("false")
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p51_saxw00.jpg")
//                .build();
//        var r6 = Room.builder()
//                .user_id(u4.getId())
//                .address("Chung cư Sông nhuệ - đối diện viện K tân triều, gần ngã ba Xa La Hà đông")
//                .capacity(4)
//                .price(5)
//                .description("Full nội thất cơ bản, điện 3,5k/số, nước 60k/người/tháng\n" +
//                        "mạng 100k/tháng, phí dịch vụ chung cư 4k/m2")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(60)
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p61_hkpdei.jpg")
//                .isApproval("true")
//                .build();
//        var r7 = Room.builder()
//                .user_id(u5.getId())
//                .address("165 Phố Văn Quán")
//                .capacity(3)
//                .price(3.6)
//                .description("Full nội thất cơ bản, vệ sinh khép kín, có gác xép")
//                .roomType(RoomType.KHONG_CHUNG_CHU)
//                .area(25)
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789418/p71_ll9qjj.jpg")
//                .isApproval("true")
//                .build();
//        var r8 = Room.builder()
//                .user_id(u5.getId())
//                .address("Văn Khê (Giữa ngõ 32A Ngô Quyền-khu cây Quýt)")
//                .capacity(4)
//                .price(4.6)
//                .description("Nội thất: VSKK, có gác xép để đồ, thang máy, PCCC\n" +
//                        "Full đồ: giường, tủ quần áo, điều hòa, nóng lạnh, tủ bếp, chạn bát, tủ lạnh\n" +
//                        "Máy giặt chung, có ban công phơi đồ riêng, Wifi\n" +
//                        " An ninh đảm bảo: cửa khóa vân tay, camera giám sát 24/7")
//                .roomType(RoomType.CHUNG_CHU)
//                .area(25)
//                .image("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p81_end9iz.jpg")
//                .isApproval("true")
//                .build();
//        r1 = roomRepository.save(r1);
//        r2 = roomRepository.save(r2);
//        r3 = roomRepository.save(r3);
//        r4 = roomRepository.save(r4);
//        r5 = roomRepository.save(r5);
//        r6 = roomRepository.save(r6);
//        r7 = roomRepository.save(r7);
//        r8 = roomRepository.save(r8);
//
//        var image1 = Image.builder()
//                .room_id(r1.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789422/p2_jenfgx.jpg")
//                .build();
//        var image2 = Image.builder()
//                .room_id(r1.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789422/p1_wjequs.jpg")
//                .build();
//        var image3 = Image.builder()
//                .room_id(r2.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p21_yzzmu8.jpg")
//                .build();
//        var image4 = Image.builder()
//                .room_id(r2.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789422/p22_srp1wy.jpg")
//                .build();
//        var image5 = Image.builder()
//                .room_id(r3.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789422/p2_jenfgx.jpg")
//                .build();
//        var image6 = Image.builder()
//                .room_id(r3.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p31_hw3skv.jpg")
//                .build();
//        var image7 = Image.builder()
//                .room_id(r3.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789420/p32_x9uzdw.jpg")
//                .build();
//        var image8 = Image.builder()
//                .room_id(r3.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p33_jyhelj.jpg")
//                .build();
//        var image9 = Image.builder()
//                .room_id(r3.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p34_dj2ge0.jpg")
//                .build();
//        var image10 = Image.builder()
//                .room_id(r4.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789418/p41_zxeclf.jpg")
//                .build();
//        var image11 = Image.builder()
//                .room_id(r4.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789418/p42_ctux1l.jpg")
//                .build();
//        var image12 = Image.builder()
//                .room_id(r4.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p43_qjakee.jpg")
//                .build();
//        var image13 = Image.builder()
//                .room_id(r5.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789421/p51_saxw00.jpg")
//                .build();
//        var image14 = Image.builder()
//                .room_id(r5.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789420/p52_o6wzf5.jpg")
//                .build();
//        var image15 = Image.builder()
//                .room_id(r6.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p61_hkpdei.jpg")
//                .build();
//        var image16 = Image.builder()
//                .room_id(r6.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789420/p62_sz8hel.jpg")
//                .build();
//        var image17 = Image.builder()
//                .room_id(r6.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789420/p63_akdpbc.jpg")
//                .build();
//        var image18 = Image.builder()
//                .room_id(r6.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p64_sszw6f.jpg")
//                .build();
//        var image19 = Image.builder()
//                .room_id(r7.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789418/p71_ll9qjj.jpg")
//                .build();
//        var image20 = Image.builder()
//                .room_id(r8.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p81_end9iz.jpg")
//                .build();
//        var image21 = Image.builder()
//                .room_id(r8.getId())
//                .url("https://res.cloudinary.com/hoaptit/image/upload/v1730789419/p82_ettd8o.jpg")
//                .build();
//        imageRepository.saveAll(new ArrayList<>(List.of(image1, image2, image3, image4, image5
//        , image6, image7, image8, image9, image10, image11, image12, image13, image14, image15
//        , image16, image17, image18, image19, image20, image21)));
//    }

}
