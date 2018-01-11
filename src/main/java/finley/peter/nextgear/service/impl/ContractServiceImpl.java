package finley.peter.nextgear.service.impl;

import java.util.Date;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import finley.peter.nextgear.dao.ContractRepository;
import finley.peter.nextgear.model.Contract;
import finley.peter.nextgear.model.ContractStatus;
import finley.peter.nextgear.model.ContractType;
import finley.peter.nextgear.service.ContractService;

@Service
public class ContractServiceImpl implements ContractService {

	private ContractRepository contractRepository;
	
	private static final int EXPRESS_CONTRACT_AMOUNT_LIMIT = 50000;
	
	public ContractServiceImpl(ContractRepository contractRepository) {
		this.contractRepository = contractRepository;
	}

	@Override
	public Iterable<Contract> findAll(Example<Contract> example) {
		return contractRepository.findAll(example);
	}

	@Override
	public Contract findOne(long id) {
		return contractRepository.findOne(id);
	}

	@Override
	public Contract create(String name, long businessNumber, ContractType type, int amountRequested) {
		
		if(name == null || name.equals("")) {
			throw new IllegalArgumentException("Contract name must be specified");
		}
		
		if(amountRequested < 1) {
			throw new IllegalArgumentException("Contract amount must be greater than 0");
		}
		
		if(type == null) {
			throw new IllegalArgumentException("Contract type must not be null");
		}
		
		// express contracts have an amount limit
		if(type == ContractType.EXPRESS 
				&& amountRequested >= EXPRESS_CONTRACT_AMOUNT_LIMIT ) {
			throw new IllegalArgumentException("EXPRESS contract amounts must be less than "
				+ EXPRESS_CONTRACT_AMOUNT_LIMIT);
		}
		
		Contract contract = new Contract();
		contract.setName(name);
		contract.setBusinessNumber(businessNumber);
		contract.setType(type);
		contract.setAmountRequested(amountRequested);
		
		// automatically approve express contracts
		if(type == ContractType.EXPRESS) {
			contract.setActivationDate(new Date());
			contract.setStatus(ContractStatus.APPROVED);
		}

		return contractRepository.save(contract);
	}

	@Override
	public Contract update(Contract contract) {
		
		long id = contract.getId();
		
		Contract existingContract = contractRepository.findOne(id);
		if(existingContract == null) {
			throw new IllegalArgumentException("Contract does not exist with id: " + id);
		}
		
		existingContract.setName(contract.getName());
		existingContract.setBusinessNumber(contract.getBusinessNumber());

		// if no status allow amount to be changed
		if(existingContract.getStatus() == null) {
			existingContract.setAmountRequested(contract.getAmountRequested());
		}

		// activation date, status, and type are read-only

		return contractRepository.save(existingContract);
	}

	@Override
	public void delete(long id) {
		contractRepository.delete(id);
	}
}
