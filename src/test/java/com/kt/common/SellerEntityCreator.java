package com.kt.common;

import com.kt.constant.Gender;
import com.kt.domain.entity.SellerEntity;

public class SellerEntityCreator {

    public static SellerEntity createSeller(String email, String password) {
        return SellerEntity.create(
                "판매자1",
                email,
                password,
                "상점1",
                "010-1234-5678",
                "seller@test.com",
                Gender.MALE
        );
    }

    public static SellerEntity createSeller() {
        return SellerEntity.create(
                "판매자1",
                "seller@test.com",
                "1234",
                "상점1",
                "010-1234-5678",
                "seller@test.com",
                Gender.MALE
        );
    }
}
