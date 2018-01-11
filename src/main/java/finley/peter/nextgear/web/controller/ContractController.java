package finley.peter.nextgear.web.controller;

import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import finley.peter.nextgear.model.Contract;
import finley.peter.nextgear.model.ContractStatus;
import finley.peter.nextgear.model.ContractType;
import finley.peter.nextgear.service.ContractService;

/**
 * RESTful services for working with {@link Contract}s.
 */
@RestController
@RequestMapping("/contracts")
public class ContractController {
	
	private ContractService contractService;
	
	public ContractController(ContractService contractService) {
		this.contractService = contractService;
	}

	/**
	 * Get all contracts, optionally filtered by the given {@link ContractStatus}.
	 * 
	 * @param status the status to filter on
	 * @return
	 */
	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Iterable<Contract>> getContracts(@RequestParam(name="status", required=false) ContractStatus status) {
		
		Contract contract = new Contract();
		contract.setStatus(status);
		
		Example<Contract> example = Example.of(contract);
		
		Iterable<Contract> contracts = contractService.findAll(example);
		
		return new ResponseEntity<>(contracts, HttpStatus.OK);
	}

	/**
	 * Get a single contract by id.
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	@GetMapping(path="/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Contract> getContract(@PathVariable long id) throws NotFoundException {
		
		Contract contract = contractService.findOne(id);
		
		if(contract == null) {
			throw new NotFoundException("Contract does not exist.");
		}
		
		return new ResponseEntity<>(contract, HttpStatus.OK);
	}

	/**
	 * Create a new contract. The type can be either EXPRESS or SALES. Express
	 * contracts must have a amount requested less than $50,000 and will be
	 * automatically approved.
	 * 
	 * @param newContract
	 *            The contract parameters. Note that only the name, businessNumber,
	 *            type, and amountRequested properties may be specified.
	 * @return
	 */
	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Contract> createContract(@RequestBody Contract newContract) {
		
		String name = newContract.getName();
		long businessNumber = newContract.getBusinessNumber();
		ContractType type = newContract.getType();
		int amountRequested = newContract.getAmountRequested();

		Contract contract = contractService.create(name, businessNumber, type, amountRequested);
		
		return new ResponseEntity<>(contract, HttpStatus.CREATED);
	}

	/**
	 * Update an existing contract.
	 * 
	 * @param id
	 *            the ID of the contract to update
	 * @param contract
	 *            The updated contract properties. Note that only the contract name
	 *            and businessNumber may be changed. The amountRequested may be
	 *            changed if the contract has no status.
	 */
	@PutMapping(path="/{id}", consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void updateContract(@PathVariable(name="id") long id, @RequestBody Contract contract) {
		
		contract.setId(id);

		contractService.update(contract);
	}

	/**
	 * Delete a contract.
	 * 
	 * @param id the ID of the contract to delete
	 */
	@DeleteMapping(path="/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteContract(@PathVariable long id) {

		contractService.delete(id);
	}
	
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	public HttpEntity<ErrorInformation> notFound( Exception exception ) {
		
		ErrorInformation errorInformation = new ErrorInformation(exception, HttpStatus.NOT_FOUND);
		return new HttpEntity<ErrorInformation>(errorInformation);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	public HttpEntity<ErrorInformation> badRequest( Exception exception ) {
		
		ErrorInformation errorInformation = new ErrorInformation(exception, HttpStatus.BAD_REQUEST);
		return new HttpEntity<ErrorInformation>(errorInformation);
	}
}
