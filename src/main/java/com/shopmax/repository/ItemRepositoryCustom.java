package com.shopmax.repository;

import com.shopmax.dto.ItemSearchDto;
import com.shopmax.dto.MainItemDto;
import com.shopmax.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
	Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
	
	Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
