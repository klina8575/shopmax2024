package com.shopmax.service;

import com.shopmax.dto.OrderDto;
import com.shopmax.dto.OrderHistDto;
import com.shopmax.dto.OrderItemDto;
import com.shopmax.entity.*;
import com.shopmax.repository.ItemImgRepository;
import com.shopmax.repository.ItemRepository;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final ItemImgRepository itemImgRepository;
	
	//주문하기
	public Long order(OrderDto orderDto, String email) {

		//1. 주문한 상품의 item객체를 가져온다.
		Item item = itemRepository.findById(orderDto.getItemId())
				                  .orElseThrow(EntityNotFoundException::new);

		//2. 현재 로그인한 회원의 이메일을 이용해 member 엔티티를 가져온다.
		Member member = memberRepository.findByEmail(email);

		// 양방향 관계일때 save
		List<OrderItem> orderItemList = new ArrayList<>();
		OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
		orderItemList.add(orderItem);

		//양방향이든 단방향이든 참조하는 객체를 무조건 넣은 후 save를 진행한다.
		Order order = Order.createOrder(member, orderItemList);
		orderRepository.save(order); //insert
		
		return order.getId();		
		
	}
	
	
	//주문 목록을 가져오는 서비스
	@Transactional(readOnly = true)
	public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
		//1. 유저 아이디를 이용해서 주문 목록을 조회
		List<Order> orders = orderRepository.findOrders(email, pageable);

		//2. 유저의 총 주문 개수를 구한다
		Long totalCount = orderRepository.countOrder(email);

		//주문내역은 여러개 이므로 리스트에 저장
		List<OrderHistDto> orderHistDtos = new ArrayList<>();

		//3. 주문리스트 orders를 순회하면서 컨트롤러에 전달할 DTO(OrderHistDto)를 생성
		for (Order order : orders) {
			OrderHistDto orderHistDto = new OrderHistDto(order);

			//양방향 참조를 하고 있으므로 order를 통해 orderItem을 가져온다.
			//JPA의 특성상 join을 하지 않아도 order안에 orderItem 레코드가 들어있다.
			List<OrderItem> orderItems = order.getOrderItems();

			//☆최종적으로 DTO로 만들어줘야 한다.
			for (OrderItem orderItem : orderItems) {
				//상품의 대표 이미지
				ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
				OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
				orderHistDto.addOrderItemDto(orderItemDto);
			}
			
			orderHistDtos.add(orderHistDto);
		}

		return new PageImpl<>(orderHistDtos, pageable, totalCount); //4.페이지 구현 객체를 생성하여 return
	}
	
	
	//본인확인(현재 로그인한 사용자와 주문한 사용자가 같은지 검사)
	@Transactional(readOnly = true)
	public boolean validateOrder(Long orderId, String email) {
		Member curMember = memberRepository.findByEmail(email); //로그인한 사용자 찾기
		Order order = orderRepository.findById(orderId)
				         .orElseThrow(EntityNotFoundException::new); //주문내역
		
		Member savedMember = order.getMember(); //주문한 사용자 찾기
		
		//로그인한 사용자의 이메일과 주문한 사용자의 이메일이 같은지 최종 비교
		if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
			//같지 않으면
			return false;
		}
		
		return true;		
	}
	
	//주문 취소
	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				                  .orElseThrow(EntityNotFoundException::new);
		
		//OrderStatus를 update -> entity 의 필드 값을 바꿔주면 된다.
		order.cancelOrder();
	}
	
	//주문 삭제
	public void deleteOrder(Long orderId) {
		//★delete하기 전에 select 를 한번 해준다
		//->영속성 컨텍스트에 엔티티를 저장한 후 변경 감지를 하도록 하기 위해
		Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
		
		//delete
		orderRepository.delete(order);
	}

	public Long orders(List<OrderDto> orderDtoList, String email){

		Member member = memberRepository.findByEmail(email);
		List<OrderItem> orderItemList = new ArrayList<>();

		for (OrderDto orderDto : orderDtoList) {
			Item item = itemRepository.findById(orderDto.getItemId())
					.orElseThrow(EntityNotFoundException::new);

			OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
			orderItemList.add(orderItem);
		}

		Order order = Order.createOrder(member, orderItemList);
		orderRepository.save(order);

		return order.getId();
	}
}





