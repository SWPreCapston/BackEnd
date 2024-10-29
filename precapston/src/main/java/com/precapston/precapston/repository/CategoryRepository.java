//package com.precapston.precapston.repository;
//
//import com.precapston.precapston.entity.Category;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface CategoryRepository extends JpaRepository<Category, Long> {
//    List<Category> findByCategoryName(String categoryName);
//
//}

package com.precapston.precapston.repository;

import com.precapston.precapston.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findFirstByName(String categoryName);

    default String getCategoryContent(String categoryName) {
        return findFirstByName(categoryName)
                .map(Category::getContent)
                .orElse(""); // 설명이 없으면 빈 문자열 반환
    }
}

