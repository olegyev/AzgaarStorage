package by.azgaar.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.azgaar.storage.dto.ClickEventDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.ClickEvent;
import by.azgaar.storage.service.ClickCounterServiceInterface;

@RestController
@RequestMapping("/count-click")
public class ClickCounterController {
	
	private final ClickCounterServiceInterface clickCounterService;
	private final DtoAssemblerInterface<ClickEvent, ClickEventDto> assembler;
	private final PagedResourcesAssembler<ClickEvent> pagedResourcesAssembler;
	
	@Autowired
	public ClickCounterController(final ClickCounterServiceInterface clickCounterService,
								  final DtoAssemblerInterface<ClickEvent, ClickEventDto> assembler,
								  PagedResourcesAssembler<ClickEvent> pagedResourcesAssembler) {
		this.clickCounterService = clickCounterService;
		this.assembler = assembler;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
	}
	
	@PostMapping
	public ResponseEntity<ClickEventDto> storeClick() {
		ClickEvent click = clickCounterService.create();
		ClickEventDto dto = assembler.toModel(click);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<PagedModel<ClickEventDto>> getAll(@RequestParam(required = false) String from,
															@RequestParam(required = false) String to,
															@PageableDefault(sort = { "clickTimestamp" }, direction = Sort.Direction.ASC) Pageable defaultPageable) {
		Page<ClickEvent> clicks = clickCounterService.getAll(from, to, defaultPageable);
		PagedModel<ClickEventDto> dto = pagedResourcesAssembler.toModel(clicks, assembler);
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping("/count")
	public ResponseEntity<Long> countAll(@RequestParam(required = false) String from,
										 @RequestParam(required = false) String to) {
		Long clicksAmount = clickCounterService.countAll(from, to);
		return ResponseEntity.ok(clicksAmount);
	}

}