package com.flipkart.config;

import com.flipkart.model.*;
import com.flipkart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded, skipping...");
                return;
            }
            log.info("Seeding database with demo data...");

            // ─── USERS ─────────────────────────────────────────────────
            User admin = userRepository.save(User.builder()
                    .firstName("Admin").lastName("User")
                    .email("admin@flipkart.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN).emailVerified(true).build());

            User seller = userRepository.save(User.builder()
                    .firstName("Seller").lastName("Demo")
                    .email("seller@flipkart.com")
                    .password(passwordEncoder.encode("seller123"))
                    .role(User.Role.SELLER).emailVerified(true).build());

            User customer = userRepository.save(User.builder()
                    .firstName("John").lastName("Doe")
                    .email("john@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .phone("9876543210")
                    .role(User.Role.CUSTOMER).emailVerified(true).build());

            // Create carts
            cartRepository.save(Cart.builder().user(admin).build());
            cartRepository.save(Cart.builder().user(seller).build());
            cartRepository.save(Cart.builder().user(customer).build());

            // ─── CATEGORIES ────────────────────────────────────────────
            Category electronics = createCategory("Electronics", "📱", "electronics",
                    "Latest gadgets and electronics", null);
            Category mobiles = createCategory("Mobiles", "📱", "mobiles",
                    "Smartphones and accessories", electronics);
            Category laptops = createCategory("Laptops", "💻", "laptops",
                    "Laptops and computers", electronics);
            Category tv = createCategory("Televisions", "📺", "televisions",
                    "Smart TVs and displays", electronics);
            Category headphones = createCategory("Headphones", "🎧", "headphones",
                    "Audio gear", electronics);

            Category fashion = createCategory("Fashion", "👗", "fashion",
                    "Clothing, shoes & accessories", null);
            Category menFashion = createCategory("Men's Fashion", "👔", "mens-fashion",
                    "Clothing for men", fashion);
            Category womenFashion = createCategory("Women's Fashion", "👗", "womens-fashion",
                    "Clothing for women", fashion);

            Category home = createCategory("Home & Kitchen", "🏠", "home-kitchen",
                    "Home decor and kitchen appliances", null);
            Category appliances = createCategory("Appliances", "🔌", "appliances",
                    "Large and small appliances", home);

            Category books = createCategory("Books", "📚", "books",
                    "Books, e-books, and more", null);
            Category sports = createCategory("Sports & Fitness", "⚽", "sports-fitness",
                    "Sports equipment and fitness gear", null);
            Category beauty = createCategory("Beauty & Personal Care", "💄", "beauty",
                    "Skincare, makeup, haircare", null);
            Category toys = createCategory("Toys & Baby", "🧸", "toys-baby",
                    "Toys and baby products", null);

            // ─── PRODUCTS ──────────────────────────────────────────────

            // Mobiles
            createProduct("Samsung Galaxy S24 Ultra", "The ultimate Galaxy AI experience with 200MP camera, titanium design, and S Pen",
                    new BigDecimal("124999"), new BigDecimal("134999"), 50, mobiles, seller, "Samsung",
                    List.of("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=500",
                            "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500"),
                    Map.of("RAM","12GB","Storage","256GB","Battery","5000mAh","Display","6.8 inch QHD+","OS","Android 14"),
                    true, 1200);

            createProduct("Apple iPhone 15 Pro Max", "Titanium design with A17 Pro chip, 48MP main camera, USB-C",
                    new BigDecimal("159900"), new BigDecimal("169900"), 35, mobiles, seller, "Apple",
                    List.of("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=500"),
                    Map.of("RAM","8GB","Storage","256GB","Battery","4422mAh","Display","6.7 inch Super Retina XDR","OS","iOS 17"),
                    true, 2300);

            createProduct("OnePlus 12", "Hasselblad camera, Snapdragon 8 Gen 3, 100W SUPERVOOC charging",
                    new BigDecimal("64999"), new BigDecimal("74999"), 80, mobiles, seller, "OnePlus",
                    List.of("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500"),
                    Map.of("RAM","16GB","Storage","512GB","Battery","5400mAh","Display","6.82 inch LTPO AMOLED"),
                    false, 890);

            createProduct("Redmi Note 13 Pro+", "200MP camera, 120W HyperCharge, IP68 water resistance",
                    new BigDecimal("29999"), new BigDecimal("34999"), 150, mobiles, seller, "Xiaomi",
                    List.of("https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=500"),
                    Map.of("RAM","12GB","Storage","256GB","Battery","5000mAh","Display","6.67 inch AMOLED"),
                    false, 3400);

            // Laptops
            createProduct("MacBook Air M3", "Supercharged by M3 chip, 18-hour battery, Liquid Retina display",
                    new BigDecimal("114900"), new BigDecimal("124900"), 25, laptops, seller, "Apple",
                    List.of("https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=500"),
                    Map.of("Processor","Apple M3","RAM","16GB","Storage","512GB SSD","Display","15.3 inch Retina"),
                    true, 650);

            createProduct("Dell XPS 15", "Intel Core i7-13700H, RTX 4060, 4K OLED display",
                    new BigDecimal("189990"), new BigDecimal("209990"), 15, laptops, seller, "Dell",
                    List.of("https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=500"),
                    Map.of("Processor","Intel Core i7-13700H","RAM","32GB","Storage","1TB SSD","GPU","NVIDIA RTX 4060"),
                    true, 280);

            createProduct("HP Pavilion Gaming 15", "AMD Ryzen 5, GTX 1650, 144Hz display for gaming",
                    new BigDecimal("62990"), new BigDecimal("75990"), 40, laptops, seller, "HP",
                    List.of("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500"),
                    Map.of("Processor","AMD Ryzen 5 7535HS","RAM","16GB","Storage","512GB SSD","GPU","NVIDIA GTX 1650"),
                    false, 520);

            // Headphones
            createProduct("Sony WH-1000XM5", "Industry-leading noise cancelling with Speak-to-Chat",
                    new BigDecimal("24990"), new BigDecimal("29990"), 60, headphones, seller, "Sony",
                    List.of("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500"),
                    Map.of("Type","Over-ear","Connectivity","Bluetooth 5.2","Battery","30 hours","ANC","Yes"),
                    true, 1800);

            createProduct("Apple AirPods Pro 2nd Gen", "H2 chip, Adaptive Transparency, Personalized Spatial Audio",
                    new BigDecimal("24900"), new BigDecimal("26900"), 70, headphones, seller, "Apple",
                    List.of("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=500"),
                    Map.of("Type","In-ear","Connectivity","Bluetooth 5.3","Battery","6 hours + 24 case","ANC","Yes"),
                    true, 2100);

            // TV
            createProduct("Samsung 65\" 4K QLED Smart TV", "Quantum Processor 4K, 100% Colour Volume, Object Tracking Sound",
                    new BigDecimal("119900"), new BigDecimal("159900"), 20, tv, seller, "Samsung",
                    List.of("https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500"),
                    Map.of("Screen Size","65 inch","Resolution","4K UHD","Panel","QLED","Smart TV","Yes, Tizen"),
                    true, 340);

            // Fashion
            createProduct("Men's Slim Fit Shirt", "Premium cotton slim fit formal shirt for office wear",
                    new BigDecimal("899"), new BigDecimal("1499"), 200, menFashion, seller, "Peter England",
                    List.of("https://images.unsplash.com/photo-1620012253295-c15cc3e65df4?w=500"),
                    Map.of("Material","100% Cotton","Fit","Slim Fit","Sizes","S, M, L, XL, XXL"),
                    false, 5400);

            createProduct("Women's Floral Kurta Set", "Beautiful ethnic wear with dupatta and palazzo pants",
                    new BigDecimal("1299"), new BigDecimal("2499"), 150, womenFashion, seller, "Biba",
                    List.of("https://images.unsplash.com/photo-1594938298603-c8148c4b7cfa?w=500"),
                    Map.of("Material","Rayon","Occasion","Casual/Ethnic","Set Includes","Kurta, Palazzo, Dupatta"),
                    false, 3200);

            // Home
            createProduct("Instant Pot Duo 7-in-1", "Electric pressure cooker, slow cooker, rice cooker, steamer",
                    new BigDecimal("8999"), new BigDecimal("12999"), 45, appliances, seller, "Instant Pot",
                    List.of("https://images.unsplash.com/photo-1585515320310-259814833e62?w=500"),
                    Map.of("Capacity","6 Liters","Functions","7-in-1","Material","Stainless Steel"),
                    false, 980);

            // Books
            createProduct("Atomic Habits", "An Easy & Proven Way to Build Good Habits & Break Bad Ones by James Clear",
                    new BigDecimal("399"), new BigDecimal("599"), 500, books, seller, "Penguin",
                    List.of("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500"),
                    Map.of("Author","James Clear","Pages","320","Language","English","Genre","Self-Help"),
                    false, 12000);

            createProduct("The Alchemist", "A fable about following your dream by Paulo Coelho",
                    new BigDecimal("299"), new BigDecimal("450"), 300, books, seller, "HarperCollins",
                    List.of("https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500"),
                    Map.of("Author","Paulo Coelho","Pages","208","Language","English","Genre","Fiction"),
                    false, 8900);

            // Sports
            createProduct("Decathlon Kiprun Running Shoes", "Lightweight cushioned running shoe for road running",
                    new BigDecimal("3999"), new BigDecimal("5999"), 100, sports, seller, "Decathlon",
                    List.of("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500"),
                    Map.of("Material","Mesh Upper","Sole","EVA Foam","Sizes","6-12","Gender","Unisex"),
                    false, 2300);

            createProduct("Fitbit Charge 6", "Advanced health & fitness tracker with GPS and Google integration",
                    new BigDecimal("14999"), new BigDecimal("17999"), 55, sports, seller, "Fitbit",
                    List.of("https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=500"),
                    Map.of("Battery","7 days","Display","Color AMOLED","Water Resistance","50m","GPS","Yes"),
                    true, 1500);

            // Beauty
            createProduct("Lakme Absolute Matte Lipstick Set", "Set of 6 bold matte shades for all-day wear",
                    new BigDecimal("699"), new BigDecimal("1200"), 300, beauty, seller, "Lakme",
                    List.of("https://images.unsplash.com/photo-1586495777744-4e6232bf5657?w=500"),
                    Map.of("Shades","6","Finish","Matte","Longevity","12 hours","Cruelty-Free","Yes"),
                    false, 4500);

            // Toys
            createProduct("LEGO Technic Bugatti Chiron", "Advanced building set with 3,599 pieces, working 8-speed gearbox",
                    new BigDecimal("34999"), new BigDecimal("39999"), 20, toys, seller, "LEGO",
                    List.of("https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=500"),
                    Map.of("Pieces","3599","Age","16+","Scale","1:8","Dimensions","56×25×13 cm"),
                    true, 450);

            // ─── COUPONS ───────────────────────────────────────────────
            couponRepository.save(Coupon.builder()
                    .code("WELCOME10").description("10% off on your first order")
                    .discountType(Coupon.DiscountType.PERCENTAGE).discountValue(new BigDecimal("10"))
                    .minOrderAmount(new BigDecimal("500")).maxDiscountAmount(new BigDecimal("200"))
                    .validFrom(LocalDateTime.now().minusDays(1))
                    .validUntil(LocalDateTime.now().plusYears(1))
                    .usageLimit(1000).build());

            couponRepository.save(Coupon.builder()
                    .code("FLAT200").description("Flat ₹200 off on orders above ₹999")
                    .discountType(Coupon.DiscountType.FLAT).discountValue(new BigDecimal("200"))
                    .minOrderAmount(new BigDecimal("999"))
                    .validFrom(LocalDateTime.now().minusDays(1))
                    .validUntil(LocalDateTime.now().plusMonths(6))
                    .usageLimit(500).build());

            couponRepository.save(Coupon.builder()
                    .code("SAVE15").description("15% off on electronics")
                    .discountType(Coupon.DiscountType.PERCENTAGE).discountValue(new BigDecimal("15"))
                    .minOrderAmount(new BigDecimal("5000")).maxDiscountAmount(new BigDecimal("2000"))
                    .validFrom(LocalDateTime.now().minusDays(1))
                    .validUntil(LocalDateTime.now().plusMonths(3))
                    .usageLimit(200).build());

            log.info("✅ Demo data seeded successfully!");
            log.info("👤 Admin: admin@flipkart.com / admin123");
            log.info("🏪 Seller: seller@flipkart.com / seller123");
            log.info("🛒 Customer: john@example.com / user123");
            log.info("🌐 Swagger UI: http://localhost:8080/swagger-ui.html");
            log.info("🗄  H2 Console: http://localhost:8080/h2-console");
        };
    }

    private Category createCategory(String name, String icon, String slug, String desc, Category parent) {
        return categoryRepository.save(Category.builder()
                .name(name).icon(icon).slug(slug).description(desc)
                .parent(parent).active(true).build());
    }

    private Product createProduct(String name, String desc, BigDecimal price, BigDecimal originalPrice,
                                   int stock, Category category, User seller, String brand,
                                   List<String> images, Map<String, String> specs, boolean featured, int sold) {
        return productRepository.save(Product.builder()
                .name(name).description(desc).price(price).originalPrice(originalPrice)
                .stockQuantity(stock).category(category).seller(seller).brand(brand)
                .images(new ArrayList<>(images)).specifications(new HashMap<>(specs))
                .featured(featured).soldCount(sold).active(true)
                .averageRating(3.5 + Math.random() * 1.5)
                .totalReviews((int)(Math.random() * 500) + 10)
                .build());
    }
}
