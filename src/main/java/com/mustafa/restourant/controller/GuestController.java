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
    private final EmployeesService employeesService;
    private final int pageSize = 4;

    public GuestController(UserService userService, TableService tableService,
                           FoodService foodService, CategoryService categoryService, ReceiptService receiptService,
                           OrderService orderService, CommentService commentService, ReservationService reservationService,
                           SittingTimeService sittingTimeService, SiteCommentService siteCommentService, EmployeesService employeesService) {

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
        this.employeesService = employeesService;
    }

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/sit_table")
    public ResponseEntity<Map> sitTable(Principal auth, @RequestBody TableDTO tableDTO) {
        User user = userService.findByEmail(auth.getName());
        Tables table = tableService.findByTableName(tableDTO.getTableName());
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        if (tableService.findByUserBool(user)) {
            response.put("error", "Zaten bir masada oturuyorsunuz");
        } else {
            if (table != null) {
                if (table.getUser() != null) {
                    response.put("error", "Bu masa dolu");
                } else {
                    Date nowDate = new Date();
                    Date tenMinutesLater = new Date(nowDate.getTime() + 600000);
                    int reservationCount = reservationService.countReservationByStartTimeAndTable(nowDate, tenMinutesLater, table);
                    if (reservationCount > 0) {
                        response.put("error", "Bu masan??n 10 dakika i??erisinde rezervasyonu bulunmaktad??r.");
                    } else {
                        table.setUser(user);
                        tableService.saveTable(table);
                        Receipt receipt = new Receipt(user, 0, new Date(System.currentTimeMillis()));
                        receiptService.saveReceipt(receipt);

                        // oturma zaman?? ve say??s?? update ediliyor
                        SittingTime sittingTime = user.getSittingTime();
                        sittingTime.setStartTime(new Date());
                        sittingTime.setCount(sittingTime.getCount() + 1);
                        sittingTimeService.saveSittingTime(sittingTime);
                        response.put("status", true);
                        response.put("message", "Masaya ba??ar??yla oturdunuz");
                    }
                }
            } else {
                response.put("error", "Masa bulunamad??");
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
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);

        User user = userService.findByEmail(auth.getName());
        Receipt receipt = receiptService.findLastByUser(user);
        Tables table = tableService.findByUser(user);
        List<Food> foods = new ArrayList<>();
        int totalPrice = 0;
        if (receipt == null || table == null) {
            response.put("error", "L??tfen ??nce bir masaya oturun.");
        } else {
            // orderlar?? d??n, gerekli yeme??i getir. Listeye ekle ve toplam fiyat?? hesapla
            for (OrderDTO orderDTO : orders) {
                Food food = foodService.findById(orderDTO.getFoodId());
                foods.add(food);
                totalPrice += food.getPrice() * orderDTO.getCount();
            }
            // fiyat c??zdandan b??y??kse sipari??i olu??turma
            if (totalPrice > user.getWallet()) {
                response.put("error", "Yeterli bakiyeniz bulunmamaktad??r. L??tfen y??kleme yap??n??z.");
            } else {
                int index = 0;
                for (OrderDTO orderDTO : orders) {
                    Order order = new Order(table, foods.get(index), orderDTO.getCount(), receipt);
                    orderService.saveOrder(order);
                    index += 1;
                    // yukar??da yiyecekler listeye ??ekildi. Burada sadece s??ras??yla ula????p sipari??e ekleniyor.
                }
                receipt.setTotalPrice(receipt.getTotalPrice() + totalPrice);
                user.setWallet(user.getWallet() - totalPrice);
                userService.saveUser(user);
                receiptService.saveReceipt(receipt);
                response.put("status", true);
                response.put("message", "Tebrikler, sipari??lerinizi ald??k.");
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
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        try {
            orderService.deleteByReceipt(receipt); // fi??e g??re sipari??leri sil
            table.setUser(null);
            tableService.saveTable(table); // masay?? bo??a ????kar
            response.put("status",true);
            response.put("receipt", receipt);

            SittingTime sittingTime = user.getSittingTime();
            Date endTime = new Date();
            Date startTime = sittingTime.getStartTime();
            long difference = endTime.getTime() - startTime.getTime(); // biti?? ve ba??lang???? aras??ndaki fark
            int total = (int) TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS); // bu fark ka?? dk eder ?
            sittingTime.setEndTime(endTime);
            sittingTime.setTotalMinute(sittingTime.getTotalMinute() + total);
            sittingTimeService.saveSittingTime(sittingTime); // de??i??iklikleri sittingTime i??in update et

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "????leminiz ger??ekle??tirilemedi");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("add_money")
    public ResponseEntity<Map> addMoney(@RequestBody int money, Principal auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);

        if (money < 0 || money > 1000) {
            response.put("error", "Y??klenecek bakiye 0 ???'den k??????k veya 1000 ???'den b??y??k olamaz.");
        } else {
            User user = userService.findByEmail(auth.getName());
            user.setWallet(user.getWallet() + money);
            userService.saveUser(user);
            response.put("status", true);
            response.put("message", "Bakiye ba??ar??yla y??klendi.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("add_comment")
    public ResponseEntity<Map> addFoodComment(@RequestBody CommentDTO comment, Principal auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);

        User user = userService.findByEmail(auth.getName());
        Food food = foodService.findById(comment.getFoodId());
        String text = comment.getComment();
        if (text.length() < 10) {
            response.put("error", "Yorum en az 10 karakter olmal??d??r.");
        } else {
            try {
                FoodComment com = commentService.saveComment(new FoodComment(user, food, text));
                response.put("status",true);
                response.put("comment", com);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                response.put("status", false);
                response.put("error", "Yorum eklenemedi.");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/food/{id}")
    public Food getFood(@PathVariable int id) throws NotFoundException {
        Food food = foodService.findById(id);
        if (food != null) {
            return food;
        } else {
            throw new NotFoundException("Yiyecek bulunamad??");
        }
    }

    @PostMapping("add_reservation")
    public ResponseEntity<Map> addReservation(@Valid @RequestBody ReservationDTO reservationDTO, BindingResult result, Principal auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("status",false);
        if (result.hasErrors()) { // do??rulamada bir hata olduysa hata d??n
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Date start = reservationDTO.getStartTime();
        Date end = reservationDTO.getEndTime();
        User user = userService.findByEmail(auth.getName());
        Tables table = tableService.findById(reservationDTO.getTableId());
        int totalRes = reservationService.countUserReservations(user);
        int timeDifference = (int) (end.getTime() - start.getTime());

        if (timeDifference <= 0) { // biti?? zaman?? ba??lang????tan ??nceyse hata d??n
            response.put("error", "Biti?? zaman?? ba??lang???? zaman??ndan ??nce veya ayn?? zamanda olamaz.");
        } else if (timeDifference < 1200000 || timeDifference > 10800000) {
            response.put("error", "20 dakikadan az ve 3 saatten fazla bir rezervasyon olu??turamazs??n??z.");
        } else if (totalRes >= 3) {
            response.put("error", "3 rezervasyondan fazla yapamazs??n??z.");
        } else {
            int reservationCount = reservationService.hoveManyReservations(start, end, table);
            System.out.println(reservationCount);
            if (reservationCount > 0) { // se??ilen tarihte rezervasyon varsa hata d??n
                response.put("error", "Se??ti??iniz saatler aras??nda bir rezervasyon bulunmaktad??r.");
            } else {
                if (table == null) {
                    response.put("error", "B??yle bir masa yok.");
                } else {
                    try {
                        reservationService.saveReservation(new Reservation(user, table, start, end));
                        response.put("status", true);
                        response.put("message", "Rezervasyon ba??ar??yla eklendi");
                        return new ResponseEntity<>(response, HttpStatus.CREATED);
                    } catch (Exception e) {
                        response.put("status", false);
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
        Date endDate = new Date(startDate.getTime() + 86400000); // 1.5 g??n sonras??n??n date nesnesi
        Tables table = tableService.findById(tableId);

        return reservationService.findReservationByDate(table, startDate, endDate);
    }

    @GetMapping("find_firstreservation")
    public ResponseEntity<Map> findFirstReservation(@RequestParam int tableId) {
        Tables table = tableService.findById(tableId);
        Date date = new Date();
        Map<String, Object> response = new HashMap<>();
        Reservation reservation = reservationService.findFirstReservationByTable(table, date);
        if (reservation == null) {
            response.put("status", false);
            response.put("error", "Masaya ait bir rezervasyon bulunamad??");
        } else {
            response.put("status",true);
            response.put("reservation", reservation);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("check_reservation")
    public ResponseEntity<Map> checkReservationThenSit(Principal auth) {
        User user = userService.findByEmail(auth.getName());
        Date tenMinutesLater = new Date(new Date().getTime() + 600000);
        Reservation reservation = reservationService.findReservationNow(tenMinutesLater, user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        if (reservation != null) {
            if (reservation.getTable().getUser() != null) {
                response.put("error", "Masan??n m????terisi bulunmaktad??r. Y??netim ile ileti??ime ge??iniz ve tekrar deneyiniz.");
            } else {
                if (tableService.findByUser(user)!=null){
                    response.put("error","Zaten bir masada oturuyorsunuz. Rezervasyonunuz bulunan masaya oturamad??n??z.");
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
                    response.put("status", true);
                    response.put("message", "Rezervasyon yapt??????n??z masaya oturdunuz.");
                }
            }
        } else {
            response.put("error", "??u anda bir rezervasyonunuz bulunmuyor.");
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
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(auth.getName());
        Reservation reservation = reservationService.findById(id);
        response.put("status", false);
        if (reservation == null) {
            response.put("error", "Rezervasyon bulunamad??.");
        } else if (reservation.getUser().getId() != user.getId()) { // kullan??c?? rezervasyon sahibi de??ilse
            response.put("error", "Rezervasyon sahibi de??ilsiniz.");
        } else {
            reservationService.deleteReservationById(id);
            response.put("status", true);
            response.put("message", "Rezervasyon ba??ar??yla silindi.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add_sitecomment")
    public ResponseEntity<Map> addSiteComment(@RequestBody SiteCommentDTO comment, Principal auth){
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(auth.getName());
        if (comment.getComment().length()<10){
            response.put("status",false);
            response.put("error","Yorumunuz en az 10 karakter olmal??d??r");
        }else{
            SiteComment siteComment = new SiteComment(user,comment.getComment());
            siteCommentService.saveComment(siteComment);
            response.put("status",true);
            response.put("message","Yorum ba??ar??yla eklendi");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/tip/{id}")
    public ResponseEntity<Map> tipTheEmployee(@RequestBody Tip tip, @PathVariable int id, Principal auth){
        Map<String,Object> response = new HashMap<>();

        User user = userService.findByEmail(auth.getName());
        Employees employee = employeesService.findById(id);

        if (user.getWallet() < tip.getTip()){
            response.put("status",false);
            response.put("error","C??zdan??n??zda bu kadar para bulunmuyor, l??tfen y??kleme yap??n??z.");
        }else {
            tip.setEmployee(employee); tip.setUser(user);
            employee.setTips(tip); user.setWallet(user.getWallet()-tip.getTip());
            employeesService.saveEmployee(employee);
            userService.saveUser(user);
            response.put("status",true);
            response.put("message","Bah??i??iniz ??al????an i??in kaydedildi, te??ekk??r ederiz.");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
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

    @GetMapping("/all_employees")
    public List<Employees> getAllEmployees(){return employeesService.getAllEmployes();}
}

