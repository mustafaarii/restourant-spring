package com.mustafa.restourant.controller;

import com.mustafa.restourant.dto.CategoryDTO;
import com.mustafa.restourant.dto.FoodDTO;
import com.mustafa.restourant.dto.TableDTO;
import com.mustafa.restourant.entity.*;
import com.mustafa.restourant.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TableService tableService;
    private final UserService userService;
    private final FoodService foodService;
    private final FilesStorageService filesStorageService;
    private final CategoryService categoryService;
    private final SiteCommentService siteCommentService;

    public AdminController(TableService tableService, UserService userService, FoodService foodService,
                           FilesStorageService filesStorageService, CategoryService categoryService, SiteCommentService siteCommentService) {
        this.tableService = tableService;
        this.userService = userService;
        this.foodService = foodService;
        this.filesStorageService = filesStorageService;
        this.categoryService = categoryService;
        this.siteCommentService = siteCommentService;
    }

    @GetMapping("/allusers")
    public List<User> getUsers(){
        return userService.allUsers();
    }


    @PostMapping("/add_table")
    public ResponseEntity<Map> createTable(@Valid @RequestBody TableDTO tableDTO, BindingResult bindingResult){
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()){
            List<String> errors = new ArrayList<>();
            for (FieldError e : bindingResult.getFieldErrors()){
                errors.add(e.getDefaultMessage());
            }
            response.put("errors",errors);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else{
            Tables isExist = tableService.findByTableName(tableDTO.getTableName());
            if (isExist!=null){
                response.put("status","false");
                response.put("error","Böyle bir masa zaten var.");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                Tables table = new Tables(tableDTO.getTableName());
               Tables newTable =  tableService.saveTable(table);
                response.put("status","true");
                response.put("table",newTable);
                return new ResponseEntity<>(response,HttpStatus.CREATED);
            }

        }
    }

    @PostMapping(value = "/add_category")
    public ResponseEntity<Map> addCategory(@Valid @RequestBody CategoryDTO categoryDTO,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            Map<String, List<String>> response = new HashMap<>();
            List<String> errors = new ArrayList<>();
            for (FieldError e : bindingResult.getFieldErrors()){
                errors.add(e.getDefaultMessage());
            }
            response.put("errors",errors);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        Map<String,String> response = new HashMap<>();
        if (categoryService.isExistCategoryByName(categoryDTO.getName())){
            response.put("status","false");
            response.put("error","Böyle bir kategori zaten var.");
        }else {
            Category category = new Category(categoryDTO.getName());
            categoryService.saveCategory(category);
            response.put("status","true");
            response.put("message","Kategori başarıyla kaydedildi.");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete_table/{id}")
    public ResponseEntity<Map> deleteTable(@PathVariable int id){
        tableService.deleteTable(id);
        Map<String,String> response = new HashMap<>();
        response.put("status","true");
        response.put("message","Masa başarıyla silindi");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    @DeleteMapping("/delete_category/{id}")
    public ResponseEntity<Map> deleteCategory(@PathVariable int id){
        categoryService.deleteCategory(id);
        Map<String,String> response = new HashMap<>();
        response.put("status","true");
        response.put("message","Kategori başarıyla silindi");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PostMapping(value = "/add_food",consumes = {
            MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<Map> addFood(@Valid @RequestPart("food") FoodDTO foodDTO,BindingResult bindingResult, @RequestPart("file") MultipartFile file) {
        if (bindingResult.hasErrors()){
            Map<String, List<String>> response = new HashMap<>();
            List<String> errors = new ArrayList<>();
            for (FieldError e : bindingResult.getFieldErrors()){
                errors.add(e.getDefaultMessage());
            }
            response.put("errors",errors);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        Map<String,String> response = new HashMap<>();

        if (foodService.findByFoodName(foodDTO.getFoodName())!=null) {
            response.put("status","false");
            response.put("error","Bu yemek daha önce eklenmiş.");
        }else {
          Category foodCategory = categoryService.findById(foodDTO.getCategory());
          if (foodCategory==null){
              response.put("status","false");
              response.put("error","Böyle bir kategori yok.");
          }else {
              // resmi yükleme
              try {
                  String originalName = file.getOriginalFilename();
                  String[] fileSplit = originalName.split("\\."); //uzantı parçalama
                  System.out.println(fileSplit[1]);
                  if (fileSplit[1].equals("png") || fileSplit[1].equals("jpg")){
                      String fileName = UUID.randomUUID().toString()+"."+fileSplit[1]; //uzantı ile beraber rand isim
                      filesStorageService.save(file,fileName); //resmi kaydet
                      Food food = new Food(foodDTO.getFoodName(),foodDTO.getPrice(),fileName,foodCategory); //yemek oluştur
                      foodService.saveFood(food);
                      response.put("status","true");
                      response.put("message","Yemek başarıyla eklendi.");
                      return new ResponseEntity<>(response,HttpStatus.CREATED);
                  }else {
                      response.put("status","false");
                      response.put("error","Dosyanız png veya jpg uzantılı olmalıdır.");
                  }

              } catch (Exception e) {
                  response.put("status","false");
                  response.put("error","Dosya yüklenemedi.");
              }
          }
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete_food/{id}")
    public ResponseEntity<Map> deleteFood(@PathVariable int id) throws IOException {
        Food food = foodService.findById(id);
        foodService.deleteFood(food.getId());
        deleteFile(food.getImage());
        Map<String,String> response = new HashMap<>();

        response.put("status","true");
        response.put("message","Yemek başarıyla silindi.");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    private void deleteFile(String path) throws IOException {
        Path deleteFilePath = Paths.get("uploads/"+path);
        Files.delete(deleteFilePath);
    }

    @GetMapping("/all_sitecomments")
    public Page<SiteComment> allComments(Pageable pageable){
        return siteCommentService.allComments(pageable.getPageNumber(),4);
    }

    @GetMapping("/delete_sitecomment")
    public ResponseEntity<Map> deleteComment(@RequestParam int id){
        Map<String,String> response = new HashMap<>();
        try{
            siteCommentService.deleteCommentById(id);
            response.put("status","true");
            response.put("message","Yorum başarıyla silindi.");
        }catch (Exception e){
            response.put("status","false");
            response.put("error","Yorum silinemedi.");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
