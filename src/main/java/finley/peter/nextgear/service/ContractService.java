package finley.peter.nextgear.service;

import org.springframework.data.domain.Example;

import finley.peter.nextgear.model.Contract;
import finley.peter.nextgear.model.ContractType;

/**
 * Service for working with {@link Contract}s.
 */
public interface ContractService {

	/**
	 * Find all contracts, optionally filtered by the supplied example instance.
	 * 
	 * @param example the example to filter by
	 * @return all matching contracts
	 */
	Iterable<Contract> findAll(Example<Contract> example);

	/**
	 * Find a single {@link Contract} by ID.
	 * 
	 * @param id
	 * @return
	 */
	Contract findOne(long id);

	/**
	 * Create a new contract. The type can be either EXPRESS or SALES. Express
	 * contracts must have a amount requested less than $50,000 and will be
	 * automatically approved.
	 * 
	 * @param name
	 * @param businessNumber
	 * @param type
	 * @param amountRequested
	 * @return the created {@link Contract} instance
	 */
	Contract create(String name, long businessNumber, ContractType type, int amountRequested);
	
	/**
	 * Update an existing contract.
	 * 
	 * @param contract
	 *            The updated contract properties. Note that only the contract name
	 *            and businessNumber may be changed. The amountRequested may be
	 *            changed if the contract has no status.
	 * @return the updated {@link Contract} instance
	 */
	Contract update(Contract contract);
	
	/**
	 * Delete a contract.
	 * 
	 * @param id
	 */
	void delete(long id);
}
