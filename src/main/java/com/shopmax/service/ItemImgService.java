package com.shopmax.service;

import com.shopmax.entity.ItemImg;
import com.shopmax.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

	@Value("${itemImgLocation}") // C:/shop/item
	private String itemImgLocation;
	
	private final ItemImgRepository itemImgRepository;
	private final FileService fileService;

	/*
	 * 이미지 저장
	 * 1. 파일을 itemImgLocation에 저장(서버에 저장) -> FileService를 이용
	 * 2. item_img 테이블에 이미지 정보 insert
	 */
	public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
		String oriImgName = itemImgFile.getOriginalFilename(); // 파일이름 -> 이미지1.jpg
		String imgName = "";
		String imgUrl = "";

		if(!StringUtils.isEmpty(oriImgName)) { //빈문자열인지 아닌지 검사
			//oriImgName 빈문자열이 아니라면 이미지 파일 업로드 진행
			imgName = fileService.uploadFile(itemImgLocation,
					oriImgName, itemImgFile.getBytes());
			//itemImgFile.getBytes(): 이미지 파일을 byte배열로 만들어준다(스트림에서 사용하기 위해)

			imgUrl = "/images/item/" + imgName; // /images/item/EERERERT-ERERERER-CDFDFGD.jpg
		}

		//DB에 insert를 하기전 유저가 직접 입력하지 못하는 값들은 개발자가 넣어준다
		itemImg.updateItemImg(oriImgName, imgName, imgUrl);
		itemImgRepository.save(itemImg); //insert
	}
	
	//이미지 업데이트 메소드
	public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) 
			throws Exception {
		if(!itemImgFile.isEmpty()) { //첨부한 이미지 파일이 있으면

			//1. 서버에 있는 이미지를 가지고 와서 수정해준다.
			//해당 이미지를 가져오고
			ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
					                     .orElseThrow(EntityNotFoundException::new);
			
			//기존 이미지 파일 C:/shop/item 폴더에서 삭제
			if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
				fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
			}
			
			//수정된 이미지 파일 C:/shop/item 에 업로드
			String oriImgName = itemImgFile.getOriginalFilename(); //이미지1.jpg
			String imgName = fileService.uploadFile(itemImgLocation, oriImgName
					, itemImgFile.getBytes());
			String imgUrl = "/images/item/" + imgName; // /images/item/EERERERT-ERERERER-CDFDFGD.jpg

			//2. item_img 테이블에 저장된 데이터를 수정해준다.
			//update (JPA가 자동감지)
			savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
		}
	}
}















