package com.sp.web.service.email;

import org.springframework.mail.MailMessage;

public interface SPTransformer {

	MailMessage transformMessage(MessageParams messageParams);
}
