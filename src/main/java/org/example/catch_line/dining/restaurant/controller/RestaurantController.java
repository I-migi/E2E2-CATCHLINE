package org.example.catch_line.dining.restaurant.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.catch_line.common.session.SessionConst;
import org.example.catch_line.common.kakao.model.dto.KakaoAddressResponse;
import org.example.catch_line.common.kakao.service.KakaoAddressService;
import org.example.catch_line.dining.restaurant.model.dto.RestaurantHourResponse;
import org.example.catch_line.dining.restaurant.model.dto.RestaurantResponse;
import org.example.catch_line.dining.restaurant.model.entity.RestaurantImageEntity;
import org.example.catch_line.dining.restaurant.service.RestaurantImageService;
import org.example.catch_line.dining.restaurant.service.RestaurantService;
import org.example.catch_line.common.constant.DayOfWeeks;
import org.example.catch_line.dining.restaurant.service.RestaurantHourService;
import org.example.catch_line.scrap.service.ScrapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantHourService restaurantHourService;
    private final RestaurantImageService restaurantImageService;
    private final KakaoAddressService kakaoAddressService;
    private final ScrapService scrapService;

    @Value("${kakao.maps.js-key}")
    private String jsKey;

    @GetMapping("/{restaurantId}")
    public String viewRestaurant(
            @PathVariable Long restaurantId,
            Model model,
            HttpSession session
    ) {
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        DayOfWeeks dayOfWeek = DayOfWeeks.from(currentDayOfWeek);

        Long memberId = (Long) session.getAttribute(SessionConst.MEMBER_ID);
        RestaurantResponse restaurant = restaurantService.findRestaurant(memberId, restaurantId);
        List<RestaurantHourResponse> restaurantHours = restaurantHourService.getAllRestaurantHours(restaurantId);
        RestaurantHourResponse hourResponse = restaurantHourService.getRestaurantHour(restaurantId, dayOfWeek);

        String x = String.valueOf(restaurant.getLongitude()); // 경도 == x 좌표
        String y = String.valueOf(restaurant.getLatitude()); // 위도 == y 좌표

        KakaoAddressResponse kakaoAddressResponse = kakaoAddressService.coordinateToAddress(x, y);
        KakaoAddressResponse.Document document = kakaoAddressResponse.getDocuments().get(0);

        List<RestaurantImageEntity> imageList = restaurantImageService.getImageList(restaurantId);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("restaurantHours", restaurantHours);
        model.addAttribute("hourResponse", hourResponse);
        model.addAttribute("imageList", imageList);
        model.addAttribute("document", document);
        model.addAttribute("jsKey", jsKey);
        model.addAttribute("dayOfWeek", dayOfWeek.getDescription());
        return "restaurant/restaurant";
    }

}