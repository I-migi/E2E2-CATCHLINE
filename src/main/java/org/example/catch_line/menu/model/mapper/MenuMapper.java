package org.example.catch_line.menu.model.mapper;

import org.example.catch_line.menu.model.dto.MenuResponse;
import org.example.catch_line.menu.model.entity.MenuEntity;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

public class MenuMapper {

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);

    public static MenuResponse entityToResponse(MenuEntity menuEntity) {
        return MenuResponse.builder()
                .menuId(menuEntity.getMenuId())
                .name(menuEntity.getName())
                .price(formatter.format(menuEntity.getPrice()))
                .build();
    }
}