package by.azgaar.storage.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import by.azgaar.storage.entity.ClickEvent;
import by.azgaar.storage.exception.BadRequestException;
import by.azgaar.storage.repo.ClickCounterRepo;
import by.azgaar.storage.service.ClickCounterServiceInterface;

@Service
public class ClickCounterServiceImpl implements ClickCounterServiceInterface {
	
	private final ClickCounterRepo clickCounterRepo;
	
	@Autowired
	public ClickCounterServiceImpl(final ClickCounterRepo clickCounterRepo) {
		this.clickCounterRepo = clickCounterRepo;
	}
	
	@Override
	@Transactional
	public ClickEvent create() {
		ClickEvent click = new ClickEvent();
		click.setClickTimestamp(Calendar.getInstance());
		return clickCounterRepo.save(click);
	}
	
	@Override
	@Transactional
	public Page<ClickEvent> getAll(String from, String to, Pageable pageable) {
		if (from != null && !from.isBlank() && (to == null || to.isBlank())) {
			try {
				Calendar startDate = parseDate(from);
				Calendar endDate = Calendar.getInstance();
				return clickCounterRepo.findByClickTimestampBetween(startDate, endDate, pageable);
			} catch (ParseException e) {
				throw new BadRequestException("Wrong date format.");
			}
		} else if (from != null && !from.isBlank() && to != null && !to.isBlank()) {
			try {
				Calendar startDate = parseDate(from);
				Calendar endDate = parseDate(to);
				return clickCounterRepo.findByClickTimestampBetween(startDate, endDate, pageable);
			} catch (ParseException e) {
				throw new BadRequestException("Wrong date format.");
			}
		} else {
			return clickCounterRepo.findAll(pageable);
		}
	}
	
	@Override
	@Transactional
	public Long countAll(String from, String to) {
		if (from != null && !from.isBlank() && (to == null || to.isBlank())) {
			try {
				Calendar startDate = parseDate(from);
				Calendar endDate = Calendar.getInstance();
				return clickCounterRepo.countByClickTimestampBetween(startDate, endDate);
			} catch (ParseException e) {
				throw new BadRequestException("Wrong date format.");
			}
		} else if (from != null && !from.isBlank() && to != null && !to.isBlank()) {
			try {
				Calendar startDate = parseDate(from);
				Calendar endDate = parseDate(to);
				return clickCounterRepo.countByClickTimestampBetween(startDate, endDate);
			} catch (ParseException e) {
				throw new BadRequestException("Wrong date format.");
			}
		} else {
			return clickCounterRepo.count();
		}
	}
	
	private Calendar parseDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedDate = sdf.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(parsedDate);
		return cal;
	}
	
}