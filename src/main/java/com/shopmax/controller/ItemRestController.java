package com.shopmax.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ItemRestController {
	
	/*
	private final ItemService itemService;
	
	@GetMapping(value = { "/admin/items", "/admin/items/{page}" })
	public Page<Item> itemManage(ItemSearchDto itemSearchDto, 
			@PathVariable("page") Optional<Integer> page, Model model) {
		
		//of(조회할 페이지의 번호 ★0부터 시작, 한페이지당 조회할 데이터 갯수)
		//url경로에 페이지가 있으면 해당 페이지 번호를 조회하도록 하고 페이지 번호가 없으면 0페이지를 조회
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
		
		Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
		
		return items;
	}
	*/
}
