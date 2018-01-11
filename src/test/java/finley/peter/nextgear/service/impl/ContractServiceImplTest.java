package finley.peter.nextgear.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;

import finley.peter.nextgear.dao.ContractRepository;
import finley.peter.nextgear.model.Contract;
import finley.peter.nextgear.model.ContractStatus;
import finley.peter.nextgear.model.ContractType;

/**
 * Unit tests for {@link ContractServiceImpl}.
 */
public class ContractServiceImplTest {
	
	@Mock
	private ContractRepository contractRepository;

	@InjectMocks
	private ContractServiceImpl contractService;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindAll() {
		
		Example<Contract> example = Example.of(new Contract());
		
		List<Contract> contracts = new ArrayList<>();

		when(contractRepository.findAll(same(example))).thenReturn(contracts);

		Iterable<Contract> result = contractService.findAll(example);
		
		assertThat(result).isSameAs(contracts);
	}
	
	@Test
	public void testFindOne() {
		
		long id = 1;
		Contract contract = new Contract();

		when(contractRepository.findOne(id)).thenReturn(contract);

		Contract result = contractService.findOne(id);
		
		assertThat(result).isSameAs(contract);
	}
	
	/**
	 * Contract name must not be null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateWithNullName() {
		
		String name = null;
		int businessNumber = 1;
		ContractType type = ContractType.EXPRESS;
		int amountRequested = 10;

		try {
			contractService.create(name, businessNumber, type, amountRequested);
		} finally {
			verify(contractRepository, never()).save(any(Contract.class));
		}
	}

	/**
	 * Contract name must not be empty string.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateWithNameEmpty() {
		
		String name = "";
		int businessNumber = 1;
		ContractType type = ContractType.EXPRESS;
		int amountRequested = 10;

		try {
			contractService.create(name, businessNumber, type, amountRequested);
		} finally {
			verify(contractRepository, never()).save(any(Contract.class));
		}
	}
	
	/**
	 * Contract amount must be >0.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateContractAmountTooSmall() {

		String name = "name";
		int businessNumber = 1;
		ContractType type = ContractType.EXPRESS;
		int amountRequested = 0;

		try {
			contractService.create(name, businessNumber, type, amountRequested);
		} finally {
			verify(contractRepository, never()).save(any(Contract.class));
		}
	}
	
	/**
	 * Express contract amount must be less than $50,000.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateExpressContractAmountTooLarge() {
		
		String name = "name";
		int businessNumber = 1;
		ContractType type = ContractType.EXPRESS;
		int amountRequested = 50000;

		try {
			contractService.create(name, businessNumber, type, amountRequested);
		} finally {
			verify(contractRepository, never()).save(any(Contract.class));
		}
	}
	
	/**
	 * Sales contract amount may be $50,000 or more.
	 */
	@Test
	public void testCreateSalesContractAmountLargerThanExpressLimit() {
		
		String name = "name";
		int businessNumber = 1;
		ContractType type = ContractType.SALES;
		int amountRequested = 50000;
		
		Contract expectedContract = new Contract(); // values irrelevant for test
		
		ArgumentCaptor<Contract> savedContractArgument = ArgumentCaptor.forClass(Contract.class);
		when(contractRepository.save(savedContractArgument.capture())).thenReturn(expectedContract);

		Contract result = contractService.create(name, businessNumber, type, amountRequested);
		
		Contract savedContract = savedContractArgument.getValue();
		assertThat(savedContract.getId()).isNull();
		assertThat(savedContract.getAmountRequested()).isEqualTo(amountRequested);
		assertThat(savedContract.getName()).isEqualTo(name);
		assertThat(savedContract.getBusinessNumber()).isEqualTo(businessNumber);
		assertThat(savedContract.getType()).isEqualTo(type);
		assertThat(savedContract.getActivationDate()).isNull();
		assertThat(savedContract.getStatus()).isNull();
		
		assertThat(result).isSameAs(expectedContract);
	}
	
	/**
	 * Express contract should be automatically approved and activated date set.
	 */
	@Test
	public void testCreateExpressContract() {
		
		String name = "name";
		int businessNumber = 1;
		ContractType type = ContractType.EXPRESS;
		int amountRequested = 1000;
		
		Contract expectedContract = new Contract(); // values irrelevant for test
		
		ArgumentCaptor<Contract> savedContractArgument = ArgumentCaptor.forClass(Contract.class);
		when(contractRepository.save(savedContractArgument.capture())).thenReturn(expectedContract);

		Contract result = contractService.create(name, businessNumber, type, amountRequested);
		
		Contract savedContract = savedContractArgument.getValue();
		assertThat(savedContract.getId()).isNull();
		assertThat(savedContract.getAmountRequested()).isEqualTo(amountRequested);
		assertThat(savedContract.getName()).isEqualTo(name);
		assertThat(savedContract.getBusinessNumber()).isEqualTo(businessNumber);
		assertThat(savedContract.getType()).isEqualTo(type);
		assertThat(savedContract.getActivationDate()).isEqualToIgnoringSeconds(new Date());
		assertThat(savedContract.getStatus()).isEqualTo(ContractStatus.APPROVED);
		
		assertThat(result).isSameAs(expectedContract);
	}
	
