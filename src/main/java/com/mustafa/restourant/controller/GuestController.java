package com.mustafa.restourant.controller;

import com.mustafa.restourant.dto.*;
import com.mustafa.restourant.entity.*;
import com.mustafa.restourant.service.*;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class GuestController {

    private final UserService userService;
    private final TableService tableService;
    private final FoodService foodService;
    private final CategoryService categoryService;
    private final ReceiptService receiptService;
    private final OrderService orderService;
    private final CommentService commentService;
    private final ReservationService reservationService;
    private final SittingTimeService sittingTimeService;
    private final SiteCommentService siteCommentService;
    private final int pageSize = 4;

    public GuestController(UserService userService, TableService tableService,
                           FoodService foodService, CategoryService categoryService, ReceiptService receiptService,
                           OrderService orderService, CommentService commentService, ReservationService reservationService,
                           SittingTimeService sittingTimeService, SiteCommentService siteCommentService) {

        this.userService = userService;
        this.tableService = tableService;
        this.foodService = foodService;
        this.categoryService = categoryService;
        this.receiptService = receiptService;
        this.orderService = orderService;
        this.commentService = commentService;
        this.reservationService = reservationService;
        this.sittingTimeService = sittingTimeService;
        this.siteCommentService = siteCommentService;
    }

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/sit_table")
    public ResponseEntity<Map> sitTable(Principal auth, @RequestBody TableDTO tableDTO) {
        User user = userService.findByEmail(auth.getName());
        Tables table = tableService.findByTableName(tableDTO.getTableName());
        Map<String, String> response = new HashMap<>();
        if (tableService.findByUserBool(user)) {
            response.put("status", "false");
            response.put("error", "Zaten bir masada oturuyorsunuz");
        } else {
            if (table != null) {
                if (table.getUser() != null) {
                    response.put("status", "false");
                    response.put("error", "Bu masa dolu");
                } else {
                    Date nowDate = new Date();
                    Date tenMinutesLater = new Date(nowDate.getTime() + 600000);
                    int reservationCount = reservationService.countReservationByStartTimeAndTable(nowDate, tenMinutesLater, table);
                    if (reservationCount > 0) {
                        response.put("status", "false");
                        response.put("error", "Bu masanın 10 dakika içerisinde rezervasyonu bulunmaktadır.");
                    } else {
                        table.setUser(user);
                        tableService.saveTable(table);
                        Receipt receipt = new Receipt(user, 0, new Date(System.currentTimeMillis()));
                        receiptService.saveReceipt(receipt);

                        // oturma zamanı ve sayısı update ediliyor
                        SittingTime sittingTime = user.getSittingTime();
                        sittingTime.setStartTime(new Date());
                        sittingTime.setCount(sittingTime.getCount() + 1);
                        sittingTimeService.saveSittingTime(sittingTime);

                        response.put("status", "true");
                        response.put("message", "Masaya başarıyla oturdunuz");
                    }
                }
            } else {
                response.put("status", "false");
                response.put("error", "Masa bulunamadı");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/is_sitting")
    public ResponseEntity<Integer> isSitting(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        if (tableService.findByUserBool(user)) {
            int receiptId = receiptService.findLastByUser(user).getId();
            return new ResponseEntity<>(receiptId, HttpStatus.OK);
        } else return ResponseEntity.status(404).build();
    }

    @GetMapping("/auth")
    public ResponseEntity<User> authInfos(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/to_order")
    public ResponseEntity<Map> toOrder(@RequestBody List<OrderDTO> orders, Principal auth) {
        Map<String, String> response = new HashMap<>();

        User user = userService.findByEmail(auth.getName());
        Receipt receipt = receiptService.findLastByUser(user);
        Tables table = tableService.findByUser(user);
        List<Food> foods = new ArrayList<>();
        int totalPrice = 0;
        if (receipt == null || table == null) {
            response.put("status", "false");
            response.put("error", "Lütfen önce bir masaya oturun.");
        } else {
            // orderları dön, gerekli yemeği getir. Listeye ekle ve toplam fiyatı hesapla
            for (OrderDTO orderDTO : orders) {
                Food food = foodService.findById(orderDTO.getFoodId());
                foods.add(food);
                totalPrice += food.getPrice() * orderDTO.getCount();
            }
            // fiyat cüzdandan büyükse siparişi oluşturma
            if (totalPrice > user.getWallet()) {
                response.put("status", "false");
                response.put("error", "Yeterli bakiyeniz bulunmamaktadır. Lütfen yükleme yapınız.");
            } else {
                int index = 0;
                for (OrderDTO orderDTO : orders) {
                    Order order = new Order(table, foods.get(index), orderDTO.getCount(), receipt);
                    orderService.saveOrder(order);
                    index += 1;
                    // yukarıda yiyecekler listeye çekildi. Burada sadece sırasıyla ulaşıp siparişe ekleniyor.
                }
                receipt.setTotalPrice(receipt.getTotalPrice() + totalPrice);
                user.setWallet(user.getWallet() - totalPrice);
                userService.saveUser(user);
                receiptService.saveReceipt(receipt);
                response.put("status", "true");
                response.put("message", "Tebrikler, siparişlerinizi aldık.");
            }

        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get_orders")
    public List<Order> getOrders(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        Tables table = tableService.findByUser(user);
        List<Order> orders = orderService.findByTable(table);
        return orders;
    }

    @GetMapping("getoff_thetable")
    public ResponseEntity<Map> getOffTheTable(Principal auth) {

        User user = userService.findByEmail(auth.getName());
        Tables table = tableService.findByUser(user);
        Receipt receipt = receiptService.findLastByUser(user);
        Map<String, Receipt> response = new HashMap<>();
        try {
            orderService.deleteByReceipt(receipt); // fişe göre siparişleri sil
            table.setUser(null);
            tableService.saveTable(table); // masayı boşa çıkar
            response.put("receipt", receipt);

            SittingTime sittingTime = user.getSittingTime();
            Date endTime = new Date();
            Date startTime = sittingTime.getStartTime();
            long difference = endTime.getTime() - startTime.getTime(); // bitiş ve başlangıç arasındaki fark
            int total = (int) TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS); // bu fark kaç dk eder ?
            sittingTime.setEndTime(endTime);
            sittingTime.setTotalMinute(sittingTime.getTotalMinute() + total);
            sittingTimeService.saveSittingTime(sittingTime); // değişiklikleri sittingTime için update et

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("status", "false");
            error.put("error", "İşleminiz gerçekleştirilemedi");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("add_money")
    public ResponseEntity<Map> addMoney(@RequestBody int money, Principal auth) {
        Map<String, String> response = new HashMap<>();

        if (money < 0 || money > 1000) {
            response.put("status", "false");
            response.put("error", "Yüklenecek bakiye 0 ₺'den küçük veya 1000 ₺'den büyük olamaz.");
        } else {
            User user = userService.findByEmail(auth.getName());
            user.setWallet(user.getWallet() + money);
            userService.saveUser(user);
            response.put("status", "true");
            response.put("message", "Bakiye başarıyla yüklendi.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("add_comment")
    public ResponseEntity<Map> addFoodComment(@RequestBody CommentDTO comment, Principal auth) {
        Map<String, String> response = new HashMap<>();
        User user = userService.findByEmail(auth.getName());
        Food food = foodService.findById(comment.getFoodId());
        String text = comment.getComment();
        System.out.println(comment);
        if (text.length() < 10) {
            response.put("status", "false");
            response.put("error", "Yorum en az 10 karakter olmalıdır.");
        } else {
            try {
                FoodComment com = commentService.saveComment(new FoodComment(user, food, text));
                Map<String, FoodComment> res = new HashMap<>();
                res.put("comment", com);
                return new ResponseEntity<>(res, HttpStatus.OK);
            } catch (Exception e) {
                response.put("status", "false");
                response.put("error", "Yorum eklenemedi.");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add_sitecomment")
    public ResponseEntity<Map> addSiteComment(@RequestBody SiteCommentDTO comment, Principal auth){
        Map<String, String> response = new HashMap<>();
        User user = userService.findByEmail(auth.getName());
        if (comment.getComment().length()<10){
            response.put("status","false");
            response.put("error","Yorumunuz en az 10 karakter olmalıdır");
        }else{
            SiteComment siteComment = new SiteComment(user,comment.getComment());
            siteCommentService.saveComment(siteComment);
            response.put("status","true");
            response.put("message","Yorum başarıyla eklendi");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/food/{id}")
    public Food getFood(@PathVariable int id) throws NotFoundException {
        Food food = foodService.findById(id);
        if (food != null) {
            return food;
        } else {
            throw new NotFoundException("Yiyecek bulunamadı");
        }
    }

    @PostMapping("add_reservation")
    public ResponseEntity<Map> addReservation(@Valid @RequestBody ReservationDTO reservationDTO, BindingResult result, Principal auth) {
        if (result.hasErrors()) { // doğrulamada bir hata olduysa hata dön
            Map<String, List<String>> response = new HashMap<>();
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Map<String, String> response = new HashMap<>();
        Date start = reservationDTO.getStartTime();
        Date end = reservationDTO.getEndTime();
        User user = userService.findByEmail(auth.getName());
        Tables table = tableService.findById(reservationDTO.getTableId());
        int totalRes = reservationService.countUserReservations(user);
        int timeDifference = (int) (end.getTime() - start.getTime());

        if (timeDifference <= 0) { // bitiş zamanı başlangıçtan önceyse hata dön
            response.put("status", "false");
            response.put("error", "Bitiş zamanı başlangıç zamanından önce veya aynı zamanda olamaz.");
        } else if (timeDifference < 1200000 || timeDifference > 10800000) {
            response.put("status", "false");
            response.put("error", "20 dakikadan az ve 3 saatten fazla bir rezervasyon oluşturamazsınız.");
        } else if (totalRes >= 3) {
            response.put("status", "false");
            response.put("error", "3 rezervasyondan fazla yapamazsınız.");
        } else {
            int reservationCount = reservationService.hoveManyReservations(start, end, table);
            System.out.println(reservationCount);
            if (reservationCount > 0) { // seçilen tarihte rezervasyon varsa hata dön
                response.put("status", "false");
                response.put("error", "Seçtiğiniz saatler arasında bir rezervasyon bulunmaktadır.");
            } else {

                if (table == null) {
                    response.put("status", "false");
                    response.put("error", "Böyle bir masa yok.");
                } else {
                    try {
                        reservationService.saveReservation(new Reservation(user, table, start, end));
                        response.put("status", "true");
                        response.put("message", "Rezervasyon başarıyla eklendi");
                        return new ResponseEntity<>(response, HttpStatus.CREATED);
                    } catch (Exception e) {
                        response.put("status", "false");
                        response.put("error", "Rezervasyon eklenemedi. Daha sonra tekrar deneyin");
                    }
                }
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search_reservation")
    public List<Reservation> searchReservation(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime, @RequestParam int tableId) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = format.parse(format.format(startTime));
        Date endDate = new Date(startDate.getTime() + 86400000); // 1.5 gün sonrasının date nesnesi
        Tables table = tableService.findById(tableId);

        return reservationService.findReservationByDate(table, startDate, endDate);
    }

    @GetMapping("find_firstreservation")
    public ResponseEntity<Map> findFirstReservation(@RequestParam int tableId) {
        Tables table = tableService.findById(tableId);
        Date date = new Date();
        Map<String, String> response = new HashMap<>();
        Reservation reservation = reservationService.findFirstReservationByTable(table, date);
        if (reservation == null) {
            response.put("status", "false");
            response.put("error", "Masaya ait bir rezervasyon bulunamadı");
        } else {
            Map<String, Reservation> success = new HashMap<>();
            success.put("reservation", reservation);
            return new ResponseEntity<>(success, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("check_reservation")
    public ResponseEntity<Map> checkReservationThenSit(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        Date tenMinutesLater = new Date(new Date().getTime() + 600000);
        Reservation reservation = reservationService.findReservationNow(tenMinutesLater, user);
        Map<String, String> response = new HashMap<>();

        if (reservation != null) {
            if (reservation.getTable().getUser() != null) {
                response.put("status", "false");
                response.put("error", "Masanın müşterisi bulunmaktadır. Yönetim ile iletişime geçiniz ve tekrar deneyiniz.");
            } else {
                if (tableService.findByUser(user)!=null){
                    response.put("status","false");
                    response.put("error","Zaten bir masada oturuyorsunuz. Rezervasyonunuz bulunan masaya oturamadınız.");
                }else{
                    Tables table = tableService.findById(reservation.getTable().getId());
                    table.setUser(user);
                    tableService.saveTable(table);
                    Receipt receipt = new Receipt(user, 0, new Date(System.currentTimeMillis()));
                    receiptService.saveReceipt(receipt);
                    reservationService.deleteReservationById(reservation.getId());

                    SittingTime sittingTime = user.getSittingTime();
                    sittingTime.setStartTime(new Date());
                    sittingTime.setCount(sittingTime.getCount()+1);
                    response.put("status", "true");
                    response.put("message", "Rezervasyon yaptığınız masaya oturdunuz.");
                }
            }
        } else {
            response.put("status", "false");
            response.put("error", "Şu anda bir rezervasyonunuz bulunmuyor.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my_reservations")
    public List<ReservationDTO2> myReservations(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        List<Reservation> myReservations = user.getReservations();
        return myReservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDTO2.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/delete_old_reservations")
    public ResponseEntity deleteOldReservations(Principal auth) throws NotFoundException {
        User user = userService.findByEmail(auth.getName());
        try {
            reservationService.deleteOldReservations(user);
        } catch (Exception e) {
            throw new NotFoundException("Eski rezervasyonlar silinemedi.");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete_reservation")
    public ResponseEntity<Map> deleteReservation(Principal auth, @RequestParam int id) {
        Map<String, String> response = new HashMap<>();
        User user = userService.findByEmail(auth.getName());
        Reservation reservation = reservationService.findById(id);
        response.put("status", "false");
        if (reservation == null) {
            response.put("error", "Rezervasyon bulunamadı.");
        } else if (reservation.getUser().getId() != user.getId()) { // kullanıcı rezervasyon sahibi değilse
            response.put("error", "Rezervasyon sahibi değilsiniz.");
        } else {
            reservationService.deleteReservationById(id);
            response.put("status", "true");
            response.put("message", "Rezervasyon başarıyla silindi.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get_sittingtime")
    public SittingTime getSittingTime(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        return user.getSittingTime();
    }

    @GetMapping("/search_food")
    public Page<Food> searchFood(@RequestParam String s, Pageable page) {
        return foodService.findBySearch(s, page.getPageNumber(), pageSize);
    }

    @GetMapping("/all_foods")
    public Page<Food> allFoods(Pageable page) {
        return foodService.allFoods(page.getPageNumber(), pageSize);
    }

    @GetMapping("/all_categories")
    public List<Category> allCategories() {
        return categoryService.allCategories();
    }

    @GetMapping("/foods/{id}")
    public Page<Food> foodsByCategory(@PathVariable int id, Pageable page) {
        return foodService.findByCategory(id, page.getPageNumber(), pageSize);
    }

    @GetMapping("comments/{id}")
    public Page<FoodComment> foodComments(@PathVariable int id, Pageable page) {
        Food food = foodService.findById(id);
        return commentService.findByFood(food, page.getPageNumber(), pageSize);
    }

    @GetMapping("all_comments")
    public List<FoodComment> allComments() {
        return commentService.allComments();
    }

    @GetMapping("/my_receipts")
    public Page<Receipt> myReceipts(Principal auth, Pageable page) {
        User user = userService.findByEmail(auth.getName());
        return receiptService.findByUser(user, page.getPageNumber(), 10);
    }

    @GetMapping("/tables")
    public Page<Tables> getTables(Pageable page) {
        return tableService.getTables(page.getPageNumber(), pageSize);
    }

    @GetMapping("all_tables")
    public List<Tables> getAllTables() {
        return tableService.getAllTables();
    }
}

