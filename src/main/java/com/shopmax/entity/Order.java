package com.shopmax.entity;

import com.shopmax.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Setter
@Getter
@ToString
public class Order {
	
	@Id
	@Column(name="order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime orderDate; //주문일
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus; //주문상태
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	//order에서도 orderItem을 참조할 수 있도록 양방향 관계를 만든다.
	//다만 orderItem은 자식 테이블이 되므로 List로 만든다.
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, 
			orphanRemoval = true, fetch = FetchType.LAZY) //연관관계의 주인 설정(외래키 지정)
	private List<OrderItem> orderItems = new ArrayList<>();
	
	
	public void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
		orderItem.setOrder(this);  //★양방향 참조관계 일때는 orderItem객체에도 order 객체를 세팅한다.
	}
	
	//order 객체를 생성해준다.
	public static Order createOrder(Member member, List<OrderItem> orderItemList) {
		Order order = new Order();
		order.setMember(member);
		
		for(OrderItem orderItem : orderItemList) {
			order.addOrderItem(orderItem);
		}
		
		order.setOrderStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		
		return order;
	}
	
	//총 주문 금액
	public int getTotalPrice() {
		int totalPrice = 0;
		for(OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		return totalPrice;
	}
	
	//주문 취소
	public void cancelOrder() {
		this.orderStatus = OrderStatus.CANCEL;
		
		//재고를 원래대로 돌려 놓는다.
		for(OrderItem orderItem: orderItems) {
			orderItem.cancel();
		}
	}
	
}






