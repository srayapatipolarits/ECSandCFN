package com.sp.web.account;

import static org.junit.Assert.assertTrue;

import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.repository.user.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MongoAccountRepositoryTest extends SPTestBase {

	@Autowired
	UserRepository repository;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFindByEmailListOfString() {
		List<String> userEmailList = null;
		List<User> userList = repository.findByEmail(userEmailList);
		assertTrue("Should be null for empty list !!!", userList.size() == 0);
	}

}
