package com.shopmax.controller;

import com.shopmax.dto.ItemRankDto;
import com.shopmax.dto.ItemSearchDto;
import com.shopmax.dto.MainItemDto;
import com.shopmax.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
	private final ItemService itemService;
	
	@GetMapping(value = "/")
	public String main(ItemSearchDto itemSearchDto, @RequestParam(value="page") Optional<Integer> page, Model model) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
		Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
		List<ItemRankDto> itemsRank = itemService.getItemRankList();
		
		model.addAttribute("items", items);
		model.addAttribute("itemsRank", itemsRank);
		model.addAttribute("itemSearchDto", itemSearchDto); 
				
		return "main";
		
		
	}
}
