package org.example.catch_line.booking.waiting.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.catch_line.booking.waiting.model.dto.WaitingRequest;
import org.example.catch_line.booking.waiting.model.dto.WaitingResponse;
import org.example.catch_line.booking.waiting.model.entity.WaitingType;
import org.example.catch_line.booking.waiting.service.WaitingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

class WaitingControllerTest {

	private MockMvc mockMvc;

	@Mock
	private WaitingService waitingService;

	@InjectMocks
	private WaitingController waitingController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// ViewResolver 설정 (thymeleaf 등 사용 시 필요)
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders.standaloneSetup(waitingController)
			.setViewResolvers(viewResolver)
			.build();
	}

	@Test
	@DisplayName("GET /restaurants/{restaurantId}/waiting - 웨이팅 폼 페이지로 이동")
	void testAddWaitingForm() throws Exception {
		mockMvc.perform(get("/restaurants/1/waiting"))
			.andExpect(status().isOk())
			.andExpect(view().name("waiting/waiting"));
	}

	@Test
	@DisplayName("POST /restaurants/{restaurantId}/waiting - 웨이팅 등록 테스트")
	void testAddWaitingSuccess() throws Exception {
		WaitingRequest waitingRequest = WaitingRequest.builder()
			.memberCount(3)
			.waitingType(WaitingType.DINE_IN)
			.build();
		WaitingResponse waitingResponse = WaitingResponse.builder()
			.waitingId(1L)
			.memberCount(3)
			.waitingType(WaitingType.DINE_IN)
			.build();

		when(waitingService.addWaiting(anyLong(), any(WaitingRequest.class), anyLong()))
			.thenReturn(waitingResponse);

		mockMvc.perform(post("/restaurants/1/waiting")
				.flashAttr("waitingRequest", waitingRequest)
				.sessionAttr("memberId", 1L)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/history")); // 리디렉션 URL 확인
	}

	@Test
	@DisplayName("POST /restaurants/{restaurantId}/waiting - 웨이팅 등록 예외처리 테스트")
	void testAddWaitingFailure() throws Exception {
		WaitingRequest waitingRequest = WaitingRequest.builder()
			.memberCount(3)
			.waitingType(WaitingType.DINE_IN)
			.build();

		when(waitingService.addWaiting(anyLong(), any(WaitingRequest.class), anyLong()))
			.thenThrow(new IllegalArgumentException("Waiting failed"));

		mockMvc.perform(post("/restaurants/1/waiting")
				.flashAttr("waitingRequest", waitingRequest)
				.sessionAttr("memberId", 1L)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/restaurants/1/waiting"))
			.andExpect(flash().attributeExists("error"));
	}
}