	/**
	 * Test that an exception is thrown when updating a contract that doesn't exist.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateNonExisting() {
		
		long id = 1;

		Contract contract = new Contract();
		contract.setId(id);

		when(contractRepository.findOne(id)).thenReturn(null);
		
		try {
			contractService.update(contract);
		} finally {
			verify(contractRepository, never()).save(any(Contract.class));
		}
	}

	/**
	 * Test {@link ContractServiceImpl#update(Contract)} ensuring read-only
	 * properties can't be changed.
	 */
	@Test
	public void testUpdateExisting() {

		long id = 1;

		Contract updatedContract = new Contract();
		updatedContract.setId(id);
		updatedContract.setActivationDate(new Date());
		updatedContract.setAmountRequested(1000);
		updatedContract.setBusinessNumber(9);
		updatedContract.setName("New name");
		updatedContract.setStatus(ContractStatus.APPROVED);
		updatedContract.setType(ContractType.EXPRESS);
		
		Date existingActivationDate = new Date();
		int existingAmountRequested = 100;
		ContractStatus existingStatus = ContractStatus.DENIED;
		ContractType existingType = ContractType.SALES;
		
		Contract existingContract = new Contract();
		existingContract.setId(id);
		existingContract.setActivationDate(existingActivationDate);
		existingContract.setAmountRequested(existingAmountRequested);
		existingContract.setBusinessNumber(1);
		existingContract.setName("name");
		existingContract.setStatus(existingStatus);
		existingContract.setType(existingType);

		when(contractRepository.findOne(id)).thenReturn(existingContract);
		when(contractRepository.save(same(existingContract))).thenReturn(existingContract);
		
		Contract result = contractService.update(updatedContract);
		
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getActivationDate()).isSameAs(existingActivationDate);
		assertThat(result.getAmountRequested()).isEqualTo(existingAmountRequested);
		assertThat(result.getBusinessNumber()).isEqualTo(updatedContract.getBusinessNumber());
		assertThat(result.getName()).isEqualTo(updatedContract.getName());
		assertThat(result.getStatus()).isSameAs(existingStatus);
		assertThat(result.getType()).isSameAs(existingType);

		verify(contractRepository, never()).save(same(updatedContract));
	}

	/**
	 * Test {@link ContractServiceImpl#update(Contract)} when the existing contract
	 * has no status yet, thus allowing the requested amount to be changed. Ensure
	 * read-only attributes still can't be changed.
	 */
	@Test
	public void testUpdateExistingWithNoStatus() {

		long id = 1;

		Contract updatedContract = new Contract();
		updatedContract.setId(id);
		updatedContract.setActivationDate(new Date());
		updatedContract.setAmountRequested(1000);
		updatedContract.setBusinessNumber(9);
		updatedContract.setName("New name");
		updatedContract.setStatus(ContractStatus.APPROVED);
		updatedContract.setType(ContractType.EXPRESS);
		
		ContractType existingType = ContractType.SALES;
		
		Contract existingContract = new Contract();
		existingContract.setId(id);
		existingContract.setAmountRequested(100);
		existingContract.setBusinessNumber(1);
		existingContract.setName("name");
		existingContract.setType(existingType);

		when(contractRepository.findOne(id)).thenReturn(existingContract);
		when(contractRepository.save(same(existingContract))).thenReturn(existingContract);
		
		Contract result = contractService.update(updatedContract);
		
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getActivationDate()).isNull();
		assertThat(result.getAmountRequested()).isEqualTo(updatedContract.getAmountRequested());
		assertThat(result.getBusinessNumber()).isEqualTo(updatedContract.getBusinessNumber());
		assertThat(result.getName()).isEqualTo(updatedContract.getName());
		assertThat(result.getStatus()).isNull();
		assertThat(result.getType()).isSameAs(existingType);

		verify(contractRepository, never()).save(same(updatedContract));
	}
	
	@Test
	public void testDelete() {
		
		long id = 1;

		contractService.delete(id);
		
		verify(contractRepository).delete(id);
	}
}
