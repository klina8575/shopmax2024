package com.shopmax.controller;

import com.shopmax.dto.OrderDto;
import com.shopmax.dto.OrderHistDto;
import com.shopmax.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	//OrderDto는 ajax에서 전달해준 데이터를 받아온다,
	// http통신에서 body에 담겨져서 오므로 반드시 @RequestBody 어노테이션 추가
	@PostMapping(value = "/order")
	public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
			BindingResult bindingResult, Principal principal) {
		//Principal: 로그인한 사용자의 정보를 가져올 수 있다.

			if(bindingResult.hasErrors()) {
				StringBuilder sb = new StringBuilder();

				//유효성 체크후 에러결과를 가져온다.
				List<FieldError> fieldErrors = bindingResult.getFieldErrors();

				for (FieldError fieldError : fieldErrors) {
					sb.append(fieldError.getDefaultMessage()); //에러메세지를 합친다.
			}

			      //new ResponseEntity<첫번째 매개변수의 타입>(response 데이터, response status 코드);
			return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
		}
		
		String email = principal.getName(); //id에 해당하는 정보를 가지고 온다(email)
		Long orderId;
		
		try {
			orderId = orderService.order(orderDto, email); //주문하기
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Long>(orderId, HttpStatus.OK); //성공시
	}
	
	
	//주문내역을 보여준다
	@GetMapping(value = {"/orders", "/orders/{page}"})
	public String orderHist(@PathVariable("page") Optional<Integer> page,
			Principal principal, Model model) {
		//1. 한페이지 당 4개의 데이터를 가지고 오도록 설정
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
		
		//2. 서비스 호출
		Page<OrderHistDto> orderHistDtoList = 
				orderService.getOrderList(principal.getName(), pageable);
		
		//3. 서비스에서 가져온 값들을 view단에 model을 이용해 전송
		model.addAttribute("orders", orderHistDtoList);
		model.addAttribute("maxPage", 5); //하단에 보여줄 최대 페이지
		//model.addAttribute("page", pageable.getPageNumber()); //현재페이지
		
		return "order/orderHist";
	}
	
	//주문 취소
	@PatchMapping("/order/{orderId}/cancel")
	public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId,
			Principal principal) {
		//1. 주문취소 권한이 있는지 확인(본인확인)
		if(!orderService.validateOrder(orderId, principal.getName())) {
			return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		
		//2. 주문취소
		orderService.cancelOrder(orderId);
		
		return new ResponseEntity<Long>(orderId, HttpStatus.OK); //성공했을때
	}
	
	//주문삭제
	@DeleteMapping("/order/{orderId}/delete")
	public @ResponseBody ResponseEntity deleteOrder(@PathVariable("orderId") Long orderId
			, Principal principal) {
		//1. 본인인증
		if(!orderService.validateOrder(orderId, principal.getName())) {
			return new ResponseEntity<String>("주문 삭제 권한이 없습니다.", 
					HttpStatus.FORBIDDEN);
		}
		
		//2.주문삭제
		orderService.deleteOrder(orderId);
		
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}

}










