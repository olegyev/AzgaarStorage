package by.azgaar.storage.repo;

import java.util.Calendar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.azgaar.storage.entity.ClickEvent;

@Repository
public interface ClickCounterRepo extends JpaRepository<ClickEvent, Long> {
	
	Page<ClickEvent> findAll(Pageable pageable);
	
	Page<ClickEvent> findByClickTimestampBetween(Calendar from, Calendar to, Pageable pageable);
	
	long count();
	
	long countByClickTimestampBetween(Calendar from, Calendar to);
}