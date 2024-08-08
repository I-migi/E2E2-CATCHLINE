package org.example.catch_line.reservation.controller;

import java.time.LocalDateTime;

import org.example.catch_line.common.SessionUtils;
import org.example.catch_line.common.constant.Status;
import org.example.catch_line.reservation.model.dto.ReservationRequest;
import org.example.catch_line.reservation.model.dto.ReservationResponse;
import org.example.catch_line.reservation.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@GetMapping("/restaurants/{restaurantId}/reservation")
	public String addReservationForm(
		@PathVariable Long restaurantId
	) {

		return "reservation/reservation";
	}

	@PostMapping("/restaurants/{restaurantId}/reservation")
	public String addReservation(
		@PathVariable Long restaurantId,
		@RequestParam Integer memberCount,
		@RequestParam LocalDateTime reservationDate,
		Model model,
		HttpSession session,
		RedirectAttributes redirectAttributes
	) {
		try {
			Long memberId = SessionUtils.getMemberId(session);

			ReservationRequest reservationRequest = ReservationRequest.builder()
				.reservationDate(reservationDate)
				.memberCount(memberCount)
				.status(Status.SCHEDULED)
				.build();

			ReservationResponse reservationResponse = reservationService.addReserve(restaurantId,
				reservationRequest, memberId);

			model.addAttribute("reservationResponse", reservationResponse);

			return "redirect:/history";
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", "Reservation failed: " + e.getMessage());
			return "redirect:/restaurants/" + restaurantId + "/reservation";
		}
	}
}
