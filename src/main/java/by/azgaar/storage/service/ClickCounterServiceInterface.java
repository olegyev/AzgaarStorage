package by.azgaar.storage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import by.azgaar.storage.entity.ClickEvent;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

public interface ClickCounterServiceInterface {
	
	ClickEvent create();
	
	Page<ClickEvent> getAll(String from, String to, Pageable pageable);
	
	Long countAll(String from, String to);

}