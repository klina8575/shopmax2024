package com.shopmax.repository;

import com.shopmax.constant.ItemSellStatus;
import com.shopmax.dto.ItemRankDto;
import com.shopmax.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
                               //<해당 repository에서 사용할 Entity, Entity클래스의 기본키 타입>
public interface ItemRepository extends JpaRepository<Item, Long>,
                                        ItemRepositoryCustom{
	//select * from item where item_nm = ?
	List<Item> findByItemNm(String itemNm);
	
	//select * from item where item_nm = ? and item_sell_status = ?
	List<Item> findByItemNmAndItemSellStatus(String itemNm, ItemSellStatus itemSellStatus);

	//퀴즈1-2
	List<Item> findByPriceBetween(int price1, int price2);
	
	//퀴즈1-3
	List<Item> findByRegTimeAfter(LocalDateTime regTime);
	
	//퀴즈1-4
	List<Item> findByItemSellStatusNotNull();
	
	//퀴즈 1-5
	List<Item> findByItemDetailLike(String itemDetail);
	
	//퀴즈 1-6
	List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
	
	//퀴즈 1-7
	List<Item> findByPriceLessThanOrderByPriceDesc(int price);
	
	//JPQL(non native 쿼리) -> 컬럼명, 테이블명은 entity 클래스 기준으로 작성한다.
	@Query("select i from Item i where i.itemDetail "
			+ "like %:itemDetail% order by i.price desc")
	List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
	
	
	//JPQL(native 쿼리) -> h2 데이터베이스를 기준으로한 쿼리문 작성
	@Query(value="select * from item where item_detail "
			+ "like %:itemDetail% order by price desc", nativeQuery = true)
	List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
	
	
	//퀴즈 2-1
	@Query("select i from Item i where i.price >= :price")
	List<Item> getPrice(@Param("price") int price); 
	
	//퀴즈 2-2
	@Query("select i from Item i where i.itemNm = :itemNm and"
			+ " i.itemSellStatus = :sell")
	List<Item> getItemNmAndItemSellStatus(@Param("itemNm") String itemNm, 
			@Param("sell") ItemSellStatus sell);
	
	@Query(value = "select data.num num, item.item_id id, item.item_nm itemNm, item.price price, item_img.img_url imgUrl, item_img.repimg_yn repimgYn \r\n"
			+ "            from item \r\n"
			+ "			inner join item_img on (item.item_id = item_img.item_id)\r\n"
			+ "			inner join (select @ROWNUM\\:=@ROWNUM+1 num, groupdata.* from\r\n"
			+ "			            (select order_item.item_id, count(*) cnt\r\n"
			+ "			              from order_item\r\n"
			+ "			              inner join orders on (order_item.order_id = orders.order_id)\r\n"
			+ "			              where orders.order_status = 'ORDER'\r\n"
			+ "			             group by order_item.item_id order by cnt desc) groupdata,\r\n"
			+ "                          (SELECT @ROWNUM\\:=0) R \r\n"
			+ "                          limit 6) data\r\n"
			+ "			on (item.item_id = data.item_id)\r\n"
			+ "			where item_img.repimg_yn = 'Y'\r\n"
			+ "			order by num", nativeQuery = true)
	List<ItemRankDto> getItemRankList();

}
