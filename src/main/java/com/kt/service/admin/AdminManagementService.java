package com.kt.service.admin;

import com.kt.domain.dto.request.AdminRequest;
import com.kt.domain.dto.response.AdminResponse;

public interface AdminManagementService {

	AdminResponse.Detail detail();

	void update(AdminRequest.Update request);

}
