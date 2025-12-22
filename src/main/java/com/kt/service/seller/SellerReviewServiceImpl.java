package com.kt.service.seller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.SellerReviewResponse;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.util.Preconditions;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerReviewServiceImpl implements SellerReviewService {

	private final ReviewRepository reviewRepository;
	private final ProductRepository productRepository;

	@Override
	public Page<SellerReviewResponse.Search> getAllReviews(Pageable pageable, UUID sellerId) {
		return reviewRepository.searchReviewsForSeller(pageable, sellerId, null);
	}

	@Override
	public Page<SellerReviewResponse.Search> getReviewsByProduct(Pageable pageable, UUID sellerId, UUID productId) {
		checkSellerId(sellerId, productId);
		return reviewRepository.searchReviewsForSeller(pageable, sellerId, productId);
	}

	private void checkSellerId(UUID sellerId, UUID productId) {
		Preconditions.validate(productRepository.findByIdOrThrow(productId).getSeller().getId().equals(sellerId),
			ErrorCode.PRODUCT_NOT_OWNER);
	}
}

